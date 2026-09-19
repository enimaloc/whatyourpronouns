plugins {
    id("dev.kikugie.stonecutter")
}

// The "active" node is the one the IDE indexes, and the one project-unprefixed tasks
// (./gradlew build, ./gradlew runClient) target by default. Change it with:
//   ./gradlew "1.21.1-fabric:stonecutterSwitchTo1.21.1-fabric"
stonecutter active "1.20.1-fabric"
