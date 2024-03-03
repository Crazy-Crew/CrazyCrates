plugins {
    id("io.papermc.paperweight.userdev")

    id("xyz.jpenilla.run-paper")

    id("root-plugin")
}

base {
    archivesName.set(rootProject.name)
}

repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    maven("https://repo.papermc.io/repository/maven-public/")

    maven("https://repo.codemc.io/repository/maven-public/")

    maven("https://repo.triumphteam.dev/snapshots/")

    maven("https://repo.oraxen.com/releases/")

    flatDir { dirs("libs") }
}

val mcVersion = providers.gradleProperty("mcVersion").get()

project.version = if (System.getenv("BUILD_NUMBER") != null) "${rootProject.version}-${System.getenv("BUILD_NUMBER")}" else rootProject.version

dependencies {
    paperweight.paperDevBundle("$mcVersion-R0.1-SNAPSHOT")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }

    runServer {
        jvmArgs("-Dnet.kyori.ansi.colorLevel=truecolor")

        defaultCharacterEncoding = Charsets.UTF_8.name()

        minecraftVersion(mcVersion)
    }

    modrinth {
        loaders.addAll("paper", "purpur")
    }
}