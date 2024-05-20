plugins {
    `java-plugin`
}

dependencies {
    api(project(":crazycrates-api"))

    compileOnly(libs.vital.core)

    compileOnly(libs.annotations)
}