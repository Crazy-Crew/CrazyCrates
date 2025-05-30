plugins {
    alias(libs.plugins.fix.javadoc)

    id("config-java")

    `maven-publish`
}

project.group = "us.crazycrew.crazycrates"
project.description = "The official API for CrazyCrates!"

dependencies {
    compileOnly(libs.bundles.adventure)
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks {
    withType<com.jeff_media.fixjavadoc.FixJavadoc> {
        configureEach {
            newLineOnMethodParameters.set(false)
            keepOriginal.set(false)
        }
    }
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