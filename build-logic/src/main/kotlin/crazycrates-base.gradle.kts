plugins {
    id("crazycrates-language")
}

group = "com.badbones69.crazycrates"
version = "1.0.0-${System.getenv("BUILD_NUMBER") ?: "SNAPSHOT"}"
description = "The best crates plugin!"

repositories {
    mavenCentral()

    maven("https://jitpack.io")
}