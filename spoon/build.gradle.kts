plugins {
    //alias(libs.plugins.runPaper)
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
    compileOnly(libs.paper)

    //implementation(projects.crazycratesCore)

    //implementation(libs.triumph.cmds)

    implementation(libs.fusion.paper) {
        exclude("org.yaml")
    }

    implementation(libs.hikari.cp)

    //implementation(libs.metrics)

    //compileOnly(libs.bundles.dependencies)
    //compileOnly(libs.bundles.shared)
    //compileOnly(libs.bundles.crates)
}

tasks {
    /*shadowJar {
        archiveBaseName.set(rootProject.name)
        archiveClassifier.set("")

        listOf(
            "com.ryderbelserion",
            "com.zaxxer",
        ).forEach {
            relocate(it, "libs.$it")
        }
    }*/

    /*assemble {
        dependsOn(shadowJar)

        doLast {
            copy {
                from(shadowJar.get())
                into(rootProject.projectDir.resolve("jars"))
            }
        }
    }*/

    /*processResources {
        inputs.properties("name" to rootProject.name)
        inputs.properties("version" to project.version)
        inputs.properties("group" to project.group)
        inputs.properties("apiVersion" to libs.versions.minecraft.get())
        inputs.properties("description" to project.description)
        inputs.properties("website" to "https://modrinth.com/plugin/crazycrates")

        filesMatching("paper-plugin.yml") {
            expand(inputs.properties)
        }
    }*/

    /*runServer {
        jvmArgs("-Dnet.kyori.ansi.colorLevel=truecolor")

        defaultCharacterEncoding = Charsets.UTF_8.name()

        minecraftVersion(libs.versions.minecraft.get())
    }*/
}