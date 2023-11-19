plugins {
    id("root-plugin")
}

project.group = "${rootProject.group}.api"
project.version = "${rootProject.version}"

base {
    archivesName.set("${rootProject.name}-${project.name}")
}