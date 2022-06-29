plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.0")
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "17"
}