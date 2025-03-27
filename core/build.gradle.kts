plugins {
    id("root-plugin")
}

dependencies {
    compileOnly(libs.fusion.core)
    compileOnly(libs.fusion.api)

    compileOnly(libs.jetbrains)

    api(project(":crazycrates-api"))

    api(libs.jalu)
}