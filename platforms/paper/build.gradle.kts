@Suppress("DSL_SCOPE_VIOLATION")

plugins {
    id("crazycrates.paper-plugin")

    alias(settings.plugins.minotaur)
    alias(settings.plugins.run.paper)
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

    maven("https://repo.crazycrew.us/libraries/")
}

dependencies {
    api(project(":crazycrates-core"))

    implementation("net.dehya.ruby:ruby-core:0.0.1")
    implementation("net.dehya.ruby:ruby-paper:0.0.1")

    compileOnly(libs.papermc)

    implementation(libs.triumph.cmds)

    implementation(libs.nbt.api)
    implementation(libs.bstats.bukkit)

    compileOnly(libs.holographic.displays)
    compileOnly(libs.decent.holograms)
    compileOnly(libs.cmi.api)
    compileOnly(libs.cmi.lib)

    compileOnly(libs.placeholder.api)

    compileOnly(libs.itemsadder.api)
}

val projectDescription = settings.versions.projectDescription.get()
val projectGithub = settings.versions.projectGithub.get()
val projectGroup = settings.versions.projectGroup.get()
val projectName = settings.versions.projectName.get()
val projectExt = settings.versions.projectExtension.get()

val isBeta = settings.versions.projectBeta.get().toBoolean()

val projectVersion = settings.versions.projectVersion.get()

val finalVersion = if (isBeta) "$projectVersion+Beta" else projectVersion

val projectNameLowerCase = projectName.toLowerCase()

val repo = if (isBeta) "beta" else "releases"
val type = if (isBeta) "beta" else "release"

tasks {
    shadowJar {
        archiveFileName.set("${projectName}+$finalVersion.jar")

        listOf(
            "de.tr7zw.changeme.nbtapi",
            "org.bstats",
            "dev.triumphteam.cmd",
            "net.dehya.ruby"
        ).forEach { relocate(it, "$projectGroup.plugin.library.$it") }
    }

    runServer {
        minecraftVersion("1.19.3")
    }

    modrinth {
        token.set(System.getenv("MODRINTH_TOKEN"))
        projectId.set(projectNameLowerCase)

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
                <h4>Changes:</h4>
                 <p>Added 1.17.1 support back.</p>
                 <p>Added a feature https://github.com/orgs/Crazy-Crew/discussions/19</p>
                <h4>Under the hood changes</h4>
                 <p>Re-organized the build script for the last time.</p>
                 <p>Cleaned up a few pieces of code.</p>
                 <p>No longer check for updates when a player joins.</p>
                 <p>Updated nbt-api artifact id.</p>
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
                "website" to "https://modrinth.com/$projectExt/$projectNameLowerCase"
            )
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = projectGroup
            artifactId = "$projectNameLowerCase-paper"
            version = finalVersion

            from(components["java"])

            pom {
                name.set(projectName)

                description.set(projectDescription)
                url.set(projectGithub)

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://www.opensource.org/licenses/mit-license.php")
                    }
                }

                developers {
                    developer {
                        id.set("ryderbelserion")
                        name.set("Ryder Belserion")
                    }

                    developer {
                        id.set("badbones69")
                        name.set("BadBones69")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/Crazy-Crew/$projectName.git")
                    developerConnection.set("scm:git:ssh://github.com/Crazy-Crew/$projectName.git")
                    url.set(projectGithub)
                }
            }
        }
    }

    repositories {
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
}