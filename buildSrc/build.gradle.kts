plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()

    maven("https://repo.crazycrew.us/api/")
}

dependencies {
    implementation(libs.paperweight)
    implementation(libs.featherweight)

    implementation(libs.minotaur)
    //implementation(libs.hangar)

    implementation(libs.shadow)

    implementation(libs.turtle)
}