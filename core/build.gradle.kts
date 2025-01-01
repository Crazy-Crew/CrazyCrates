plugins {
    id("crates.base")
}

dependencies {
    compileOnly(libs.fusion.core)

    compileOnly(libs.jetbrains)

    api(projects.crazycratesApi)
    api(libs.jalu)
}