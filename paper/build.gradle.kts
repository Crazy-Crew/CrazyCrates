plugins {
    alias(libs.plugins.run.paper)
}

val mcVersion = libs.versions.bundle.get()

dependencies {
    compileOnly(fileTree("$rootDir/libs/compile").include("*.jar"))

    implementation(fileTree("$rootDir/libs/shade").include("*.jar"))

    paperweight.paperDevBundle(mcVersion)

    implementation(project(":api"))

    implementation(libs.decent.holograms)

    implementation(libs.bundles.triumph)

    implementation(libs.config.me)

    implementation(libs.metrics)

    compileOnly(libs.head.database.api)

    compileOnly(libs.placeholder.api)

    compileOnly(libs.itemsadder.api)

    compileOnly(libs.oraxen.api)

    compileOnly(libs.vault)
}

tasks {
    runServer {
        jvmArgs("-Dnet.kyori.ansi.colorLevel=truecolor")

        defaultCharacterEncoding = Charsets.UTF_8.name()

        minecraftVersion(mcVersion)
    }

    assemble {
        dependsOn(reobfJar)
    }

    reobfJar {
        outputJar = rootProject.layout.buildDirectory.file("$rootDir/jars/paper/${rootProject.name.lowercase()}-${rootProject.version}.jar")
    }
}