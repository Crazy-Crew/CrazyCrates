plugins {
    alias(libs.plugins.shadow)
}

val projectName = "${rootProject.name}-${project.name.substring(0, 1).uppercase() + project.name.substring(1)}"

base {
    archivesName.set(projectName)
}

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

    assemble {
        dependsOn(shadowJar)
    }
}