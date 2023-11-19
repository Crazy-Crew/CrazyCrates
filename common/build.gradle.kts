plugins {
    id("root-plugin")
}

project.group = "${rootProject.group}.common"
project.version = "${rootProject.version}"

base {
    archivesName.set("${rootProject.name}-${project.name}")
}

dependencies {
    api(project(":api"))

    api(libs.config.me) {
        exclude("org.yaml", "snakeyaml")
    }
}