import com.ryderbelserion.feather.libs

rootProject.name = "buildSrc"

dependencyResolutionManagement {
    repositories {
        maven("https://repo.crazycrew.us/releases")

        gradlePluginPortal()

        mavenCentral()
    }

    versionCatalogs {
        register("libs") {
            from(files(libs))
        }
    }
}

pluginManagement {
    repositories {
        maven("https://repo.crazycrew.us/releases")

        gradlePluginPortal()
    }
}

plugins {
    id("com.ryderbelserion.feather-settings")
}