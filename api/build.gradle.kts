plugins {
    id("config-java")

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

            credentials(PasswordCredentials::class)
            authentication.create<BasicAuthentication>("basic")
        }
    }

    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "${project.group}"
            artifactId = "api"
            version = "0.8"

            from(components["java"])
        }
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}