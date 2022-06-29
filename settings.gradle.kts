dependencyResolutionManagement {
    includeBuild("build-logic")
    repositories.gradlePluginPortal()
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}

rootProject.name = "Crazy-Crates"

include("api", "paper", "common", "sponge")

enableFeaturePreview("VERSION_CATALOGS")