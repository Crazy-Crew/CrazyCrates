import io.papermc.hangarpublishplugin.model.Platforms

plugins {
    `java-library`

    alias(libs.plugins.modrinth)

    alias(libs.plugins.hangar)
}

val mcVersion = rootProject.properties["minecraftVersion"] as String
val isBeta: Boolean get() = rootProject.extra["isBeta"]?.toString()?.toBoolean() ?: false
val type = if (isBeta) "Beta" else "Release"

val component: SoftwareComponent = components["java"]

val jarsDir = File("$rootDir/jars")

tasks {
    assemble {
        if (jarsDir.exists()) jarsDir.delete()
        jarsDir.mkdirs()

        subprojects.forEach { project ->
            dependsOn(":${project.name}:clean build")

            doLast {
                runCatching {
                    if (project.name != "api" || project.name != "common") {
                        copy {
                            from(project.layout.buildDirectory.file("libs/${rootProject.name}-${project.version}.jar"))
                            into(jarsDir)
                        }
                    }
                }.onSuccess {
                    // Delete to save space on jenkins.
                    delete(project.layout.buildDirectory.get())
                    delete(rootProject.layout.buildDirectory.get())
                }.onFailure {
                    println("Failed to copy file out of build folder into jars directory: Likely does not exist.")
                }
            }
        }
    }
}

subprojects {
    apply(plugin = "java-library")

    repositories {
        maven("https://repo.crazycrew.us/releases")

        maven("https://jitpack.io/")

        mavenCentral()
    }

    tasks {
        compileJava {
            options.encoding = Charsets.UTF_8.name()
            options.release.set(17)
        }

        javadoc {
            options.encoding = Charsets.UTF_8.name()
        }

        processResources {
            filteringCharset = Charsets.UTF_8.name()
        }
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of("17"))
    }

    val buildNumber = System.getenv("BUILD_NUMBER")

    when (project.name) {
        "paper" -> {
            val newVersion = "1.19.1"

            project.version = if (buildNumber != null) "$newVersion-#$buildNumber" else newVersion
        }

        "fabric" -> {
            val newVersion = "1.19.1"

            project.version = if (buildNumber != null) "$newVersion-#$buildNumber" else newVersion
        }

        "forge" -> {
            val newVersion = "1.19.1"

            project.version = if (buildNumber != null) "$newVersion-#$buildNumber" else newVersion
        }
    }

    if (name == "paper") {
        repositories {
            maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

            maven("https://repo.codemc.io/repository/maven-public/")

            maven("https://repo.triumphteam.dev/snapshots/")

            maven("https://repo.oraxen.com/releases/")

            flatDir { dirs("libs") }
        }

        apply(plugin = "io.papermc.hangar-publish-plugin")

        tasks {
            // Publish to hangar.papermc.io.
            hangarPublish {
                publications.register("plugin") {
                    version.set("${project.version}")

                    id.set(rootProject.name)

                    channel.set(type)

                    changelog.set(rootProject.file("CHANGELOG.md").readText())

                    apiKey.set(System.getenv("hangar_key"))

                    platforms {
                        register(Platforms.PAPER) {
                            jar.set(file("$jarsDir/${rootProject.name}-${project.version}.jar"))

                            platformVersions.set(listOf(mcVersion))
                        }
                    }
                }
            }
        }
    }

    if (name == "paper" || name == "fabric" || name == "forge") {
        apply(plugin = "com.modrinth.minotaur")

        tasks {
            // Publish to modrinth.
            modrinth {
                autoAddDependsOn.set(false)

                token.set(System.getenv("modrinth_token"))

                projectId.set("crazyrunes")

                versionName.set("${rootProject.name} ${project.version}")

                versionNumber.set("${project.version}")

                versionType.set(type.lowercase())

                uploadFile.set("$jarsDir/${rootProject.name}-${project.version}.jar")

                gameVersions.add(mcVersion)

                changelog.set(rootProject.file("CHANGELOG.md").readText())

                when (project.name) {
                    "fabric" -> {
                        loaders.addAll("fabric", "quilt")
                    }

                    "paper" -> {
                        loaders.addAll("paper", "purpur")
                    }

                    "forge" -> {
                        loaders.addAll("forge", "neoforge")
                    }
                }
            }
        }
    }
}