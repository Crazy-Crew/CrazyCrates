plugins {
    alias(libs.plugins.shadowJar)

    `paper-plugin`
}

project.group = "us.crazycrew.crazycrates"
project.version = "0.8"

dependencies {
    compileOnly(libs.paper)
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
            }
        }
    }
}