plugins {
    `maven-publish`
    `java-library`
}

defaultTasks("build")

rootProject.group = "com.badbones69.crazycrates"
rootProject.description = "Add unlimited crates to your server with 10 different crate types to choose from!"
rootProject.version = "1.14"

subprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "java-library")

    repositories {
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

        maven("https://repo.codemc.org/repository/maven-public/")

        maven("https://repo.aikar.co/content/groups/aikar/")

        maven("https://repo.triumphteam.dev/snapshots/")

        maven("https://repo.crazycrew.us/first-party/")

        maven("https://repo.crazycrew.us/third-party/")

        maven("https://repo.crazycrew.us/releases/")

        maven("https://jitpack.io/")

        mavenCentral()
    }

    listOf(
        ":core",
        ":paper"
    ).forEach {
        project(it) {
            group = "${rootProject.group}.${this.name}"
            version = rootProject.version
        }
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of("17"))
    }

    tasks {
        compileJava {
            options.encoding = Charsets.UTF_8.name()
            options.release.set(17)
        }
    }
}

tasks {
    assemble {
        val jarsDir = File("$rootDir/jars")
        if (jarsDir.exists()) jarsDir.delete()

        subprojects.forEach { project ->
            dependsOn(":${project.name}:build")

            doLast {
                if (!jarsDir.exists()) jarsDir.mkdirs()

                if (project.name == "core") return@doLast

                val projectName = "${rootProject.name}-${project.name.substring(0, 1).uppercase() + project.name.substring(1)}"

                val file = file("${project.buildDir}/libs/$projectName-${project.version}.jar")

                copy {
                    from(file)
                    into(jarsDir)
                }
            }
        }
    }
}

val isSnapshot = rootProject.version.toString().contains("snapshot")

publishing {
    repositories {
        maven {
            credentials {
                this.username = System.getenv("gradle_username")
                this.password = System.getenv("gradle_password")
            }

            if (isSnapshot) {
                url = uri("https://repo.crazycrew.us/snapshots/")
                return@maven
            }

            url = uri("https://repo.crazycrew.us/releases/")
        }
    }
}