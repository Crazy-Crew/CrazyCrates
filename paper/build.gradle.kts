plugins {
    alias(libs.plugins.run.paper)

    `paper-plugin`
}

val mcVersion = libs.versions.bundle

dependencies {
    compileOnly(fileTree("$rootDir/libs/compile").include("*.jar"))

    implementation(project(":api"))

    implementation(libs.bundles.triumph)

    implementation(libs.config.me)

    implementation(libs.metrics)

    implementation(libs.vital)

    compileOnly(libs.head.database.api)

    compileOnly(libs.decent.holograms)

    compileOnly(libs.placeholder.api)

    compileOnly(libs.itemsadder.api)

    compileOnly(libs.oraxen.api)
}

tasks {
    runServer {
        jvmArgs("-Dnet.kyori.ansi.colorLevel=truecolor")

        defaultCharacterEncoding = Charsets.UTF_8.name()

        minecraftVersion("1.20.4")
    }

    assemble {
        dependsOn(reobfJar)

        doLast {
            copy {
                from(reobfJar.get())
                into(rootProject.projectDir.resolve("jars"))
            }
        }
    }

    shadowJar {
        archiveClassifier.set("")

        //archiveFileName.set("${rootProject.name}-${rootProject.version}.jar")
        //destinationDirectory.set(rootProject.projectDir.resolve("jars"))

        listOf(
            "dev.triumphteam",
            "org.bstats",
            "ch.jalu"
        ).forEach {
            relocate(it, "libs.$it")
        }
    }

    processResources {
        val properties = hashMapOf(
            "name" to rootProject.name,
            "version" to rootProject.version,
            "group" to rootProject.group,
            "description" to rootProject.description,
            "apiVersion" to "1.20",
            "authors" to listOf("RyderBelserion", "BadBones69"),
            "website" to "https://modrinth.com/plugin/crazycrates"
        )

        inputs.properties(properties)

        filesMatching("plugin.yml") {
            expand(properties)
        }
    }
}