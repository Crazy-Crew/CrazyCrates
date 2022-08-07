dependencyResolutionManagement {
    includeBuild("build-logic")
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

// Project Name!
rootProject.name = "Crazy-Crates"

include("paper", "api", "common")

enableFeaturePreview("VERSION_CATALOGS")