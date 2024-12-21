plugins {
    alias(libs.plugins.shadow)

    id("crates.base")
}

dependencies {
    implementation(projects.crazycratesApi)

    compileOnly(libs.paper)
}