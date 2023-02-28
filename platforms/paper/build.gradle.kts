@Suppress("DSL_SCOPE_VIOLATION")

plugins {
    id("crazycrates.paper-plugin")

    alias(settings.plugins.minotaur)
}

repositories {
    /**
     * PAPI Team
     */
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    /**
     * NBT Team
     */
    maven("https://repo.codemc.org/repository/maven-public/")
}

dependencies {
    api(project(":crazycrates-api"))

    compileOnly(libs.papermc)

    compileOnly(libs.holographic.displays)
    compileOnly(libs.decent.holograms)
    compileOnly(libs.cmi.api)
    compileOnly(libs.cmi.lib)

    compileOnly(libs.placeholder.api)

    compileOnly(libs.itemsadder.api)

    implementation(libs.triumph.cmds)
    //implementation(libs.triumph.gui)

    implementation(libs.nbt.api)
    implementation(libs.bstats.bukkit)

    implementation(libs.ruby.paper)
}

val projectDescription = settings.versions.projectDescription.get()
val projectGithub = settings.versions.projectGithub.get()
val projectGroup = settings.versions.projectGroup.get()
val projectName = settings.versions.projectName.get()
val projectExt = settings.versions.projectExtension.get()

val isBeta = settings.versions.projectBeta.get().toBoolean()

val projectVersion = settings.versions.projectVersion.get()

val finalVersion = if (isBeta) "$projectVersion+beta" else projectVersion

val type = if (isBeta) "beta" else "release"

tasks {
    shadowJar {
        archiveFileName.set("${projectName}+${projectDir.name}+$finalVersion.jar")

        listOf(
            "de.tr7zw.changeme.nbtapi",
            "org.bstats",
            "dev.triumphteam.cmd",
            "net.dehya.ruby"
        ).forEach { relocate(it, "$projectGroup.library.$it") }
    }

    modrinth {
        token.set(System.getenv("MODRINTH_TOKEN"))
        projectId.set(projectName.lowercase())

        versionName.set("$projectName $finalVersion")
        versionNumber.set(finalVersion)

        versionType.set(type)

        uploadFile.set(shadowJar.get())

        autoAddDependsOn.set(true)

        gameVersions.addAll(
            listOf(
                "1.17",
                "1.17.1",
                "1.18",
                "1.18.1",
                "1.18.2",
                "1.19",
                "1.19.1",
                "1.19.2",
                "1.19.3"
            )
        )

        loaders.addAll(listOf("paper", "purpur"))

        //<h3>The first release for CrazyCrates on Modrinth! 🎉🎉🎉🎉🎉<h3><br> If we want a header.
        changelog.set(
            """
                <h3>⚠️ This is a MAJOR release, Please take a backup of your CrazyCrates folder if you need to downgrade. ( I am not responsible if you didn't take one ) ⚠️<h3>
                <h4>Changes:</h4>
                 <p>Added 1.17.1 support back.</p>
                 <p>Added a feature https://github.com/orgs/Crazy-Crew/discussions/19</p>
                 <p>Updated the config file with an auto converter on start-up.</p>
                 <p>Added multiple locale files under the locale folder with a config option to choose which language you want.</p>
                <h4>Under the hood changes</h4>
                 <p>Re-organized the build script for the last time.</p>
                 <p>Cleaned up a few pieces of code.</p>
                 <p>No longer check for updates when a player joins.</p>
                 <p>Updated nbt-api artifact id.</p>
                 <p>Bumped cmi api</p>
                <h4>Bug Fixes:</h4>
                 <p>Fixed quadcrates hopefully for the last time.</p>
                 <p>Fixed the nbt-api link.</p>
            """.trimIndent()
        )
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(
                "name" to projectName,
                "group" to projectGroup,
                "version" to finalVersion,
                "description" to projectDescription,
                "website" to "https://modrinth.com/$projectExt/${projectName.lowercase()}"
            )
        }
    }
}

publishing {
    repositories {
        val repo = if (isBeta) "beta" else "releases"
        maven("https://repo.crazycrew.us/$repo") {
            name = "crazycrew"
            // Used for locally publishing.
            // credentials(PasswordCredentials::class)

            credentials {
                username = System.getenv("REPOSITORY_USERNAME")
                password = System.getenv("REPOSITORY_PASSWORD")
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = projectGroup
            artifactId = "${projectName.lowercase()}-api"
            version = finalVersion

            from(components["java"])
        }
    }
}