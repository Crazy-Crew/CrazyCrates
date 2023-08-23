plugins {
    id("paper-plugin")
}

project.group = "${rootProject.group}.core"

val component: SoftwareComponent = components["java"]

tasks {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = project.group.toString()
                artifactId = "${rootProject.name.lowercase()}-${project.name.lowercase()}-api"
                version = project.version.toString()

                from(component)
            }
        }
    }
}