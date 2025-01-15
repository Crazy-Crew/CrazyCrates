plugins {
    id("paper-plugin")

    alias(libs.plugins.shadow)
}

dependencies {
    implementation(project(":crazycrates-api"))

    compileOnly(libs.paper)
}