import io.papermc.hangarpublishplugin.model.Platforms

plugins {
    alias(libs.plugins.modrinth)
    alias(libs.plugins.hangar)

    id("paper-plugin")
}

project.group = "${rootProject.group}.paper"
project.version = rootProject.version

repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    maven("https://repo.aikar.co/content/groups/aikar/")

    maven("https://repo.triumphteam.dev/snapshots/")

    flatDir { dirs("libs") }
}

dependencies {
    implementation(project(":common"))

    implementation("org.bstats", "bstats-bukkit", "3.0.2")

    implementation(libs.cluster.bukkit.api) {
        exclude("com.ryderbelserion.cluster", "cluster-api")
    }

    implementation("dev.triumphteam", "triumph-cmd-bukkit", "2.0.0-SNAPSHOT")

    implementation("dev.triumphteam", "triumph-gui", "3.1.2") {
        exclude("net.kyori", "*")
    }

    compileOnly(fileTree("libs").include("*.jar"))

    compileOnly("me.filoghost.holographicdisplays", "holographicdisplays-api", "3.0.0")

    compileOnly("com.github.decentsoftware-eu", "decentholograms","2.8.4")

    compileOnly("com.github.LoneDev6", "API-ItemsAdder", "3.5.0b")

    compileOnly("com.github.oraxen", "oraxen", "1.160.0") {
        exclude("*", "*")
    }

    compileOnly("me.clip", "placeholderapi", "2.11.4")
}

tasks {
    shadowJar {
        listOf(
            "ch.jalu",
            "org.bstats",
            "org.jetbrains",
            "dev.triumphteam.gui",
            "dev.triumphteam.cmd",
        ).forEach {
            relocate(it, "libs.$it")
        }
    }

    processResources {
        val props = mapOf(
            "name" to rootProject.name,
            "group" to project.group,
            "version" to rootProject.version,
            "description" to rootProject.description,
            "authors" to rootProject.properties["authors"],
            "apiVersion" to rootProject.properties["apiVersion"],
            "website" to rootProject.properties["website"],
        )

        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }
}

val isSnapshot = true
val type = if (isSnapshot) "beta" else "release"
val other = if (isSnapshot) "Beta" else "Release"

val file = file("${rootProject.rootDir}/jars/${rootProject.name}-${rootProject.version}.jar")

val description = """
# Notice: This is considered a major update and is currently in beta, You should only use this build if you are fine with the following:
 * A) You will have bugs.
 * B) None of your files can be migrated. You should only use this on a **fresh server**. Do not update if you already have a live server.
 * C) If you do use it on a live fresh server, Please take backups.
 * D) **You should really test everything on a local server first.**

# If you are fine with all that, You can download but please read the changelogs, Everything in this version and any version labeled beta after is subject to change a lot.

## Changes:
 * Replaced plugin.yml with paper-plugin.yml meaning the plugin will only be recognized by paper servers.
 * Updated the config.yml format.
 * Updated the format of the `crates` files in the `crates` folder.
 * Keys are no longer checked by lore or name, They are only checked by a unique and lightweight identifier attached to the keys.

## New Features:
 * Added plugin-config.yml with the following options:
   * Ability to change the plugin locale
   * Ability to turn off verbose logging
   * Properly shut off metrics ( requires a server start )
   * Change the command prefix
 * messages.yml has been moved to the `locale` folder as `en-US.yml`
  * You can now copy and paste this file, Change it in the `plugin-config.yml`
 ### Crates:
  * Added the ability to set slots for prizes so you can re-organize your prizes however you want.
  * Added extra true/false toggles to avoid confusion.
  * Added the ability to disable the crate in each crate file.

## Other:
 * [Feature Requests](https://github.com/Crazy-Crew/${rootProject.name}/issues)
 * [Bug Reports](https://github.com/Crazy-Crew/${rootProject.name}/issues)
""".trimIndent()

val versions = listOf(
    "1.20",
    "1.20.1",
    "1.20.2"
)

modrinth {
    autoAddDependsOn.set(false)

    token.set(System.getenv("modrinth_token"))

    projectId.set(rootProject.name.lowercase())

    versionName.set("${rootProject.name} ${rootProject.version}")
    versionNumber.set("${rootProject.version}")

    versionType.set(type)

    uploadFile.set(file("${rootProject.rootDir}/jars/${rootProject.name}-${rootProject.version}.jar"))

    gameVersions.addAll(versions)

    changelog.set(description)

    loaders.addAll("paper", "purpur")
}

hangarPublish {
    publications.register("plugin") {
        version.set(rootProject.version as String)

        id.set(rootProject.name)

        channel.set(other)

        changelog.set(description)

        apiKey.set(System.getenv("hangar_key"))

        platforms {
            register(Platforms.PAPER) {
                jar.set(file("${rootProject.rootDir}/jars/${rootProject.name}-${rootProject.version}.jar"))
                platformVersions.set(versions)
            }
        }
    }
}