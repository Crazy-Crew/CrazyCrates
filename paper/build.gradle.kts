plugins {
    id("crazycrates-base")

    id("io.papermc.paperweight.userdev") version "1.3.7"

    id("xyz.jpenilla.run-paper") version "1.0.6"

    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    // PAPI API
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    // MVDW API
    maven("https://repo.mvdw-software.com/content/groups/public/")

    // NBT API
    maven("https://repo.codemc.org/repository/maven-public/")

    // Paper API
    maven("https://repo.papermc.io/repository/maven-public/")

    // Triumph Team
    maven("https://repo.triumphteam.dev/snapshots/")
}

dependencies {
    implementation(project(":common"))

    // Paper API
    paperDevBundle("1.19-R0.1-SNAPSHOT")

    compileOnly(libs.paper)

    // Paper Lib
    implementation(libs.paper.lib)

    // Paper Cloud Commands
    // compileOnly(libs.paper.command.cloud)

    // Misc
    implementation(libs.bstats.bukkit)

    // NBT API - TODO() Replace this by using PDC.
    implementation(libs.nbt.api)

    // Vault API
    compileOnly(libs.vault.api)

    // Holograms
    compileOnly(libs.holographic.displays)
    compileOnly(libs.decent.holograms)

    // Placeholders

    compileOnly(libs.placeholder.api) {
        exclude(group = "org.spigotmc", module = "spigot")
        exclude(group = "org.bukkit", module = "bukkit")
    }

    // Triumph Team
    // implementation(libs.triumph.gui.bukkit)
}

tasks {
    reobfJar {
        outputJar.set(rootProject.layout.buildDirectory.file("libs/${rootProject.name}-[1.18-1.19]-${rootProject.version}.jar"))
    }

    assemble {
        dependsOn(reobfJar)
    }

    processResources {
        filesMatching("plugin.yml") {
            expand (
                "name" to rootProject.name,
                "group" to rootProject.group,
                "version" to rootProject.version,
                "description" to rootProject.description
            )
        }
    }

    shadowJar {
        listOf(
            "de.tr7zw",
            "org.bstats",
            "io.papermc"
        ).forEach {
            relocate(it, "${rootProject.group}.plugin.lib.$it")
        }
    }
}