dependencies {
    compileOnly(libs.vital.api)

    compileOnly(libs.jetbrains)

    api(project(":api"))

    api(libs.jalu)
}