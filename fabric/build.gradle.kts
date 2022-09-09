plugins {
    id("crazycrates-base")

    id("fabric-loom") version "1.0-SNAPSHOT"

    id("com.github.johnrengelman.shadow") version "7.1.2"
}

dependencies {
    minecraft(libs.fabric.minecraft)

    mappings(libs.fabric.mappings)

    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)

    implementation(project(":common"))
}

tasks {
    shadowJar {
        archiveFileName.set("${rootProject.name}-${rootProject.version}-FABRIC.jar")

        listOf(
            "com.badbones69.crazycrates.common"
        ).forEach {
            relocate(it, "${rootProject.group}.plugin.lib.$it")
        }

        doLast {
            copy {
                from("build/libs/${rootProject.name}-${rootProject.version}-FABRIC.jar")
                into(rootProject.layout.buildDirectory.dir("libs"))
            }
        }
    }

    processResources {
        inputs.property("version", rootProject.version)

        filesMatching("fabric.mod.json") {
            expand(mutableMapOf("version" to rootProject.version))
        }
    }
}

java {
    withSourcesJar()
}