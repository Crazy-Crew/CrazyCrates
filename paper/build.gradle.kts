import net.minecrell.pluginyml.bukkit.BukkitPluginDescription;

plugins {
    id("crazycrates-base")

    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"

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
    compileOnly(libs.paper)

    // Paper Lib
    implementation(libs.paper.lib)

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
    compileOnly(libs.mvdw.placeholder.api) {
        exclude(group = "org.spigotmc", module = "spigot")
        exclude(group = "org.bukkit", module = "bukkit")
        exclude(group = "be.maximvdw", module = "MVdWUpdater")
    }

    compileOnly(libs.placeholder.api) {
        exclude(group = "org.spigotmc", module = "spigot")
        exclude(group = "org.bukkit", module = "bukkit")
    }

    // Triumph Team
    implementation(libs.triumph.bukkit)
    implementation(libs.triumph.gui.bukkit)
}

//idea {
//    module {
//        isDownloadJavadoc = true
//        isDownloadSources = true
//    }
//}

tasks {
    shadowJar {
        minimize()

        archiveFileName.set("${rootProject.name}-[1.18-1.19]-v${rootProject.version}.jar")

        listOf(
            "de.tr7zw",
            "org.bstats",
            "io.papermc",
            "dev.triumphteam.cmd",
            "dev.triumphteam.gui"
        ).forEach {
            relocate(it, "${rootProject.group}.plugin.lib.$it")
        }
    }
}

bukkit {
    name = "CrazyCrates"

    main = "${rootProject.group}.CrazyCrates"

    apiVersion = "1.18"

    version = rootProject.version.toString()

    description = rootProject.description.toString()

    authors = listOf(
        "BadBones69",
        "Ryder Belserion"
    )

    softDepend = listOf(
        "HolographicDisplays",
        "DecentHolograms",
        "PlaceholderAPI"
    )

    commands {
        register("crates") {
            description = "The base command for Crazy Crates."
            aliases = listOf(
                "crate",
                "cc",
                "crazycrates"
            )
        }

        register("keys") {
            description = "Views the amount of keys you/others have."
            aliases = listOf(
                "key"
            )
        }

        libraries = listOf(
            "org.jetbrains.kotlin:kotlin-stdlib:1.7.10"
        )
    }

    val permTrue = BukkitPluginDescription.Permission.Default.TRUE
    val permFalse = BukkitPluginDescription.Permission.Default.FALSE
    val permOP = BukkitPluginDescription.Permission.Default.OP
    val permNotOP = BukkitPluginDescription.Permission.Default.NOT_OP

    permissions {
        register("crazycrates.command.player.menu") {
            default = permTrue
        }

        register("crazycrates.command.player.key") {
            default = permOP
        }

        register("crazycrates.command.player.transfer") {
            default = permOP
        }

        register("crazycrates.command.player.help") {
            children = listOf(
                "crazycrates.command.player.key",
            )
        }

        register("crazycrates.command.player.key.others") {
            default = permOP
            children = listOf(
                "crazycrates.command.player.key",
            )
        }

        register("crazycrates.command.players.*") {
            default = permOP
            children = listOf(
                "crazycrates.command.player.key.others",
                "crazycrates.command.player.help",
                "crazycrates.command.player.menu"
            )
        }

        // Admin permissions
        register("crazycrates.command.admin.teleport") {
            default = permOP
        }

        register("crazycrates.command.admin.givekey") {
            default = permOP
        }

        register("crazycrates.command.admin.set") {
            default = permOP
        }

        register("crazycrates.command.admin.additem") {
            default = permOP
        }

        register("crazycrates.command.admin.access") {
            default = permOP
        }

        register("crazycrates.command.admin.preview") {
            default = permOP
        }

        register("crazycrates.command.admin.open") {
            default = permOP
        }

        register("crazycrates.command.admin.reload") {
            default = permOP
        }

        register("crazycrates.command.admin.debug") {
            default = permOP
        }

        register("crazycrates.command.admin.convert") {
            default = permOP
        }

        register("crazycrates.command.admin.schematic.save") {
            default = permOP
        }

        register("crazycrates.command.admin.schematic.set") {
            default = permOP
        }

        register("crazycrates.command.admin.list") {
            default = permOP
            children = listOf(
                "crazycrates.command.admin.teleport"
            )
        }

        register("crazycrates.command.admin.open.others") {
            default = permOP
            children = listOf(
                "crazycrates.command.admin.open"
            )
        }

        register("crazycrates.command.admin.setmenu") {
            default = permOP
            children = listOf(
                "crazycrates.command.admin.set"
            )
        }

        register("crazycrates.command.admin.takekey") {
            default = permOP
            children = listOf(
                "crazycrates.command.player.transfer"
            )
        }

        register("crazycrates.command.admin.help") {
            default = permOP
            children = listOf(
                "crazycrates.command.admin.reload"
            )
        }

        register("crazycrates.command.admin.forceopen") {
            default = permOP
            children = listOf(
                "crazycrates.command.admin.open",
                "crazycrates.command.admin.open.others"
            )
        }

        register("crazycrates.command.admin.giveall") {
            default = permOP
            children = listOf(
                "crazycrates.command.admin.givekey",
                "crazycrates.command.admin.takekey",
                "crazycrates.command.player.transfer"
            )
        }

        register("crazycrates.command.admin.schematic.*") {
            default = permOP
            children = listOf(
                "crazycrates.command.admin.schematic.save",
                "crazycrates.command.admin.schematic.set"
            )
        }

        register("crazycrates.command.admin.*") {
            default = permOP
            children = listOf(
                "crazycrates.command.admin.schematic.*",
                "crazycrates.command.admin.open.others",
                "crazycrates.command.admin.forceopen",
                "crazycrates.command.admin.preview",
                "crazycrates.command.admin.convert",
                "crazycrates.command.admin.setmenu",
                "crazycrates.command.admin.giveall",
                "crazycrates.command.admin.additem",
                "crazycrates.command.admin.access",
                "crazycrates.command.admin.debug",
                "crazycrates.command.admin.help",
                "crazycrates.command.admin.list"
            )
        }
    }
}