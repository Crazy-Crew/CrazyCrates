plugins {
    id("paper-plugin")
    id("publish-task")

    id("xyz.jpenilla.run-paper") version "2.1.0"
}

repositories {
    flatDir {
        dirs("libs")
    }
}

dependencies {
    api(project(":crazycrates-api"))

    compileOnly("cmi-api:CMI-API")
    compileOnly("cmi-lib:CMILib")

    compileOnly(libs.decent.holograms)
    compileOnly(libs.fancy.holograms)
    compileOnly(libs.fancy.npcs)

    compileOnly(libs.placeholder.api)
    compileOnly(libs.itemsadder.api)

    implementation(libs.bstats.bukkit)

    implementation(libs.triumph.cmds)

    implementation(libs.nbt.api)
}

tasks {
    reobfJar {
        val file = File("$rootDir/jars")

        if (!file.exists()) file.mkdirs()

        outputJar.set(layout.buildDirectory.file("$file/${rootProject.name}-${rootProject.version}.jar"))
    }

    shadowJar {
        listOf(
            "de.tr7zw.changeme.nbtapi",
            "dev.triumphteam",
            "org.bstats"
        ).forEach { pack -> relocate(pack, "${rootProject.group}.$pack") }
    }

    runServer {
        minecraftVersion("1.20")
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(
                "name" to rootProject.name,
                "group" to rootProject.group,
                "version" to rootProject.version,
                "description" to rootProject.description,
                "website" to "https://modrinth.com/plugin/${rootProject.name.lowercase()}"
            )
        }
    }
}