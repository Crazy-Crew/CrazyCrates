plugins {
    id("io.github.goooler.shadow")

    `root-plugin`
}

project.group = "us.crazycrew.crazycrates"
project.version = "0.5"

repositories {
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")

    maven("https://repo.papermc.io/repository/maven-public")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.6-R0.1-SNAPSHOT")

    compileOnly(libs.config.me)

    compileOnly(libs.vital)
}

java {
    withSourcesJar()
    withJavadocJar()
}

val component: SoftwareComponent = components["java"]

tasks {
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
                group = project.group
                artifactId = project.name
                version = "${project.version}"

                from(component)
            }
        }
    }
}