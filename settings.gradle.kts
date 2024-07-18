rootProject.name = "CrazyCrates"

pluginManagement {
    repositories {
        maven("https://repo.crazycrew.us/releases")

        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"

    id("com.ryderbelserion.feather-settings") version "0.0.4"
}

include("api")