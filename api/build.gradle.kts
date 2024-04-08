plugins {
    `maven-publish`
}

project.group = "us.crazycrew.crazycrates"
project.version = "1.0-snapshot"

val mcVersion = libs.versions.bundle.get()

dependencies {
    compileOnly(libs.vital)

    paperweight.paperDevBundle(mcVersion)

    compileOnly(libs.config.me)

    compileOnly(libs.vital)
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks {
    publishing {
        repositories {
            maven {
                url = uri("https://repo.crazycrew.us/snapshots")

                credentials {
                    this.username = System.getenv("gradle_username")
                    this.password = System.getenv("gradle_password")
                }
            }
        }

        publications {
            create<MavenPublication>("maven") {
                group = project.group
                artifactId = project.name
                version = project.version.toString()

                artifact(reobfJar)
            }
        }
    }
}