import io.papermc.hangarpublishplugin.model.Platforms
import org.gradle.kotlin.dsl.support.uppercaseFirstChar

plugins {
    `java-library`

    alias(libs.plugins.modrinth)

    alias(libs.plugins.hangar)
}

val mcVersion = rootProject.properties["minecraftVersion"] as String

val version = rootProject.version as String
val isRelease: Boolean = version.contains("-")
val type = if (isRelease) "Release" else "Beta"

val component: SoftwareComponent = components["java"]

tasks {
    val jarsDir = File("$rootDir/jars")

    assemble {
        if (jarsDir.exists()) jarsDir.delete()

        jarsDir.mkdirs()

        subprojects.forEach { project ->
            dependsOn(":${project.name}:build")

            doLast {
                runCatching {
                    copy {
                        from(project.layout.buildDirectory.file("libs/${rootProject.name}-${project.name.uppercaseFirstChar()}-${version}.jar"))
                        into(jarsDir)
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

    // Publish to hangar.papermc.io.
    hangarPublish {
        publications.register("plugin") {
            version.set("$rootProject.version")

            id.set(rootProject.name)

            channel.set(type)

            changelog.set(rootProject.file("CHANGELOG.md").readText())

            apiKey.set(System.getenv("hangar_key"))

            platforms {
                register(Platforms.PAPER) {
                    jar.set(file("$jarsDir/${rootProject.name}-Paper-${rootProject.version}.jar"))

                    platformVersions.set(listOf(mcVersion))
                }
            }
        }
    }

    // Publish to modrinth.
    modrinth {
        autoAddDependsOn.set(false)

        token.set(System.getenv("modrinth_token"))

        projectId.set("crazyrunes")

        versionName.set("${rootProject.name} ${rootProject.version}")

        versionNumber.set("${rootProject.version}")

        versionType.set(type.lowercase())

        uploadFile.set(file("$jarsDir/${rootProject.name}-Paper-${rootProject.version}.jar"))

        gameVersions.add(mcVersion)

        changelog.set(rootProject.file("CHANGELOG.md").readText())

        loaders.addAll("paper", "purpur")
    }
}

subprojects {
    apply(plugin = "java-library")

    repositories {
        maven("https://repo.crazycrew.us/releases")

        maven("https://jitpack.io/")

        mavenCentral()
    }

    if (name == "paper") {
        repositories {
            maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

            maven("https://repo.codemc.io/repository/maven-public/")

            maven("https://repo.triumphteam.dev/snapshots/")

            maven("https://repo.oraxen.com/releases/")

            flatDir { dirs("libs") }
        }
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
}