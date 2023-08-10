plugins {
    alias(libs.plugins.shadow)
}

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

    shadowJar {
        archiveBaseName.set("${rootProject.name}-${project.name}")
        archiveClassifier.set("")
        mergeServiceFiles()

        listOf(
            "dev.triumphteam",
            "org.bstats",
            "de.tr7zw"
        ).forEach {
            relocate(it, "libs.$it")
        }
    }
}