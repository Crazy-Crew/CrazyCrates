plugins {
    alias(libs.plugins.shadowJar)

    `java-plugin`
}

dependencies {
    compileOnly(libs.vital.core)

    api(libs.crazycrates)
}