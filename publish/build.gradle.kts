plugins {
    `root-plugin`

    id("io.papermc.hangar-publish-plugin") version "0.1.2"
    id("com.modrinth.minotaur") version "2.+"
}

dependencies {
    api(projects.paper)
}

val finalVersion = if (System.getenv("NEXT_BUILD_NUMBER") != null) "${rootProject.version}-${System.getenv("NEXT_BUILD_NUMBER")}" else rootProject.version as String
val isSnapshot = finalVersion.contains("-")

val content: String = if (isSnapshot) {
    if (System.getenv("COMMIT_MESSAGE") != null) {
        System.getenv("COMMIT_MESSAGE")
    } else {
        formatLog(latestCommitHash(), latestCommitMessage(), rootProject.name)
    }
} else {
    rootProject.file("CHANGELOG.md").readText(Charsets.UTF_8)
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))

    projectId.set(rootProject.name.lowercase())

    versionType.set(if (isSnapshot) "beta" else "release")

    versionName.set("${rootProject.name} $finalVersion")
    versionNumber.set(finalVersion)

    changelog.set(content)

    uploadFile.set(file("$rootDir/jars/${rootProject.name}-$finalVersion.jar"))

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

        version.set(finalVersion)

        channel.set(if (isSnapshot) "Snapshot" else "Release")

        changelog.set(content)

        platforms {
            paper {
                jar.set(file("$rootDir/jars/${rootProject.name}-$finalVersion.jar"))

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