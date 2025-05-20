plugins {
    `config-java`
}

dependencies {
    api(project(":crazycrates-api"))

    compileOnlyApi(libs.fusion.core)
}