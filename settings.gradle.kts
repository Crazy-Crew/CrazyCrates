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
    "common",
).forEach {
    include(it)
}