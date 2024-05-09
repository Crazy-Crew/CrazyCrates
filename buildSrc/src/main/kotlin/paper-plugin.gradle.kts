plugins {
    id("io.papermc.paperweight.userdev")

    id("java-plugin")
}

repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    maven("https://repo.papermc.io/repository/maven-public/")

    maven("https://repo.triumphteam.dev/snapshots/")

    maven("https://repo.oraxen.com/releases/")
}

dependencies {
    paperweight.paperDevBundle("1.20.6-R0.1-SNAPSHOT")
}