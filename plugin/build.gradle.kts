plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

dependencies {

    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")

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

    shadowJar {
        destinationDirectory.set(project.file("F:/Crazy Crew/..Test Server/plugins"))

        archiveFileName.set("${rootProject.name}-${rootProject.version}.jar")

        relocate("org.bstats", "com.badbones69.libs.bstats")
        relocate("de.tr7zw", "com.badbones69.libs.nbt")
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    compileKotlin {
        kotlinOptions {
            jvmTarget = "17"
        }
    }

    processResources {
        filesMatching("plugin.yml") {
            expand (
                "name" to rootProject.name,
                "group" to rootProject.group,
                "version" to rootProject.version,
                "description" to rootProject.description,
                "apiVersion" to "1.18"
            )
        }
    }
}