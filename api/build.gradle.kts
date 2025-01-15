plugins {
    id("root-plugin")

    `maven-publish`
}

project.group = "us.crazycrew.crazycrates"
project.description = "The official API for CrazyCrates!"
project.version = "0.8"

dependencies {
    compileOnly(libs.bundles.adventure)
}

publishing {
    repositories {
        maven {
            url = uri("https://repo.crazycrew.us/releases/")

            credentials {
                this.username = System.getenv("gradle_username")
                this.password = System.getenv("gradle_password")
            }
        }
    }
}