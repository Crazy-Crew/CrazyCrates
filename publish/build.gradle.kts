plugins {
    alias(libs.plugins.minotaur)
    alias(libs.plugins.hangar)
}

val content: String = rootProject.file("CHANGELOG.md").readText(Charsets.UTF_8)

val isBeta = false
val pluginName = rootProject.name
val mcVersion = libs.versions.minecraft.get()

tasks {
    modrinth {
        token.set(System.getenv("MODRINTH_TOKEN"))

        projectId.set(rootProject.name)

        versionType.set(if (isBeta) "beta" else "release")

        versionName.set("$pluginName ${rootProject.version}")
        versionNumber.set(rootProject.version as String)

        changelog.set(content)

        uploadFile.set(rootProject.projectDir.resolve("jars/$pluginName-${rootProject.version}.jar"))

        gameVersions.set(listOf(mcVersion))

        loaders.addAll(listOf("purpur", "paper", "folia"))

        syncBodyFrom.set(rootProject.file("README.md").readText(Charsets.UTF_8))

        autoAddDependsOn.set(false)
        detectLoaders.set(false)

        dependencies {
            optional.version("fancyholograms", "2.3.2")
        }
    }

    hangarPublish {
        publications.register("plugin") {
            apiKey.set(System.getenv("HANGAR_KEY"))

            id.set(pluginName)

            version.set(rootProject.version as String)

            channel.set(if (isBeta) "Beta" else "Release")

            changelog.set(content)

            platforms {
                paper {
                    jar.set(rootProject.projectDir.resolve("jars/$pluginName-${rootProject.version}.jar"))

                    platformVersions.set(listOf(mcVersion))

                    dependencies {
                        hangar("PlaceholderAPI") {
                            required = false
                        }

                        hangar("FancyHolograms") {
                            required = false
                        }

                        url("Oraxen", "https://www.spigotmc.org/resources/%E2%98%84%EF%B8%8F-oraxen-custom-items-blocks-emotes-furniture-resourcepack-and-gui-1-18-1-20-4.72448/") {
                            required = false
                        }

                        url("CMI", "https://www.spigotmc.org/resources/cmi-298-commands-insane-kits-portals-essentials-economy-mysql-sqlite-much-more.3742/") {
                            required = false
                        }

                        url("DecentHolograms", "https://www.spigotmc.org/resources/decentholograms-1-8-1-20-4-papi-support-no-dependencies.96927/") {
                            required = false
                        }
                    }
                }
            }
        }
    }
}