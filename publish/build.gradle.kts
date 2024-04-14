plugins {
    `root-plugin`

    id("io.papermc.hangar-publish-plugin") version "0.1.2"
    id("com.modrinth.minotaur") version "2.+"
}

dependencies {
    api(projects.paper)
}

val branch = branchName()
val baseVersion = project.version as String
val isSnapshot = branch.contains("-")

val newVersion = if (isSnapshot) {
    "$baseVersion-${System.getenv("GITHUB_RUN_NUMBER")}"
} else {
    baseVersion
}

val content = if (isSnapshot) {
    val hash = rootProject.latestCommitHash()
    "[$hash](https://github.com/Crazy-Crew/${rootProject.name}/commit/$hash) ${rootProject.latestCommitMessage()}"
} else {
    rootProject.file("CHANGELOG.md").readText(Charsets.UTF_8)
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))

    projectId.set(rootProject.name.lowercase())

    versionType.set(if (!isSnapshot) "release" else "beta")

    versionName.set("${rootProject.name} ${rootProject.version}")
    versionNumber.set(newVersion)

    changelog.set(content)

    uploadFile.set("$rootDir/jars/${rootProject.name}-${rootProject.version}.jar")

    gameVersions.set(listOf(libs.versions.bundle.get()))

    loaders.add("paper")
    loaders.add("purpur")

    autoAddDependsOn.set(false)
    detectLoaders.set(false)
}

hangarPublish {
    publications.register("plugin") {
        apiKey.set(System.getenv("HANGAR_KEY"))

        id.set(rootProject.name.lowercase())

        version.set(newVersion)

        channel.set(if (!isSnapshot) "Release" else "Snapshot")

        changelog.set(content)

        platforms {
            paper {
                jar.set(file("$rootDir/jars/${rootProject.name}-${rootProject.version}.jar"))

                platformVersions.set(listOf(libs.versions.bundle.get()))

                dependencies {
                    hangar("PlaceholderAPI") {
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