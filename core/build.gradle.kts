plugins {
    id("crates.base")
}

dependencies {
    compileOnly(libs.fusion.core)

    compileOnly(libs.jetbrains)

    compileOnlyApi(libs.jalu)

    api(projects.crazycratesApi)
}