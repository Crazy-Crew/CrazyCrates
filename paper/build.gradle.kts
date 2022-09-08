plugins {
    id("crazycrates-base")

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

    compileOnly(libs.paper) {
        exclude(group = "com.google.code.gson", module = "gson")
    }

    implementation(libs.google.guice)

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
    compileOnly(libs.triumph.gui.bukkit)
}

tasks {
    shadowJar {
        archiveFileName.set("${rootProject.name}-${rootProject.version}-PAPER.jar")

        listOf(
            "org.bstats",
            "com.badbones69.crazycrates.common"
        ).forEach {
            relocate(it, "${rootProject.group}.plugin.lib.$it")
        }

        doLast {
            copy {
                from("build/libs/${rootProject.name}-${rootProject.version}-PAPER.jar")
                into(rootProject.layout.buildDirectory.dir("libs"))
            }
        }
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
}