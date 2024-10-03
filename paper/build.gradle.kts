plugins {
    alias(libs.plugins.runPaper)
    alias(libs.plugins.shadow)
}

repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi")

    maven("https://repo.papermc.io/repository/maven-public")

    maven("https://repo.triumphteam.dev/snapshots")

    maven("https://repo.fancyplugins.de/releases")

    maven("https://repo.oraxen.com/releases")
}

dependencies {
    implementation(project(":common"))

    implementation(libs.triumph.cmds)

    implementation(libs.vital.paper) {
        exclude("org.yaml")
    }

    compileOnly(libs.bundles.dependencies)
    compileOnly(libs.bundles.shared)
    compileOnly(libs.bundles.crates)

    compileOnly(libs.paper)
}

tasks {
    runServer {
        jvmArgs("-Dnet.kyori.ansi.colorLevel=truecolor")

        defaultCharacterEncoding = Charsets.UTF_8.name()

        minecraftVersion(libs.versions.minecraft.get())
    }

    assemble {
        dependsOn(shadowJar)

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
            "com.ryderbelserion.vital",
            "dev.triumphteam.cmd"
        ).forEach {
            relocate(it, "libs.$it")
        }
    }

    processResources {
        inputs.properties("name" to rootProject.name)
        inputs.properties("version" to project.version)
        inputs.properties("group" to project.group)
        inputs.properties("apiVersion" to libs.versions.minecraft.get())
        inputs.properties("description" to project.description)
        inputs.properties("website" to "https://modrinth.com/plugin/crazycrates")

        filesMatching("paper-plugin.yml") {
            expand(inputs.properties)
        }
    }
}