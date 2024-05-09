plugins {
    id("io.github.goooler.shadow")

    `root-plugin`
}

project.group = "us.crazycrew.crazycrates"
project.version = "1.0-snapshot"

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")

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
                version = "${project.version}"

                artifact(shadowJar)
            }
        }
    }
}