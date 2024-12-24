plugins {
    id("crates.base")

    alias(libs.plugins.shadow)
}

dependencies {
    implementation(projects.crazycratesApi)

    compileOnly(libs.paper)
}