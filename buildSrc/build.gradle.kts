plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(libs.shadow)
    implementation(libs.paperweight)

    implementation(libs.kotlin)
    implementation(libs.kotlin.serialization)

    // For the webhook tasks, this applies to the buildSrc only

    implementation(libs.ktor.core)
    implementation(libs.ktor.cio)
    implementation(libs.ktor.content)
    implementation(libs.ktor.gson)

    implementation(libs.kotlin.coroutines)
}