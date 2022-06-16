rootProject.name = "Crazy-Crates"

pluginManagement {
    repositories {
        gradlePluginPortal()

        maven("https://papermc.io/repo/repository/maven-public/")
    }
}

include("plugin", "api")