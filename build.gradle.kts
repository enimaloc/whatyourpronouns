import net.fabricmc.loom.api.fabricapi.FabricApiExtension

plugins {
    id("architectury-plugin") version "3.5-SNAPSHOT"
    id("dev.architectury.loom") version "1.17-SNAPSHOT"
    id("java")
    id("maven-publish")
    id("com.modrinth.minotaur") version "2.9.0"
}

// "loom.platform" comes from the node's gradle.properties; absent means fabric by default.
val loader = (project.findProperty("loom.platform") as String? ?: "fabric").lowercase()

// Declares the //? if fabric / //? if neoforge constants for shared code (not used by any file
// yet, but wired so it's available the moment a genuinely loader-specific need shows up).
stonecutter.constants.match(loader, "fabric", "neoforge")

base {
    archivesName.set("${rootProject.property("archives_base_name")}-$loader")
}

version = "${rootProject.property("mod_version")}+${project.property("minecraft_version")}"
group = rootProject.property("maven_group") as String

// Depends on the Minecraft version, not the loader: 1.20.1 needs Java 17, 1.21.1 needs Java 21.
val javaVersion = (project.findProperty("java_version") as String? ?: "21").toInt()

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(javaVersion)
}

java {
    withSourcesJar()
}

architectury {
    minecraft = project.property("minecraft_version") as String
    platformSetupLoomIde()
    if (loader == "fabric") fabric() else neoForge()
}

loom {
    // No splitEnvironmentSourceSets(): a single main sourceSet, shared between common and
    // client-only code (flat Architectury layout).
    if (loader == "fabric") {
        mods {
            create(rootProject.property("archives_base_name").toString().lowercase()) {
                sourceSet(sourceSets["main"])
            }
        }
    }

    runs {
        named("client") {
            runDir("run/client")
        }
        named("server") {
            runDir("run/server")
        }
    }
}

if (loader == "fabric") {
    project.extensions.getByType(FabricApiExtension::class.java).apply {
        configureDataGeneration {
            client = true
        }
    }
}

// Classes specific to the other loader don't compile here; excluded from both the
// SourceDirectorySet and stonecutter.filters. Both are required, neither alone suffices.
val excludedPlatform = if (loader == "fabric") "neoforge" else "fabric"
val basePackagePath = (rootProject.property("mod_package") as String).replace(".", "/")
sourceSets["main"].java.exclude("$basePackagePath/platforms/$excludedPlatform/**")
stonecutter.filters.exclude("java/$basePackagePath/platforms/$excludedPlatform/**")

// Only one of fabric.mod.json / neoforge.mods.toml should end up in this node's jar.
sourceSets["main"].resources.exclude(
    if (loader == "fabric") "META-INF/neoforge.mods.toml" else "fabric.mod.json"
)

// Without this call, Stonecutter creates no task to process //? if markers for this sourceSet.
stonecutter.tasks.configureSource(sourceSets["main"])

repositories {
    maven("https://maven.fabricmc.net/")
    maven("https://maven.architectury.dev/")
    maven("https://maven.neoforged.net/releases/")
}

dependencies {
    "minecraft"("com.mojang:minecraft:${project.property("minecraft_version")}")

    // Official Mojang mappings, not Yarn: one naming vocabulary across every Minecraft version
    // of shared source code.
    "mappings"(loom.officialMojangMappings())

    if (loader == "fabric") {
        "modImplementation"("net.fabricmc:fabric-loader:${project.property("loader_version")}")
        "modImplementation"("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")
    } else {
        "neoForge"("net.neoforged:neoforge:${project.property("neoforge_version")}")
    }
}

// fabric.mod.json / neoforge.mods.toml are shared between nodes but contain ${...} tokens whose
// value depends on the node, substituted here via plain Gradle expand().
tasks.processResources {
    if (loader == "fabric") {
        filesMatching("fabric.mod.json") {
            expand(
                mapOf(
                    "version" to project.version,
                    "minecraft_version_range" to project.property("minecraft_version_range"),
                    "loader_version" to project.property("loader_version")
                )
            )
        }
    } else {
        filesMatching("META-INF/neoforge.mods.toml") {
            expand(
                mapOf(
                    "version" to project.version,
                    "mc_range_lower" to project.property("mc_range_lower"),
                    "mc_range_upper" to project.property("mc_range_upper"),
                    "neoforge_version" to project.property("neoforge_version"),
                    "neo_loader_version_range" to project.property("neo_loader_version_range")
                )
            )
        }
    }
}

tasks.jar {
    from(rootProject.file("COPYING")) {
        rename { "${it}_${project.name}" }
    }
    from(rootProject.file("COPYING.LESSER")) {
        rename { "${it}_${project.name}" }
    }
}

publishing {
    repositories {
        // Add a Maven repository here if you also want to publish somewhere other than Modrinth.
    }
    publications {
        create<MavenPublication>("maven${loader.replaceFirstChar { it.uppercase() }}") {
            artifactId = base.archivesName.get()
            from(components["java"])
        }
    }
}

// Modrinth publishing, disabled by default (missing MODRINTH_TOKEN makes the "modrinth" task
// fail if invoked).
modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("whatyourpronouns") // https://modrinth.com/mod/whatyourpronouns
    // versionNumber must stay unique per node (mc+loader) to avoid collisions between two nodes
    // sharing the same minecraft_version (1.21.1-fabric and 1.21.1-neoforge).
    versionNumber.set("${rootProject.property("mod_version")}+${project.property("minecraft_version")}-$loader")
    versionName.set(rootProject.property("mod_version") as String)
    versionType.set("release")
    changelog.set(System.getenv("CHANGELOG") ?: "See the commits for changelog details.")
    uploadFile.set(tasks.named("remapJar"))
    gameVersions.set(listOf(project.property("minecraft_version") as String))
    loaders.set(listOf(loader))
}
