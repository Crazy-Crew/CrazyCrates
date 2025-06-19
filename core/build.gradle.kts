plugins {
    `config-java`
}

dependencies {
    api(project(":crazycrates-api"))

    compileOnly(libs.fusion.core)
}