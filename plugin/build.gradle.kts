plugins {
    id("crates.common")

    id("com.github.johnrengelman.shadow") version "7.1.2"
}

dependencies {

    compileOnly("com.gmail.filoghost.holographicdisplays:holographicdisplays-api:2.4.9")

    compileOnly("com.github.decentsoftware-eu:decentholograms:2.2.5")

    compileOnly("be.maximvdw:MVdWPlaceholderAPI:3.1.1-SNAPSHOT") {
        exclude(group = "org.spigotmc", module = "spigot")
        exclude(group = "org.bukkit", module = "bukkit")
        exclude(group = "be.maximvdw", module = "MVdWUpdater")
    }

    implementation("de.tr7zw:nbt-data-api:2.10.0")

    implementation("org.bstats:bstats-bukkit:3.0.0")

    implementation("io.papermc:paperlib:1.0.7")

    compileOnly("me.clip:placeholderapi:2.11.1") {
        exclude(group = "org.spigotmc", module = "spigot")
        exclude(group = "org.bukkit", module = "bukkit")
    }

    implementation(project(":api"))
}

tasks {
    shadowJar {
        archiveFileName.set("Crazy-Crates-[v${rootProject.version}].jar")

        val path = "com.badbones69.crazycrates.libs"

        relocate("de.tr7zw", path)
        relocate("org.bstats", path)
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(
                "version" to rootProject.version
            )
        }
    }
}