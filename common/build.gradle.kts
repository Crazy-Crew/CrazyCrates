plugins {
    id("crazycrates-base")
}

dependencies {
    implementation(libs.yaml)

    implementation(libs.kyori.api)
    implementation(libs.mini.message)
}