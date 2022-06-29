plugins {
    `java-library`

    kotlin("jvm")
}

group = "com.badbones69.crazycrates"
version = "1.11.5-${System.getenv("BUILD_NUMBER") ?: "SNAPSHOT"}"
description = "Quality crates for free!"

repositories {
    mavenCentral()

    maven("https://repo.badbones69.com/releases/")

    maven("https://jitpack.io/")
}

dependencies {
    // compileOnly("org.apache.commons:commons-text:1.9")
}

tasks {

}