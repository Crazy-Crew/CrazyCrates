plugins {
    id("crates.base-conventions")
    id("crates.nms-conventions")

    id("me.mattstudios.triumph") version "0.2.7"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("xyz.jpenilla.run-paper") version "1.0.6"
}

dependencies {
    // Module

    implementation(project(":api"))

    // Paper
    paperDevBundle("1.18.2-R0.1-SNAPSHOT")

    // Placeholders

    compileOnly("me.clip:placeholderapi:2.11.1") {
        exclude(group = "org.spigotmc", module = "spigot")
    }

    compileOnly("be.maximvdw:MVdWPlaceholderAPI:3.1.1-SNAPSHOT") {
        exclude(group = "org.spigotmc", module = "spigot")
        exclude(group = "be.maximvdw", module = "MVdWUpdater")
    }

    // Holograms
    compileOnly("com.gmail.filoghost.holographicdisplays:holographicdisplays-api:2.4.9")

    compileOnly("com.github.decentsoftware-eu:decentholograms:2.2.5")

    // BStats | NBT Api
    implementation("org.bstats:bstats-bukkit:3.0.0")

    implementation("de.tr7zw:item-nbt-api:2.9.2")
}

tasks {
    runServer {
        minecraftVersion("1.18.2")
    }
}

bukkit {
    name = "CrazyCrates"
    apiVersion = "1.18"
    authors = listOf(
        "Ryder",
        "Badbones69"
    )
    version = "${project.version}"
}