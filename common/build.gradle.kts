plugins {
    id("crazycrates-base")
}

dependencies {
    implementation(libs.yaml)

    implementation(libs.kyori.api)
    implementation(libs.mini.message)

    // Apache Commons - forgot what I used this for.
    // implementation(libs.apache.commons)
}