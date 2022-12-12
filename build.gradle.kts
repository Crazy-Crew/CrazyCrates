plugins {
    java

    id("com.modrinth.minotaur") version "2.+"

    id("com.github.johnrengelman.shadow") version "7.1.2"
}

val buildNumber: String? = System.getenv("BUILD_NUMBER")

val jenkinsVersion = "1.11.8-b$buildNumber"

group = "com.badbones69.crazycrates"
version = "1.11.8"
description = "Add unlimited crates to your server with 10 different crate types to choose from!"

repositories {
    /**
     * PAPI Team
     */
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    /**
     * NBT Team
     */
    maven("https://repo.codemc.org/repository/maven-public/")

    /**
     * Paper Team
     */
    maven("https://repo.papermc.io/repository/maven-public/")

    /**
     * Triumph Team
     */
    maven("https://repo.triumphteam.dev/snapshots/")

    /**
     * CrazyCrew Team
     */
    maven("https://repo.crazycrew.us/plugins/")

    /**
     * Minecraft Team
     */
    maven("https://libraries.minecraft.net/")

    /**
     * Vault Team
     */
    maven("https://jitpack.io/")

    /**
     * Everything else we need.
     */
    mavenCentral()
}

dependencies {
    implementation("dev.triumphteam", "triumph-cmd-bukkit", "2.0.0-SNAPSHOT")

    implementation("de.tr7zw", "nbt-data-api", "2.11.0")

    implementation("org.bstats", "bstats-bukkit", "3.0.0")

    compileOnly("io.papermc.paper", "paper-api", "1.19.3-R0.1-SNAPSHOT")

    compileOnly("me.filoghost.holographicdisplays", "holographicdisplays-api", "3.0.0")

    compileOnly("com.github.decentsoftware-eu", "decentholograms", "2.7.7")

    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7")

    compileOnly("com.Zrips.CMI", "CMI-API", "9.2.6.1")
    compileOnly("CMILib", "CMILib", "1.2.3.7")

    compileOnly("me.clip", "placeholderapi", "2.11.2") {
        exclude(group = "org.spigotmc", module = "spigot")
        exclude(group = "org.bukkit", module = "bukkit")
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    shadowJar {
        if (buildNumber != null) {
            archiveFileName.set("${rootProject.name}-[v${jenkinsVersion}].jar")
        } else {
            archiveFileName.set("${rootProject.name}-[v${rootProject.version}].jar")
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
        versionName.set("${rootProject.name} ${rootProject.version} Update")
        versionNumber.set("${rootProject.version}")
        versionType.set("release")
        uploadFile.set(jar(rootProject.name))

        gameVersions.addAll(listOf("1.19", "1.19.1", "1.19.2", "1.19.3"))
        loaders.addAll(listOf("paper", "purpur"))

        changelog.set("""
                <h3>The first release for CrazyCrates on Modrinth! 🎉🎉🎉🎉🎉<h3><br>
                
                <h2>Changes:</h2>
                 <p>Added 1.19.3 support.</p>
                 <p>Added a new command called /cc give-random which allows you to give random keys from your available crates.</p>
                 <p>Added the ability to have a colored name on the glass panes in the preview menus.</p>
                 <p>Fixed a bug where holograms would duplicate.</p>
            """.trimIndent())
    }

    compileJava {
        options.release.set(17)
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(
                "name" to rootProject.name,
                "group" to rootProject.group,
                "version" to rootProject.version,
                "description" to rootProject.description
            )
        }
    }
}

fun jar(name: String): RegularFile {
    return rootProject.layout.buildDirectory.file("libs/${name}-[v${rootProject.version}].jar").get();
}