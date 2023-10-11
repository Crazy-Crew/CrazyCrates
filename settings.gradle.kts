pluginManagement {
    repositories {
        maven("https://repo.papermc.io/repository/maven-public/")

        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "CrazyCrates"

listOf(
    "api",
    "paper",
    "old",
    "common",
).forEach {
    include(it)
}