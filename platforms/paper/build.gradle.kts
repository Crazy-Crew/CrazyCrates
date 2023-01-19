plugins {
    id("com.modrinth.minotaur")

    id("xyz.jpenilla.run-paper")

    id("crazycrates.paper-plugin")
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
    api(project(":crazycrates-core"))

    implementation("dev.triumphteam", "triumph-cmd-bukkit", "2.0.0-SNAPSHOT")

    implementation("de.tr7zw", "nbt-data-api", "2.11.1")

    implementation("org.bstats", "bstats-bukkit", "3.0.0")

    compileOnly("me.filoghost.holographicdisplays", "holographicdisplays-api", "3.0.0")

    compileOnly("com.github.decentsoftware-eu", "decentholograms", "2.7.8")

    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7")

    compileOnly("com.Zrips.CMI", "CMI-API", "9.2.6.1")
    compileOnly("net.Zrips.CMILib", "CMI-Lib", "1.2.4.1")

    compileOnly("me.clip", "placeholderapi", "2.11.2") {
        exclude(group = "org.spigotmc", module = "spigot")
        exclude(group = "org.bukkit", module = "bukkit")
    }

    compileOnly("com.github.LoneDev6", "api-itemsadder", "3.0.0")
}

compile {
    tasks {
        shadowJar {
            val versionString = if (isBeta()) getProjectVersion() else getProjectVersion()
            archiveFileName.set("${getProjectName()}+$versionString.jar")

            listOf(
                "de.tr7zw",
                "org.bstats",
                "dev.triumphteam.cmd"
            ).forEach { relocate(it, "${getProjectGroup()}.plugin.library.$it") }
        }

        runServer {
            minecraftVersion("1.19.3")
        }

        modrinth {
            token.set(System.getenv("MODRINTH_TOKEN"))
            projectId.set(getProjectName().toLowerCase())

            versionName.set("${getProjectName()} ${getProjectVersion()}")
            versionNumber.set(getProjectVersion())

            versionType.set(getProjectType())

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
                 <p>We no longer check for updates when a player joins.</p>
                 <p>Switched to Modrinth's v2 api for update checker</p>
                 <p>Update build script</p>
                <h4>Bug Fixes:</h4>
                 <p>N/A</p>
            """.trimIndent()
            )
        }

        processResources {
            filesMatching("plugin.yml") {
                expand(
                    "name" to getProjectName(),
                    "group" to getProjectGroup(),
                    "version" to getProjectVersion(),
                    "description" to getProjectDescription(),
                    "website" to "https://modrinth.com/${getExtension()}/${getProjectName().toLowerCase()}"
                )
            }
        }
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = getProjectGroup()
                artifactId = "${getProjectName().toLowerCase()}-paper"
                version = getProjectVersion()

                from(components["java"])

                pom {
                    name.set(getProjectName())

                    description.set(getProjectDescription())
                    url.set(getProjectGithub())

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
                        connection.set("scm:git:git://github.com/Crazy-Crew/CrazyCrates.git")
                        developerConnection.set("scm:git:ssh://github.com/Crazy-Crew/CrazyCrates.git")
                        url.set(getProjectGithub())
                    }
                }
            }
        }

        repositories {
            val urlExt = if (isBeta()) "beta" else "releases"

            maven("https://repo.crazycrew.us/$urlExt") {
                name = "crazycrew"
                // Used for locally publishing.
                credentials(PasswordCredentials::class)

                // credentials {
                //    username = System.getenv("REPOSITORY_USERNAME")
                //    password = System.getenv("REPOSITORY_PASSWORD")
                //}
            }
        }
    }
}