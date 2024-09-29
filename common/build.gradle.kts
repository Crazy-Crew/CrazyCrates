plugins {
    `java-plugin`
}

dependencies {
    api(project(":api"))

    compileOnly(libs.vital.common)
    compileOnly(libs.jetbrains)
}