plugins {
    id("paper-plugin")
}

dependencies {
    implementation(project(":api"))

    implementation(libs.cluster.paper)

    implementation(libs.triumphcmds)

    implementation(libs.configme) {
        exclude(group = "org.yaml", module = "snakeyaml")
    }

    implementation(libs.metrics)

    compileOnly(libs.holographicdisplays)

    compileOnly(libs.decentholograms)

    compileOnly(libs.placeholderapi)

    compileOnly(libs.itemsadder)

    compileOnly(libs.oraxen)

    compileOnly(fileTree("libs").include("*.jar"))
}

tasks {
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