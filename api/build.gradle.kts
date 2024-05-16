plugins {
    id("io.github.goooler.shadow")

    `java-plugin`
}

project.group = "us.crazycrew.crazycrates"
project.version = "0.5"

dependencies {
    compileOnly("net.kyori", "adventure-api", "4.17.0")
}

val javaComponent: SoftwareComponent = components["java"]

tasks {
    val sourcesJar by creating(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    val javadocJar by creating(Jar::class) {
        dependsOn.add(javadoc)
        archiveClassifier.set("javadoc")
        from(javadoc)
    }

    publishing {
        repositories {
            maven {
                url = uri("https://repo.crazycrew.us/releases")

                credentials {
                    this.username = System.getenv("gradle_username")
                    this.password = System.getenv("gradle_password")
                }
            }
        }

        publications {
            create<MavenPublication>("maven") {
                artifactId = "api"

                from(javaComponent)

                artifact(sourcesJar)
                artifact(javadocJar)

                versionMapping {
                    usage("java-api") {
                        fromResolutionOf("runtimeClasspath")
                    }
                    usage("java-runtime") {
                        fromResolutionResult()
                    }
                }

                pom {
                    name.set("CrazyCrates API")
                    description.set("The official API of CrazyCrates")
                    url.set("https://modrinth.com/plugin/crazycrates")

                    licenses {
                        licenses {
                            name.set("MIT")
                            url.set("https://opensource.org/licenses/MIT")
                        }
                    }

                    developers {
                        developer {
                            id.set("ryderbelserion")
                            name.set("Ryder Belserion")
                            email.set("no-reply@ryderbelserion.com")
                        }
                    }

                    scm {
                        connection.set("scm:git:git://github.com/Crazy-Crew/CrazyCrates")
                        developerConnection.set("scm:git:ssh://github.com/Crazy-Crew/CrazyCrates")
                        url.set("https://github.com/Crazy-Crew/CrazyCrates")
                    }
                }
            }
        }
    }
}