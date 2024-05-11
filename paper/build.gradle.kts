plugins {
    id("io.github.goooler.shadow")

    alias(libs.plugins.run.paper)

    `paper-plugin`
}

repositories {
    maven("https://repo.fancyplugins.de/releases/")
}

dependencies {
    compileOnly(fileTree("$rootDir/libs/compile").include("*.jar"))

    compileOnly("com.github.decentsoftware-eu", "decentholograms", "2.8.6")

    compileOnly("de.oliver", "FancyHolograms", "2.0.6")

    compileOnly("me.clip", "placeholderapi", "2.11.5")

    compileOnly("io.th0rgal", "oraxen", "1.171.0")

    implementation("dev.triumphteam", "triumph-cmd-bukkit", "2.0.0-ALPHA-10")

    implementation("com.ryderbelserion", "vital-paper", "1.1")

    implementation(project(":crazycrates-common"))
}

tasks {
    runServer {
        jvmArgs("-Dnet.kyori.ansi.colorLevel=truecolor")

        defaultCharacterEncoding = Charsets.UTF_8.name()

        minecraftVersion("1.20.6")
    }

    assemble {
        doLast {
            copy {
                from(shadowJar.get())
                into(rootProject.projectDir.resolve("jars"))
            }
        }
    }

    shadowJar {
        archiveBaseName.set(rootProject.name)
        archiveClassifier.set("")

        listOf(
            "com.ryderbelserion",
            "dev.triumphteam",
            "ch.jalu"
        ).forEach {
            relocate(it, "libs.$it")
        }
    }

    processResources {
        inputs.properties("name" to rootProject.name)
        inputs.properties("version" to project.version)
        inputs.properties("group" to project.group)
        inputs.properties("description" to project.properties["description"])
        inputs.properties("website" to project.properties["website"])

        filesMatching("paper-plugin.yml") {
            expand(inputs.properties)
        }
    }
}