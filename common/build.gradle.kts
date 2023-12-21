plugins {
    id("root-plugin")
}

dependencies {
    api(project(":api"))

    api(libs.config.me) {
        exclude(group = "org.yaml", module = "snakeyaml")
    }

    compileOnly(libs.cluster.api)
}