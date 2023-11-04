plugins {
    id("root-plugin")
}

project.group = "${rootProject.group}.common"
project.version = "${rootProject.version}"

dependencies {
    api(project(":api"))

    api(libs.configme) {
        exclude("org.yaml", "snakeyaml")
    }

    compileOnly(libs.annotations)
}