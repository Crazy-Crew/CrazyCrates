plugins {
    alias(libs.plugins.minotaur)
    alias(libs.plugins.feather)
    alias(libs.plugins.hangar)

    `config-java`
}

rootProject.group = "com.badbones69.crazycrates"

val git = feather.getGit()

val commitHash: String? = git.getCurrentCommitHash().subSequence(0, 7).toString()
val isSnapshot: Boolean = System.getenv("IS_SNAPSHOT") != null
val content: String = if (isSnapshot) "[$commitHash](https://github.com/Crazy-Crew/${rootProject.name}/commit/$commitHash) ${git.getCurrentCommit()}" else rootProject.file("changelog.md").readText(Charsets.UTF_8)
val minecraft = libs.versions.minecraft.get()

rootProject.version = version()
rootProject.description = "Add crates to your server with 11 different crate types to choose from!"

fun version(): String {
    if (isSnapshot) {
        return "$minecraft-$commitHash"
    }

    if (System.getenv("BUILD_NUMBER") != null && System.getenv("IS_PUBLISHING") == null) {
        return "$minecraft-${System.getenv("BUILD_NUMBER")}-$commitHash"
    }

    return libs.versions.crazycrates.get()
}

feather {
    rootDirectory = rootProject.rootDir.toPath()

    val data = git.getCurrentCommitAuthorData()

    discord {
        webhook {
            group(rootProject.name.lowercase())
            task("dev-build")

            if (System.getenv("BUILD_WEBHOOK") != null) {
                post(System.getenv("BUILD_WEBHOOK"))
            }

            username(data.author)

            avatar(data.avatar)

            embeds {
                embed {
                    color("#ffa347")

                    title("A new dev version of ${rootProject.name} is ready!")

                    fields {
                        field(
                            "Version ${rootProject.version}",
                            "Click [here](https://modrinth.com/plugin/${rootProject.name.lowercase()}/version/${rootProject.version}) to download!"
                        )

                        field(
                            ":bug: Report Bugs",
                            "https://github.com/Crazy-Crew/${rootProject.name}/issues"
                        )

                        field(
                            ":hammer: Changelog",
                            content
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

            username(data.author)

            avatar(data.avatar)

            content("<@&929463441159254066>")

            embeds {
                embed {
                    color("#1bd96a")

                    title("A new release version of ${rootProject.name} is ready!")

                    fields {
                        field(
                            "Version ${rootProject.version}",
                            "Click [here](https://modrinth.com/plugin/${rootProject.name.lowercase()}/version/${rootProject.version}) to download!"
                        )

                        field(
                            ":bug: Report Bugs",
                            "https://github.com/Crazy-Crew/${rootProject.name}/issues"
                        )

                        field(
                            ":hammer: Changelog",
                            content
                        )
                    }
                }
            }
        }
    }
}

allprojects { //todo() why? the gradle shit in buildSrc already applies this...
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

modrinth {
    token = System.getenv("MODRINTH_TOKEN")

    projectId = rootProject.name

    versionName = "${rootProject.name} ${rootProject.version}"
    versionNumber = "${rootProject.version}"
    versionType = if (isSnapshot) "beta" else "release"

    changelog = content

    gameVersions.addAll(listOf(libs.versions.minecraft.get()))

    uploadFile = tasks.jar.get().archiveFile.get()

    loaders.addAll(listOf("paper", "folia", "purpur"))

    syncBodyFrom = rootProject.file("description.md").readText(Charsets.UTF_8)

    autoAddDependsOn = false
    detectLoaders = false

    dependencies {
        optional.project("fancyholograms")
        optional.project("DecentHolograms")
    }
}

hangarPublish {
    publications.register("plugin") {
        apiKey.set(System.getenv("HANGAR_KEY"))

        id.set(rootProject.name)

        version.set("${rootProject.version}")

        channel.set(if (isSnapshot) "Beta" else "Release")

        changelog.set(content)

        platforms {
            paper {
                jar.set(tasks.jar.get().archiveFile.get())

                platformVersions.set(listOf(libs.versions.minecraft.get()))

                dependencies {
                    hangar("PlaceholderAPI") {
                        required = false
                    }

                    hangar("FancyHolograms") {
                        required = false
                    }

                    url("DecentHolograms", "https://modrinth.com/plugin/decentholograms") {
                        required = false
                    }

                    url("ItemsAdder", "https://polymart.org/product/1851/itemsadder") {
                        required = false
                    }

                    url("Oraxen", "https://polymart.org/product/629/oraxen") {
                        required = false
                    }

                    url("Nexo", "https://polymart.org/resource/nexo.6901") {
                        required = false
                    }
                }
            }
        }
    }
}