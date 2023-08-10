plugins {
    alias(libs.plugins.userdev)
}

base {
    archivesName.set("${rootProject.name}-${project.name}")
}

repositories {
    flatDir { dirs("libs") }
}

dependencies {
    compileOnly(project(":core"))

    compileOnly(fileTree("libs").include("*.jar"))

    compileOnly("me.clip", "placeholderapi", "2.11.3")

    compileOnly("com.github.decentsoftware-eu", "decentholograms","2.8.3")

    paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")
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

    reobfJar {
        outputJar.set(file("$buildDir/libs/${rootProject.name}-${project.name}-${project.version}.jar"))
    }

    assemble {
        dependsOn(reobfJar)
    }

    processResources {
        val props = mapOf(
            "name" to rootProject.name,
            "group" to project.group,
            "version" to rootProject.version,
            "description" to rootProject.description,
            "authors" to rootProject.properties["authors"],
            "apiVersion" to "1.20",
            "website" to "https://modrinth.com/plugin/${rootProject.name.lowercase()}"
        )

        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}