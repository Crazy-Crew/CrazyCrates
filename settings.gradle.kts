dependencyResolutionManagement {
    includeBuild("build-logic")
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")

        maven("https://maven.fabricmc.net")
    }
}

// Project Name!
rootProject.name = "Crazy-Crates"

include("paper", "common")

enableFeaturePreview("VERSION_CATALOGS")