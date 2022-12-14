plugins {
    `java-library`

    `maven-publish`

    id("com.modrinth.minotaur") version "2.5.0"

    id("com.github.johnrengelman.shadow") version "7.1.2"
}

project.group = "com.badbones69.crazycrates"
project.version = "${extra["plugin_version"]}"
project.description = "Add unlimited crates to your server with 10 different crate types to choose from!"

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

    compileOnly("io.papermc.paper", "paper-api", "${project.extra["minecraft_version"]}-R0.1-SNAPSHOT")

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

val buildNumber: String? = System.getenv("BUILD_NUMBER")
val buildVersion = "${project.version}-b$buildNumber-SNAPSHOT"

tasks {
    shadowJar {
        if (buildNumber != null) {
            archiveFileName.set("${rootProject.name}-${buildVersion}.jar")
        } else {
            archiveFileName.set("${rootProject.name}-${project.version}.jar")
        }

        listOf(
            "de.tr7zw",
            "org.bstats",
            "dev.triumphteam.cmd"
        ).forEach {
            relocate(it, "${project.group}.plugin.lib.$it")
        }
    }

    modrinth {
        token.set(System.getenv("MODRINTH_TOKEN"))
        projectId.set("crazycrates")
        versionName.set("${rootProject.name} ${project.version} Update")
        versionNumber.set("${project.version}")
        versionType.set("${extra["version_type"]}")
        uploadFile.set(shadowJar.get())

        autoAddDependsOn.set(true)

        gameVersions.addAll(listOf("1.19", "1.19.1", "1.19.2", "1.19.3"))
        loaders.addAll(listOf("paper", "purpur"))

        changelog.set(System.getenv("COMMIT_MESSAGE"))
    }

    compileJava {
        options.release.set(17)
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(
                "name" to rootProject.name,
                "group" to project.group,
                "version" to if (buildNumber != null) buildVersion else project.version,
                "description" to project.description
            )
        }
    }
}

publishing {
    repositories {
        maven("https://repo.crazycrew.us/releases") {
            name = "crazycrew"
            credentials(PasswordCredentials::class)
        }
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = "${project.group}"
            artifactId = rootProject.name.toLowerCase()
            version = "${project.version}"
            from(components["java"])
        }
    }
}