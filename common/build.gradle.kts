plugins {
    id("crazycrates-base")
}

dependencies {
    implementation(libs.yaml)

    implementation(libs.kyori.api)
    implementation(libs.mini.message)

    implementation(libs.guice)

    // Apache Commons - forgot what I used this for.
    // implementation(libs.apache.commons)
}