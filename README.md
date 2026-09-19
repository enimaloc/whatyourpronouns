# WhatYourPronouns

## Setup

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

## License

This project is licensed under the GNU Lesser General Public License v3.0 or later (LGPL-3.0-or-later). See [COPYING](COPYING) and [COPYING.LESSER](COPYING.LESSER) for the full text.
