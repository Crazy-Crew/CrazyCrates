pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")

        maven("https://maven.minecraftforge.net/")

        maven("https://repo.papermc.io/repository/maven-public/")

        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "CrazyCrates"

listOf(
    "api",

    // Bukkit
    "paper",

    // Fabric
    "fabric"
).forEach {
    include(it)
}