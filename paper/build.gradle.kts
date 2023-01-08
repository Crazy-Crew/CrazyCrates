plugins {
    id("crazycrates-paper")

    id("com.modrinth.minotaur") version "2.6.0"

    id("com.github.johnrengelman.shadow") version "7.1.2"

    `maven-publish`
}

val isSnapshot = false

fun getPluginVersion(): String {
    return if (isSnapshot) "${project.version}-SNAPSHOT" else project.version.toString()
}

fun getPluginVersionType(): String {
    return if (isSnapshot) "beta" else "release"
}

tasks {
    shadowJar {
        archiveFileName.set("${rootProject.name}-${getPluginVersion()}.jar")

        listOf(
            "de.tr7zw",
            "org.bstats",
            "dev.triumphteam.cmd"
        ).forEach {
            relocate(it, "${rootProject.group}.plugin.lib.$it")
        }
    }

    modrinth {
        token.set(System.getenv("MODRINTH_TOKEN"))
        projectId.set("crazycrates")

        versionName.set("${rootProject.name} ${getPluginVersion()}")
        versionNumber.set(getPluginVersion())

        versionType.set(getPluginVersionType())

        uploadFile.set(shadowJar.get())

        autoAddDependsOn.set(true)

        gameVersions.addAll(listOf("1.18", "1.18.1", "1.18.2", "1.19", "1.19.1", "1.19.2", "1.19.3"))
        loaders.addAll(listOf("paper", "purpur"))

        //<h3>The first release for CrazyCrates on Modrinth! 🎉🎉🎉🎉🎉<h3><br> If we want a header.
        changelog.set("""
                <h2>Changes:</h2>
                 <p>Added an option to limit mass-opening in QuickCrate thanks to TrueDarkLord.</p>
                 <p>Small preparations for multi platform support.</p>
                 <p>Added 1.18.2 support.</p>
                <h2>Bug Fixes:</h2>
                 <p>N/A</p>
            """.trimIndent())
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(
                "name" to rootProject.name,
                "group" to project.group,
                "version" to getPluginVersion(),
                "description" to project.description
            )
        }
    }
}

publishing {
    val mavenExt: String = if (isSnapshot) "snapshots" else "releases"

    repositories {
        maven("https://repo.crazycrew.us/$mavenExt") {
            name = "crazycrew"
            //credentials(PasswordCredentials::class)
            credentials {
                username = System.getenv("REPOSITORY_USERNAME")
                password = System.getenv("REPOSITORY_PASSWORD")
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = "${project.group}"
            artifactId = rootProject.name.toLowerCase()
            version = getPluginVersion()
            from(components["java"])
        }
    }
}