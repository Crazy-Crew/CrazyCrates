plugins {
    alias(libs.plugins.paperweight)
    alias(libs.plugins.runPaper)
    alias(libs.plugins.shadow)

    id("crates.base")
}

repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    maven("https://repo.triumphteam.dev/snapshots/")

    maven("https://repo.fancyplugins.de/releases/")

    maven("https://repo.nexomc.com/snapshots/")

    maven("https://repo.oraxen.com/releases/")
}

dependencies {
    paperweight.paperDevBundle(libs.versions.paper)

    implementation(projects.crazycratesCore)

    implementation(libs.triumph.cmds)

    implementation(libs.fusion.paper) {
        exclude("org.yaml")
    }

    implementation(libs.metrics)

    compileOnly(libs.bundles.dependencies)
    compileOnly(libs.bundles.shared)
    compileOnly(libs.bundles.crates)
}

tasks {
    runServer {
        jvmArgs("-Dnet.kyori.ansi.colorLevel=truecolor")

        defaultCharacterEncoding = Charsets.UTF_8.name()

        minecraftVersion(libs.versions.minecraft.get())
    }
}