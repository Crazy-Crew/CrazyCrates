import io.papermc.hangarpublishplugin.model.Platforms

plugins {
    alias(libs.plugins.paperweight)
    alias(libs.plugins.shadowjar)

    alias(libs.plugins.modrinth)

    alias(libs.plugins.runpaper)

    alias(libs.plugins.hangar)
}

base {
    archivesName.set(rootProject.name)
}

val mcVersion = rootProject.properties["minecraftVersion"] as String

dependencies {
    api(project(":common"))

    implementation(libs.cluster.paper)

    implementation(libs.triumph.cmds)

    implementation(libs.metrics)

    implementation(libs.nbtapi)

    compileOnly(libs.holographicdisplays)

    compileOnly(libs.decentholograms)

    compileOnly(libs.placeholderapi)

    compileOnly(libs.itemsadder)

    compileOnly(libs.oraxen)

    compileOnly(fileTree("libs").include("*.jar"))

    paperweightDevelopmentBundle("io.papermc.paper:dev-bundle:$mcVersion-R0.1-SNAPSHOT")
}

val isBeta: Boolean get() = rootProject.extra["isBeta"]?.toString()?.toBoolean() ?: false
val type = if (isBeta) "Beta" else "Release"

val description = """
## Configs:
 * Removed plugin-config.yml as it was just weird, Options in there will migrate to config.yml automatically.
    
## Changes:
 * Nested placeholders now work *with limitations
  * %crazycrates_<player>_<crate>_opened% must be done like %crazycrates_{player_name}_your_crate_opened%
 * {player_name} can be replaced with almost any player variable from https://github.com/PlaceholderAPI/PlaceholderAPI/wiki/Placeholders
 * /crazycrates admin-help has been removed.
  * /crazycrates help now has a permission check if the player has crazycrates.admin-access
  * The permission crazycrates.command.player.help has been changed to crazycrates.help which defaults to TRUE
 * Console can now use /crazycrates help.

## Other:
 * [Feature Requests](https://github.com/Crazy-Crew/${rootProject.name}/issues)
 * [Bug Reports](https://github.com/Crazy-Crew/${rootProject.name}/issues)
"""

val file = project.layout.buildDirectory.file("libs/${rootProject.name}-${rootProject.version}.jar").get().asFile

val component: SoftwareComponent = components["java"]

tasks {
    // Publish to hangar.papermc.io.
    hangarPublish {
        publications.register("plugin") {
            version.set("$rootProject.version")

            id.set(rootProject.name)

            channel.set(type)

            changelog.set(description)

            apiKey.set(System.getenv("hangar_key"))

            platforms {
                register(Platforms.PAPER) {
                    jar.set(file)

                    platformVersions.set(listOf(mcVersion))
                }
            }
        }
    }

    // Publish to modrinth.
    modrinth {
        autoAddDependsOn.set(false)

        token.set(System.getenv("modrinth_token"))

        projectId.set(rootProject.name.lowercase())

        versionName.set("${rootProject.name} ${rootProject.version}")

        versionNumber.set("${rootProject.version}")

        versionType.set(type.lowercase())

        uploadFile.set(file)

        gameVersions.add(mcVersion)

        changelog.set(description)

        loaders.addAll("paper", "purpur")
    }

    // Runs a test server.
    runServer {
        jvmArgs("-Dnet.kyori.ansi.colorLevel=truecolor")

        minecraftVersion("1.20.2")
    }

    // Assembles the plugin.
    assemble {
        dependsOn(reobfJar)
    }

    shadowJar {
        archiveClassifier.set("")

        exclude("META-INF/**")

        listOf(
                "com.ryderbelserion.cluster.paper",
                "de.tr7zw.changeme.nbtapi",
                "dev.triumphteam.cmd",
                "org.bstats"
        ).forEach {
            relocate(it, "libs.$it")
        }
    }

    processResources {
        val properties = hashMapOf(
                "name" to rootProject.name,
                "version" to rootProject.version,
                "group" to rootProject.group,
                "description" to rootProject.description,
                "apiVersion" to rootProject.properties["apiVersion"],
                "authors" to rootProject.properties["authors"],
                "website" to rootProject.properties["website"]
        )

        inputs.properties(properties)

        filesMatching("plugin.yml") {
            expand(properties)
        }
    }
}