plugins {
    id("crazycrates-paper")

    id("com.modrinth.minotaur") version "2.5.0"

    id("com.github.johnrengelman.shadow") version "7.1.2"

    `maven-publish`
}

val buildNumber: String? = System.getenv("BUILD_NUMBER")
val buildVersion = "${project.version}-b$buildNumber-SNAPSHOT"

tasks {
    shadowJar {
        if (buildNumber != null) {
            archiveFileName.set("${rootProject.name}-${buildVersion}.jar")
        } else {
            archiveFileName.set("${rootProject.name}-${project.version}.jar")
        }

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
        versionName.set("${rootProject.name} ${project.version} Update")
        versionNumber.set("${project.version}")
        versionType.set("${extra["version_type"]}")
        uploadFile.set(shadowJar.get())

        autoAddDependsOn.set(true)

        gameVersions.addAll(listOf("1.19", "1.19.1", "1.19.2", "1.19.3"))
        loaders.addAll(listOf("paper", "purpur"))

        //<h3>The first release for CrazyCrates on Modrinth! 🎉🎉🎉🎉🎉<h3><br> If we want a header.
        changelog.set("""
                <h2>Changes:</h2>
                 <p>QuadCrate structures now spawn directly on the crate.</p>
                 <p>Added a toggle so you can turn off the crate menu (/cc)</p>
                 <p>Added more verbose messages for when you type an incorrect command</p>
                 <p>Added the ability to hide & show holograms on QuickCrate/FireCracker crate type</p>
                 <p>Added an updater notification in console & on join if opped or if you have the crazycrates.command.admin.help ( You can turn it off in the config.yml</p>
                 <p>Added temporary config version system as configs including crate configs may have breaking changes</p>
                <h2>Bug Fixes:</h2>
                 <p>Fixed why quadcrate structures would not despawn</p>
                 <p>Fixed why quadcrate chests would not spawn</p>
            """.trimIndent())
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(
                "name" to rootProject.name,
                "group" to project.group,
                "version" to if (buildNumber != null) buildVersion else project.version,
                "description" to project.description
            )
        }
    }
}

publishing {
    repositories {
        maven("https://repo.crazycrew.us/releases") {
            name = "crazycrew"
            //credentials(PasswordCredentials::class)
            credentials {
                username = System.getenv("CRAZYCREW_USERNAME")
                password = System.getenv("CRAZYCREW_PASSWORD")
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = "${extra["plugin_group"]}"
            artifactId = rootProject.name.toLowerCase()
            version = "${project.version}"
            from(components["java"])
        }
    }
}