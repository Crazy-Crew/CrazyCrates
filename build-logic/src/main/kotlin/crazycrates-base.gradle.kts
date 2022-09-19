plugins {
    id("crazycrates-language")
}

rootProject.group = "com.badbones69.crazycrates"
rootProject.version = "2.11.6"
rootProject.description = "The best crates plugin!"

repositories {
    mavenCentral()

    maven("https://jitpack.io")
}