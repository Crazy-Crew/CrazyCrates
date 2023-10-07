plugins {
    id("root-plugin")
}

project.group = "${rootProject.group}.common"
project.version = rootProject.version

base {
    archivesName.set(rootProject.name)
}

dependencies {
    api(project(":api"))

    api(libs.cluster.api)

    api(libs.config.me) {
        exclude("org.yaml", "snakeyaml")
    }

    compileOnly(libs.adventure.api)
    compileOnly(libs.adventure.mm)

    compileOnly(libs.annotations)
}