pluginManagement {
    repositories {
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        maven("https://maven.architectury.dev/")
        maven("https://maven.minecraftforge.net/")
        maven("https://maven.neoforged.net/releases/")
        gradlePluginPortal()
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.7"
    // Auto-provisions a missing JDK (the Gradle daemon must run on the newest Java version
    // required by any node) instead of requiring a manual install.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

// Flat Architectury layout: no separate :common subproject. Each node (Minecraft version +
// loader) is a standalone, complete Gradle project; shared code and loader-specific code live in
// the SAME source tree (src/), distinguished by //? if fabric/neoforge markers rather than an
// inter-project dependency. See docs/superpowers/specs/2026-09-19-architectury-stonecutter-migration-design.md.
stonecutter {
    create(rootProject) {
        versions(
            "1.20.1-fabric" to "1.20.1",
            "1.21.1-fabric" to "1.21.1",
            "1.21.1-neoforge" to "1.21.1",
        )
    }
}

rootProject.name = "whatyourpronouns"
