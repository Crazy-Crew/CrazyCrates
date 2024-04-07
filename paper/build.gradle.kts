plugins {
    `paper-plugin`

    id("io.papermc.paperweight.userdev")

    alias(libs.plugins.run.paper)
    alias(libs.plugins.shadow)
}

val mcVersion: String = providers.gradleProperty("mcVersion").get()

dependencies {
    paperweight.paperDevBundle(libs.versions.bundle)

    implementation(projects.api)

    implementation(libs.cluster.paper)

    implementation(libs.triumph.cmds)

    implementation(libs.config.me) {
        exclude(group = "org.yaml", module = "snakeyaml")
    }

    implementation(libs.metrics)

    compileOnly(libs.decent.holograms)

    compileOnly(libs.placeholder.api)

    compileOnly(libs.itemsadder.api)

    compileOnly(libs.oraxen.api)

    compileOnly(fileTree("libs").include("*.jar"))
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

    shadowJar {
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
            "version" to project.version,
            "group" to rootProject.group,
            "description" to rootProject.description,
            "apiVersion" to providers.gradleProperty("apiVersion").get(),
            "authors" to providers.gradleProperty("authors").get(),
            "website" to providers.gradleProperty("website").get()
        )

        inputs.properties(properties)

        filesMatching("plugin.yml") {
            expand(properties)
        }
    }
}