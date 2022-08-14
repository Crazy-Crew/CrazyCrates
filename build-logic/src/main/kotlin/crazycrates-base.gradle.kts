plugins {
    id("crazycrates-language")
}

rootProject.group = "com.badbones69.crazycrates.CrazyCrates"
rootProject.version = "2.11.6-${System.getenv("BUILD_NUMBER") ?: "SNAPSHOT"}"
rootProject.description = "The best crates plugin!"

repositories {
    mavenCentral()

    maven("https://jitpack.io")
}