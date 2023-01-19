dependencyResolutionManagement {
    includeBuild("build-logic")
}

pluginManagement {
    repositories {
        maven("https://papermc.io/repo/repository/maven-public/")

        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        id("com.modrinth.minotaur") version "2.6.0"
        id("xyz.jpenilla.run-paper") version "2.0.0"
    }
}

include("core")
project(":core").name = "${rootProject.name.toLowerCase()}-core"

listOf("paper").forEach(::includePlatform)

fun includeProject(name: String) {
    include(name) {
        this.name = "${rootProject.name.toLowerCase()}-$name"
    }
}

fun includePlatform(name: String) {
    include(name) {
        this.name = "${rootProject.name.toLowerCase()}-platform-$name"
        this.projectDir = file("platforms/$name")
    }
}

fun includeModule(name: String) {
    include(name) {
        this.name = "${rootProject.name.toLowerCase()}-module-$name"
        this.projectDir = file("modules/$name")
    }
}

fun includePlatformModule(name: String, platform: String) {
    include(name) {
        this.name = "${rootProject.name.toLowerCase()}-module-$platform-$name"
        this.projectDir = file("modules/$platform/$name")
    }
}

fun include(name: String, block: ProjectDescriptor.() -> Unit) {
    include(name)
    project(":$name").apply(block)
}