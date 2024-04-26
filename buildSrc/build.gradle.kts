plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("io.papermc.paperweight", "paperweight-userdev", "1.6.0")

    implementation("com.github.johnrengelman", "shadow", "8.1.1")
}