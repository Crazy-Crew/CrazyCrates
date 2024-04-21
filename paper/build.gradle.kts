plugins {
    id("com.github.johnrengelman.shadow")

    alias(libs.plugins.run.paper)

    `paper-plugin`
}

dependencies {
    compileOnly(fileTree("$rootDir/libs/compile").include("*.jar"))

    implementation(project(":api"))

    implementation(libs.triumph.cmds)

    implementation(libs.config.me)

    //implementation(libs.metrics)

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
        listOf(
            "dev.triumphteam",
            //"org.bstats",
            "ch.jalu"
        ).forEach { relocate(it, "libs.$it") }
    }

    processResources {
        inputs.properties("name" to rootProject.name)
        inputs.properties("version" to project.version)
        inputs.properties("group" to project.group)
        //inputs.properties("authors" to project.properties["authors"])
        inputs.properties("description" to project.properties["description"])
        inputs.properties("website" to project.properties["website"])

        filesMatching("paper-plugin.yml") {
            expand(inputs.properties)
        }
    }
}