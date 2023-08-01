plugins {
    id("paper-plugin")
}

group = "${rootProject.group}.core"

base {
    archivesName.set("${rootProject.name}-${project.name}")
}

val component: SoftwareComponent = components["java"]

tasks {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = rootProject.group.toString()
                artifactId = "${rootProject.name.lowercase()}-${project.name.lowercase()}-api"
                version = rootProject.version.toString()

                from(component)
            }
        }
    }

    assemble {
        dependsOn(shadowJar)
    }
}