plugins {
    `java-plugin`
}

dependencies {
    compileOnly(libs.vital.common)

    compileOnly(libs.annotations)

    api(project(":crazycrates-api"))
}