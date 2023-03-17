import task.ReleaseWebhook
import task.WebhookExtension

plugins {
    `java-library`
    `maven-publish`

    id("com.github.johnrengelman.shadow")

    kotlin("plugin.serialization")
    kotlin("jvm")
}

repositories {
    maven("https://repo.triumphteam.dev/snapshots/")

    maven("https://repo.crazycrew.us/libraries/")

    maven("https://repo.crazycrew.us/plugins/")

    maven("https://libraries.minecraft.net/")

    maven("https://jitpack.io/")

    mavenCentral()
    mavenLocal()
}

dependencies {
    compileOnly(kotlin("stdlib"))
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of("17"))
}

kotlin {
    jvmToolchain(17)
}

tasks {
    // Creating the extension to be available on the root gradle
    val webhookExtension = extensions.create("webhook", WebhookExtension::class)

    // Register the task
    register<ReleaseWebhook>("webhook") {
        extension = webhookExtension
    }

    compileJava {
        options.release.set(17)
    }

    compileKotlin {
        kotlinOptions {
            jvmTarget = "17"
            javaParameters = true
        }
    }

    shadowJar {
        archiveClassifier.set("")
    }
}