plugins {
    `paper-plugin`
}

project.group = "us.crazycrew.crazycrates"
project.version = "1.0-snapshot"

dependencies {
    compileOnly(libs.vital)

    compileOnly(libs.config.me)
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
                version = "${project.version}"

                artifact(reobfJar)
            }
        }
    }
}