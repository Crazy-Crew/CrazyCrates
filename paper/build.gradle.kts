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

        changelog.set(System.getenv("COMMIT_MESSAGE"))
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
        maven("https://repo.crazycrew.us/snapshots") {
            name = "crazycrew"
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