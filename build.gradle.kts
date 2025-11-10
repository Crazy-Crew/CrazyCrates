plugins {
    id("modrinth-plugin")
    id("hangar-plugin")

    `java-plugin`
}

val git = feather.getGit()

allprojects {
    apply(plugin = "java-library")
}

tasks {
    withType<Jar> {
        subprojects {
            dependsOn(project.tasks.build)
        }

        // get subproject's built jars
        val jars = subprojects.map { zipTree(it.tasks.jar.get().archiveFile.get().asFile) }

        // merge them into main jar (except their manifests)
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        from(jars) {
            exclude("META-INF/MANIFEST.MF")
        }

        // put behind an action because files don't exist at configuration time
        doFirst {
            // merge all subproject's manifests into main manifest
            jars.forEach { jar ->
                jar.matching { include("META-INF/MANIFEST.MF") }
                    .files.forEach { file ->
                        manifest.from(file)
                    }
            }
        }
    }
}

feather {
    rootDirectory = rootProject.rootDir.toPath()

    val data = git.getGithubCommit("Crazy-Crew/${rootProject.name}")

    val user = data.user

    discord {
        webhook {
            group(rootProject.name.lowercase())
            task("dev-build")

            if (System.getenv("CC_WEBHOOK") != null) {
                post(System.getenv("CC_WEBHOOK"))
            }

            username("Ryder Belserion")

            avatar("https://github.com/ryderbelserion.png")

            embeds {
                embed {
                    color(rootProject.property("dev_color").toString())

                    title("A new dev version of ${rootProject.name} is ready!")

                    fields {
                        field(
                            "Version ${rootProject.version}",
                            listOf(
                                "*Click below to download!*",
                                "<:modrinth:1115307870473420800> [Modrinth](https://modrinth.com/plugin/${rootProject.name.lowercase()}/version/${rootProject.version})",
                                "<:hangar:1139326635313733652> [Hangar](https://hangar.papermc.io/CrazyCrew/${rootProject.name.lowercase()}/versions/${rootProject.version})"
                            ).convertList()
                        )

                        field(
                            ":bug: Report Bugs",
                            "https://github.com/Crazy-Crew/${rootProject.name}/issues"
                        )

                        field(
                            ":hammer: Changelog",
                            rootProject.ext.get("mc_changelog").toString()
                        )
                    }
                }
            }
        }

        webhook {
            group(rootProject.name.lowercase())
            task("release-build")

            if (System.getenv("BUILD_WEBHOOK") != null) {
                post(System.getenv("BUILD_WEBHOOK"))
            }

            username(user.getName())

            avatar(user.avatar)

            content("<@&${rootProject.property("discord_role_id").toString()}>")

            embeds {
                embed {
                    color(rootProject.property("release_color").toString())

                    title("A new release version of ${rootProject.name} is ready!")

                    fields {
                        field(
                            "Version ${rootProject.version}",
                            listOf(
                                "*Click below to download!*",
                                "<:modrinth:1115307870473420800> [Modrinth](https://modrinth.com/plugin/${rootProject.name.lowercase()}/version/${rootProject.version})",
                                "<:hangar:1139326635313733652> [Hangar](https://hangar.papermc.io/CrazyCrew/${rootProject.name.lowercase()}/versions/${rootProject.version})"
                            ).convertList()
                        )

                        field(
                            ":bug: Report Bugs",
                            "https://github.com/Crazy-Crew/${rootProject.name}/issues"
                        )

                        field(
                            ":hammer: Changelog",
                            listOf(
                                "<:modrinth:1115307870473420800> [Modrinth](https://modrinth.com/plugin/${rootProject.name.lowercase()}/version/${rootProject.version})",
                                "<:hangar:1139326635313733652> [Hangar](https://hangar.papermc.io/CrazyCrew/${rootProject.name.lowercase()}/versions/${rootProject.version})"
                            ).convertList()
                        )
                    }
                }
            }
        }

        webhook {
            group(rootProject.name.lowercase())
            task("failed-build")

            if (System.getenv("CC_WEBHOOK") != null) {
                post(System.getenv("CC_WEBHOOK"))
            }

            if (System.getenv("BUILD_WEBHOOK") != null) {
                post(System.getenv("BUILD_WEBHOOK"))
            }

            username("Ryder Belserion")

            avatar("https://github.com/ryderbelserion.png")

            embeds {
                embed {
                    color(rootProject.property("failed_color").toString())

                    title("Oh no! It failed!")

                    thumbnail("https://raw.githubusercontent.com/ryderbelserion/Branding/refs/heads/main/booze.jpg")

                    fields {
                        field(
                            "The build versioned ${rootProject.version} for project ${rootProject.name} failed.",
                            "The developer is likely already aware, he is just getting drunk.",
                            inline = true
                        )
                    }
                }
            }
        }
    }
}

fun List<String>.convertList(): String {
    val builder = StringBuilder(size)

    forEach {
        builder.append(it).append("\n")
    }

    return builder.toString()
}