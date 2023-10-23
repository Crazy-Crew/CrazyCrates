plugins {
    id("root-plugin")
}

project.group = "${rootProject.group}.common"
project.version = "${rootProject.version}"

dependencies {
    api(project(":api"))

    compileOnly(libs.minimessage)
    compileOnly(libs.adventure)

    compileOnly(libs.annotations)
}