import org.gradle.kotlin.dsl.support.uppercaseFirstChar

plugins {
    alias(libs.plugins.paperweight)
    alias(libs.plugins.shadowjar)

    alias(libs.plugins.runpaper)
}

base {
    archivesName.set("${rootProject.name}-${project.name.uppercaseFirstChar()}")
}

val mcVersion = rootProject.properties["minecraftVersion"] as String

dependencies {
    api(project(":common"))

    implementation(libs.cluster.paper)

    implementation(libs.triumph.cmds)

    implementation(libs.metrics)

    implementation(libs.nbtapi)

    compileOnly(libs.holographicdisplays)

    compileOnly(libs.decentholograms)

    compileOnly(libs.placeholderapi)

    compileOnly(libs.itemsadder)

    compileOnly(libs.oraxen)

    compileOnly(fileTree("libs").include("*.jar"))

    paperweightDevelopmentBundle("io.papermc.paper:dev-bundle:$mcVersion-R0.1-SNAPSHOT")
}

tasks {
    // Runs a test server.
    runServer {
        jvmArgs("-Dnet.kyori.ansi.colorLevel=truecolor")

        minecraftVersion(mcVersion)
    }

    // Assembles the plugin.
    assemble {
        dependsOn(reobfJar)
    }

    shadowJar {
        archiveClassifier.set("")

        exclude("META-INF/**")

        listOf(
                "com.ryderbelserion.cluster.paper",
                "de.tr7zw.changeme.nbtapi",
                "dev.triumphteam.cmd",
                "org.bstats"
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
                "apiVersion" to rootProject.properties["apiVersion"],
                "authors" to rootProject.properties["authors"],
                "website" to rootProject.properties["website"]
        )

        inputs.properties(properties)

        filesMatching("plugin.yml") {
            expand(properties)
        }
    }
}