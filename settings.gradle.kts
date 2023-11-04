pluginManagement {
    repositories {
        maven("https://repo.papermc.io/repository/maven-public/")

        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "CrazyCrates"

listOf(
    // v2
    "api",
    "common",

    "paper",

    // v1
    "core",
).forEach {
    include(it)
}