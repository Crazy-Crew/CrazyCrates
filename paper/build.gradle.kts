plugins {
    id("crazycrates-paper")

    id("com.github.johnrengelman.shadow") version "7.1.2"
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