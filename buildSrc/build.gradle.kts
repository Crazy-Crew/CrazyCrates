plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()

    maven("https://repo.crazycrew.us/first-party/")

    maven("https://repo.crazycrew.us/third-party/")

    maven("https://repo.crazycrew.us/releases/")
}

dependencies {
    implementation(libs.paperweight)
    implementation(libs.featherweight)

    implementation(libs.minotaur)
    //implementation(libs.hangar)

    implementation(libs.shadow)

    implementation(libs.turtle)
}