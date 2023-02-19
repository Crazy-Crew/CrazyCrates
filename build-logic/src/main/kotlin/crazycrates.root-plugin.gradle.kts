import task.ReleaseWebhook
import task.WebhookExtension

plugins {
    id("crazycrates.base-plugin")
}

repositories {
    maven("https://repo.triumphteam.dev/snapshots/")

    maven("https://repo.crazycrew.us/plugins/")

    maven("https://libraries.minecraft.net/")

    maven("https://jitpack.io/")

    /**
     * CrazyCrew Team
     */
    maven("https://repo.crazycrew.us/libraries/")

    mavenCentral()

    mavenLocal()
}

tasks {
    // Creating the extension to be available on the root gradle
    val webhookExtension = extensions.create("webhook", WebhookExtension::class)

    // Register the task
    register<ReleaseWebhook>("webhook") {
        extension = webhookExtension
    }
}