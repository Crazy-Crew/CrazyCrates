plugins {
    id("paper-plugin")
    id("library-plugin")

    id("xyz.jpenilla.run-paper") version "2.0.1"
}

dependencies {
    api(project(":crazycrates-api"))
    api(libs.crazycore)

    compileOnly(libs.holographic.displays)
    compileOnly(libs.decent.holograms)

    compileOnly(libs.placeholder.api)
    compileOnly(libs.itemsadder.api)

    compileOnly(libs.bstats.bukkit)

    compileOnly(libs.triumph.cmds)

    compileOnly(libs.cmi.api)
    compileOnly(libs.cmi.lib)
    compileOnly(libs.nbt.api)

    compileOnly("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    compileOnly("ch.jalu:configme:1.3.0")
}

tasks {
    reobfJar {
        val file = File("$rootDir/jars")

        if (!file.exists()) file.mkdirs()

        outputJar.set(layout.buildDirectory.file("$file/${rootProject.name}-${rootProject.version}.jar"))
    }

    runServer {
        minecraftVersion("1.19.4")
    }
}