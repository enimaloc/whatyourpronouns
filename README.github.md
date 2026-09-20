<!-- MODRINTH:README -->

## Development

This project uses [Architectury](https://docs.architectury.dev/) and [Stonecutter](https://stonecutter.kikugie.dev/wiki/v2) to support multiple Minecraft versions and loaders (Fabric + NeoForge) from one shared source tree.

```bash
# Build every node (1.20.1-fabric, 1.21.1-fabric, 1.21.1-neoforge)
./gradlew build

# Run the client for the active node (1.20.1-fabric by default, see stonecutter.gradle.kts)
./gradlew ":1.20.1-fabric:runClient"

# Run a specific node without making it active
./gradlew ":1.21.1-fabric:runClient"
./gradlew ":1.21.1-neoforge:runServer"

# List all declared nodes
./gradlew projects
```

IntelliJ IDEA with the [Stonecutter plugin](https://plugins.jetbrains.com/plugin/24608-stonecutter) is recommended: it generates Run Configurations per node and lets you switch the active node from the toolbar.

### Releasing

Publishing a [GitHub Release](https://github.com/enimaloc/whatyourpronouns/releases) triggers [`gradle-publish.yml`](https://github.com/enimaloc/whatyourpronouns/blob/master/.github/workflows/gradle-publish.yml), which builds every node, attaches the jars to the Release, and publishes each of them to [Modrinth](https://modrinth.com/mod/whatyourpronouns) via the `modrinth` Gradle task (configured in [`build.gradle.kts`](https://github.com/enimaloc/whatyourpronouns/blob/master/build.gradle.kts)). This also syncs [`README.modrinth.md`](README.modrinth.md) as the Modrinth project description (`syncBodyFrom`). Publishing requires a `MODRINTH_TOKEN` secret on the repository; without it the Modrinth publish step fails but doesn't block the Release, since the jars are already attached by the previous step.

### README

This repository keeps its README in three files:

- [`README.modrinth.md`](README.modrinth.md) — user-facing content, also synced as the Modrinth project description on release.
- [`README.github.md`](README.github.md) — this file: a base containing the `<!-- MODRINTH:README -->` placeholder plus the development-only sections above.
- `README.md` — generated, do not edit by hand. [`generate-readme.yml`](https://github.com/enimaloc/whatyourpronouns/blob/master/.github/workflows/generate-readme.yml) regenerates and commits it automatically whenever either source file above changes on `master`; run `./gradlew generateReadme` locally if you need it up to date before that.
