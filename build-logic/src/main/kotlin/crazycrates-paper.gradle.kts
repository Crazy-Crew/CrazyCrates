import gradle.kotlin.dsl.accessors._657ac3b2e5072282e18494eb2b5fc9d6.implementation

plugins {
    id("crazycrates-base")
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

    compileOnly("com.github.decentsoftware-eu:decentholograms:2.2.5")

    compileOnly("io.papermc.paper:paper-api:1.19-R0.1-SNAPSHOT")

    compileOnly("me.clip:placeholderapi:2.11.1") {
        exclude(group = "org.spigotmc", module = "spigot")
        exclude(group = "org.bukkit", module = "bukkit")
    }

    compileOnly("org.apache.commons:commons-text:1.9")

    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
}