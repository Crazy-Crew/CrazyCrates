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
# Take backups before you update just in case!
    
## New Features:
 * Added the ability to require a permission to open a crate.
   * This feature was labeled to be added a while ago but I don't know why it didn't get added. [#594](https://github.com/Crazy-Crew/CrazyCrates/pull/594)
   * New Message: 
     * No-Crate-Permission: '&cYou do not have permission to use that crate.'
   * New Permissions:
     * crazycrates.open.<crate_name>
     * crazycrates.open.* which defaults to true. Set this to false and use the permission above to start blocking crates.
 * Added multiple internal placeholders related to how many times a player has opened a crate or crates
  * %crate_opened% ( Shows a singular crate, it's used in Per-Crate inside messages.yml )
    * This placeholder can also be used in in /crates in the lore when you hover over the item.
  * %crates_opened% ( Shows the total amount of crates opened and is used in the /keys header )
  * All placeholders will return `0` if data not found so that is normal.
 * Added 2 new placeholderapi placeholders
   * `%crazycrates_<crate-name>_opened` ( Shows how many times a player has opened a singular crate. )
   * `%crazycrates_crates_opened%` ( Shows the total amount of crates a player has opened. )
 * Added the ability to run commands when a crate is opened, It defaults to false.
   ```yml
   Crate:
     opening-command:
         # If the commands should be sent or not.
         toggle: false
         # The commands to run when the crate opens.
         # Supports all placeholderapi placeholders
         # Supports %prefix which returns our prefix, %player% which uses the player name
         commands:
             - 'put your command here.'
   ```
 * Added the ability to pick whether you can have random schematics or a single schematic for QuadCrates.
   ```yml
   Crate:
      # This section is related to .nbt files.
      structure:
        # If it should randomly use an .nbt file.
        random: true
        # The file to use inside schematic's folder.
        file: 'classic.nbt'
   ```

## Changes:
 * Added plugin-config.yml
   * Migrated Settings.Toggle-Metrics and Settings.Prefix to plugin-config.yml, It will automatically move your old values to the new config.
   * Metrics actually shuts down when you reload the plugin after turning off metrics.
 * **The plugin prefix is no longer automatically appended, You must add %prefix% to every message.**
 * Added new config options related to gui-customizer and sounds in config.yml
   * Need-Key-Sound-Toggle which defaults to true, Make sure to set it to false if you don't want it.
   * GUI-Customizer-Toggle which defaults to true, Make sure you set it to false if you don't want it.
 * Added extra comments/headers to messages.yml & config.yml

 ### New placeholders:
 #### Old placeholders do still work as they serve a purpose for when you don't need to supply a player.
   * `%crazycrates_<player>_opened%` -> Returns the total amount of crates opened.
   * `%crazycrates_<player>_<crate>_opened%` -> 	Returns the amount of this particular crate opened.
   * `%crazycrates_<player>_<crate>_physical%` -> 	Returns the amount of physical keys a player has in their inventory.
   * `%crazycrates_<player>_<crate>_virtual%` -> Returns the amount of virtual keys a player has.
   * `%crazycrates_<player>_<crate>_total%` -> 	Returns the total amount of virtual and physical keys a player has.

## Fix:
 * Cosmic Crates should no longer error. [#599](https://github.com/Crazy-Crew/CrazyCrates/pull/599)
 * Fixed a bug where it would overwrite the total-crates in data.yml if adding a new crate and other overwriting issues. ( Found in beta testing )
   * You should probably join the beta tester team in our discord... I like direct feedback :)
 * Check if the crate type is cosmic before marking it as "open", We want the key to be taken before being marked as open so people can't abuse it.

## Developers:
 * Added a crate open event.
 #### Deprecation Notice:
  * com.badbones69.crazycrates.api inside the "core" module is marked for removal and is deprecated.
  * An alternative has been added that you can migrate to.
    * us.crazycrew.crazycrates:crazycrates-api:0.1
  * All methods under com.badbones69.crazycrates.api.CrazyManager have been deprecated, Please use the API above!
  * Warnings will show up in your IDE when you go to view or compile your project but backwards compatibility is a thing for now.
 * A fair bit of code cleanup.

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