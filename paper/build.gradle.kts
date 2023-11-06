import io.papermc.hangarpublishplugin.model.Platforms

plugins {
    alias(libs.plugins.modrinth)
    alias(libs.plugins.hangar)

    id("xyz.jpenilla.run-paper")

    id("paper-plugin")
}

project.group = "${rootProject.group}.paper"

repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    maven("https://repo.codemc.org/repository/maven-public/")

    maven("https://repo.crazycrew.us/first-party/")

    maven("https://repo.crazycrew.us/third-party/")

    flatDir { dirs("libs") }
}

dependencies {
    api(project(":common"))

    api(project(":core"))

    implementation(libs.triumphcmds)

    implementation(libs.metrics)

    implementation(libs.nbtapi)

    compileOnly(libs.holographicdisplays)

    compileOnly(libs.decentholograms)

    compileOnly(libs.placeholderapi)

    compileOnly(libs.itemsadder)

    compileOnly(libs.oraxen)

    compileOnly(fileTree("libs").include("*.jar"))
}

val component: SoftwareComponent = components["java"]

tasks {
    runServer {
        jvmArgs("-Dnet.kyori.ansi.colorLevel=truecolor")

        minecraftVersion("1.20.2")
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = rootProject.group.toString()
                artifactId = "${rootProject.name.lowercase()}-${project.name.lowercase()}-api"
                version = rootProject.version.toString()

                from(component)
            }
        }
    }

    shadowJar {
        listOf(
            "de.tr7zw.changeme.nbtapi",
            "org.bstats",
            "ch.jalu"
        ).forEach {
            relocate(it, "libs.$it")
        }
    }

    processResources {
        val props = mapOf(
            "name" to rootProject.name,
            "group" to project.group.toString(),
            "version" to rootProject.version,
            "description" to rootProject.description,
            "authors" to rootProject.properties["authors"],
            "apiVersion" to "1.20",
            "website" to "https://modrinth.com/plugin/${rootProject.name.lowercase()}"
        )

        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}

val isSnapshot = true
val type = if (isSnapshot) "beta" else "release"
val other = if (isSnapshot) "Beta" else "Release"

val file = file("${rootProject.rootDir}/jars/${rootProject.name}-${rootProject.version}.jar")

val description = """
## Fixes:
  * Fix crate type checks.
  * Don't load files as a yaml configuration if they don't end in .yml.
  * Fixed command typo in CrateExample.yml.
  * Fixed /cc set.
  * Added Menu crate to tab complete.
  * Filtered Menu from metrics as that isn't a crate type.
    
## Changes:
  * Add extra isLogging() checks
  * If no display name option in the crate config prize section is present, The material name will be used for %reward%
  * Inventories are no longer checked by if the view matches, This led to you being able to name your inventory Enchant and now you can't use the Enchant table
   * We use inventory holders instead now which is the preferred way. This includes the animated menus.
  * Use customizable message for when getting keys out of the admin menu.

## New Features:
  * A directory called examples will be re-generated on every startup and /crazycrates reload to always ensure that you get fresh example files if needed.

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

        channel.set(if (isSnapshot) "Beta" else "Release")

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