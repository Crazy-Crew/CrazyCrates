import task.ReleaseWebhook
import task.WebhookExtension

plugins {
    `java-library`
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(project.extra["java_version"].toString()))
}

tasks {
    // Creating the extension to be available on the root gradle
    val webhookExtension = extensions.create("webhook", WebhookExtension::class)

    // Register the task
    register<ReleaseWebhook>("releaseWebhook") {
        extension = webhookExtension
    }

    compileJava {
        options.release.set(project.extra["java_version"].toString().toInt())
    }
}