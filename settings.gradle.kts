pluginManagement {
    repositories {
        maven("https://repo.papermc.io/repository/maven-public/")

        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        maven("https://repo.papermc.io/repository/maven-public/")

        gradlePluginPortal()
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            version("adventure4", "4.16.0")
            version("cluster", "6.4")

            library("holographicdisplays", "me.filoghost.holographicdisplays", "holographicdisplays-api").version("3.0.0")

            library("decentholograms", "com.github.decentsoftware-eu", "decentholograms").version("2.8.6")

            library("triumphcmds", "dev.triumphteam", "triumph-cmd-bukkit").version("2.0.0-SNAPSHOT")

            library("cluster_paper", "com.ryderbelserion.cluster", "paper").versionRef("cluster")
            library("cluster_api", "com.ryderbelserion.cluster", "paper").versionRef("cluster")

            library("adventure4", "net.kyori", "adventure-text-minimessage").versionRef("adventure4")
            library("minimessage4", "net.kyori", "adventure-api").versionRef("adventure4")

            library("itemsadder", "com.github.LoneDev6", "api-itemsadder").version("3.6.1")

            library("placeholderapi", "me.clip", "placeholderapi").version("2.11.5")
            library("vault", "com.github.MilkBowl", "VaultAPI").version("1.7.1")
            library("metrics", "org.bstats", "bstats-bukkit").version("3.0.2")
            library("nbtapi", "de.tr7zw", "item-nbt-api").version("2.12.2")
            library("oraxen", "io.th0rgal", "oraxen").version("1.164.0")
            library("configme", "ch.jalu", "configme").version("1.4.1")

            bundle("adventure", listOf("adventure4", "minimessage4"))
        }
    }
}

rootProject.name = "CrazyCrates"

include("api")
include("paper")
//include("fabric")