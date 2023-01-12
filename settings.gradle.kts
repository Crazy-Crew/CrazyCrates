dependencyResolutionManagement {
    includeBuild("build-logic")
}

pluginManagement {
    repositories {
        maven("https://papermc.io/repo/repository/maven-public/")
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        id("com.modrinth.minotaur") version "2.6.0"
        id("com.github.johnrengelman.shadow") version "7.1.2"
    }
}

include("paper", "common")