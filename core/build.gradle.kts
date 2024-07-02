plugins {
    `paper-plugin`
}

dependencies {
    compileOnly(libs.paper)

    api(projects.crazycratesApi)

    api(libs.vital.paper)
}