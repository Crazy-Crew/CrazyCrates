plugins {
    id("root-plugin")
}

repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    maven("https://repo.papermc.io/repository/maven-public/")

    maven("https://repo.codemc.io/repository/maven-public/")

    maven("https://repo.triumphteam.dev/snapshots/")

    maven("https://repo.oraxen.com/releases/")

    flatDir { dirs("libs") }
}

project.version = if (System.getenv("BUILD_NUMBER") != null) "${rootProject.version}-${System.getenv("BUILD_NUMBER")}" else rootProject.version

tasks {
    modrinth {
        loaders.addAll("paper", "purpur")
    }
}