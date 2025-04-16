plugins {
    id("root-plugin")

    alias(libs.plugins.minotaur)
    alias(libs.plugins.hangar)
}

val content: String = rootProject.file("changelog.md").readText(Charsets.UTF_8)

val isBeta = System.getenv("BETA") != null
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

        syncBodyFrom.set(rootProject.file("description.md").readText(Charsets.UTF_8))

        autoAddDependsOn.set(false)
        detectLoaders.set(false)

        dependencies {
            optional.project("fancyholograms")
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
                        url("PlaceholderAPI", "https://hangar.papermc.io/HelpChat/PlaceholderAPI") {
                            required = false
                        }

                        url("FancyHolograms", "https://modrinth.com/plugin/fancyholograms") {
                            required = false
                        }

                        url("Oraxen", "https://polymart.org/product/629/oraxen") {
                            required = false
                        }

                        url("Nexo", "https://polymart.org/resource/nexo.6901") {
                            required = false
                        }

                        url("CMI", "https://www.spigotmc.org/resources/cmi-298-commands-insane-kits-portals-essentials-economy-mysql-sqlite-much-more.3742/") {
                            required = false
                        }

                        url("DecentHolograms", "https://modrinth.com/plugin/decentholograms") {
                            required = false
                        }

                        url("ItemsAdder", "https://polymart.org/product/1851/itemsadder") {
                            required = false
                        }
                    }
                }
            }
        }
    }
}