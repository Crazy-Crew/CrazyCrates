@Suppress("DSL_SCOPE_VIOLATION")

plugins {
    id("crazycrates.paper-plugin")
}

dependencies {
    api(project(":crazycrates-core"))

    compileOnly(libs.gabber)

    //compileOnly(libs.papermc)
}

val projectDescription = settings.versions.projectDescription.get()
val projectGithub = settings.versions.projectGithub.get()
val projectGroup = settings.versions.projectGroup.get()
val projectName = settings.versions.projectName.get()

val projectVersion = settings.versions.projectVersion.get()

val projectNameLowerCase = projectName.toLowerCase()

tasks {
    shadowJar {
        archiveFileName.set("${projectName}+Typewriter+0.0.1.jar")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = projectGroup
            artifactId = "$projectNameLowerCase-typewriter"
            version = "0.0.1"

            from(components["java"])

            pom {
                name.set(projectName)

                description.set(projectDescription)
                url.set(projectGithub)

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://www.opensource.org/licenses/mit-license.php")
                    }
                }

                developers {
                    developer {
                        id.set("ryderbelserion")
                        name.set("Ryder Belserion")
                    }

                    developer {
                        id.set("badbones69")
                        name.set("BadBones69")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/Crazy-Crew/$projectName.git")
                    developerConnection.set("scm:git:ssh://github.com/Crazy-Crew/$projectName.git")
                    url.set(projectGithub)
                }
            }
        }
    }
}