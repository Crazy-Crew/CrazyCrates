project.group = "${rootProject.group}.common"

base {
    archivesName.set("${rootProject.name}-${project.name}")
}

dependencies {
    api(project(":api"))

    api(libs.config.me) {
        exclude(group = "org.yaml", module = "snakeyaml")
    }

    compileOnly(libs.cluster.api)
}