plugins {
    alias(libs.plugins.fix.javadoc)

    `maven-publish`
    `java-plugin`
}

project.group = "us.crazycrew.crazycrates"
project.description = "The official API for CrazyCrates!"

val projectVersion = rootProject.property("api_version").toString()

dependencies {
    compileOnly(libs.bundles.adventure)
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks {
    javadoc {
        val name = rootProject.name.replaceFirstChar { it.uppercase() }
        val options = options as StandardJavadocDocletOptions

        options.header = """<img src="https://raw.githubusercontent.com/Crazy-Crew/Branding/refs/heads/main/crazycrates/png/64x64.png" style="height:100%">"""
        options.windowTitle("$name $projectVersion API Documentation")
        options.docTitle("<h1>$name $projectVersion API</h1>")
        options.overview("src/main/javadoc/overview.html")
        options.addBooleanOption("html5", true)
        options.bottom("Copyright © 2025 CrazyCrew")
        options.encoding = Charsets.UTF_8.name()
        options.linkSource(true)
        options.isDocFilesSubDirs = true
        options.use()
    }

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
            groupId = "${project.group}" // us.crazycrew.crazycrates
            artifactId = project.name
            version = projectVersion

            from(components["java"])
        }
    }
}