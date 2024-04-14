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
val isMainBranch = branch == "main"

val newVersion = if (!isSnapshot) {
    baseVersion
} else {
    "$baseVersion-${System.getenv("GITHUB_RUN_NUMBER")}"
}

val content = if (!isSnapshot) {
    rootProject.file("CHANGELOG.md").readText(Charsets.UTF_8)
} else {
    val hash = rootProject.latestCommitHash()
    "[$hash](https://github.com/Crazy-Crew/${rootProject.name}/commit/$hash) ${rootProject.latestCommitMessage()}"
}

modrinth {
    token.set(System.getenv("modrinth_token"))

    projectId.set("blockparticles")

    versionType.set(if (!isSnapshot) "release" else "beta")

    versionNumber.set(newVersion)
    versionName.set(newVersion)

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
        apiKey.set(System.getenv("HANGAR_TOKEN"))

        id.set(rootProject.name.lowercase())

        channel.set(if (!isSnapshot) "Release" else "Snapshot")

        changelog.set(content)

        platforms {
            paper {
                jar.set(file("$rootDir/jars/${rootProject.name}-${rootProject.version}.jar"))

                platformVersions.set(listOf(libs.versions.bundle.get()))
            }
        }
    }
}

tasks.modrinth {
    onlyIf {
        !isSnapshot || isMainBranch
    }
}

tasks.publishAllPublicationsToHangar {
    onlyIf {
        !isSnapshot || isMainBranch
    }
}