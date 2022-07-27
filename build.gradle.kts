plugins {
    java
    // idea

    kotlin("jvm") version "1.7.0"

    id("com.github.johnrengelman.shadow") version "7.1.2"
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

//idea {
//    module {
//        isDownloadJavadoc = true
//        isDownloadSources = true
//    }
//}

tasks.compileKotlin {
    kotlinOptions {
        jvmTarget = "17"
    }
}

group = "com.badbones69.crazycrates"
version = "1.11.5-${System.getenv("BUILD_NUMBER") ?: "SNAPSHOT"}"
description = "Quality crates for free!"

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

    // Our Repo
    maven("https://repo.badbones69.com/releases/")

    // Vault API
    maven("https://jitpack.io/")

    mavenCentral()
    mavenLocal()
}

dependencies {

    implementation("dev.triumphteam:triumph-cmd-bukkit:2.0.0-SNAPSHOT")

    implementation("org.bstats:bstats-bukkit:3.0.0")

    implementation("de.tr7zw:nbt-data-api:2.10.0")

    implementation("io.papermc:paperlib:1.0.7")

    compileOnly("com.gmail.filoghost.holographicdisplays:holographicdisplays-api:2.4.9")

    compileOnly("be.maximvdw:MVdWPlaceholderAPI:3.1.1-SNAPSHOT") {
        exclude(group = "org.spigotmc", module = "spigot")
        exclude(group = "org.bukkit", module = "bukkit")
        exclude(group = "be.maximvdw", module = "MVdWUpdater")
    }

    compileOnly("com.github.decentsoftware-eu:decentholograms:2.5.2")

    compileOnly("io.papermc.paper:paper-api:1.19-R0.1-SNAPSHOT")

    compileOnly("me.clip:placeholderapi:2.11.2") {
        exclude(group = "org.spigotmc", module = "spigot")
        exclude(group = "org.bukkit", module = "bukkit")
    }

    compileOnly("org.apache.commons:commons-text:1.9")

    compileOnly("com.github.MilkBowl:VaultAPI:1.7")

    compileOnly(kotlin("stdlib", "1.7.0"))
}

tasks {

    shadowJar {
        minimize()

        archiveFileName.set("${rootProject.name}-[1.18-1.19]-v${rootProject.version}.jar")

        listOf(
            "de.tr7zw",
            "org.bstats",
            "io.papermc",
            "dev.triumphteam.cmd"
        ).forEach {
            relocate(it, "${rootProject.group}.plugin.lib.$it")
        }
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