plugins {
    `config-paper`
}

project.group = "${rootProject.group}.paper"

repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi")

    maven("https://repo.fancyinnovations.com/releases")

    maven("https://repo.nexomc.com/snapshots")

    maven("https://repo.oraxen.com/releases")

    maven("https://maven.devs.beer")
}

dependencies {
    implementation(project(":crazycrates-core"))

    implementation(libs.triumph.cmds)

    implementation(libs.fusion.paper)

    implementation(libs.metrics)

    compileOnly(libs.bundles.holograms)
    compileOnly(libs.bundles.shared)
    compileOnly(libs.bundles.crates)
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        listOf(
            "org.bstats"
        ).forEach {
            relocate(it, "libs.$it")
        }
    }

    processResources {
        inputs.properties(
            "name" to rootProject.name,
            "version" to rootProject.version,
            "description" to rootProject.description,
            "minecraft" to libs.versions.minecraft.get(),
            "group" to project.group
        )

        with(copySpec {
            from("src/main/resources/paper-plugin.yml") {
                expand(inputs.properties)
            }
        })
    }

    runPaper.folia.registerTask()

    runServer {
        jvmArgs("-Dnet.kyori.ansi.colorLevel=truecolor")

        defaultCharacterEncoding = Charsets.UTF_8.name()

        minecraftVersion(libs.versions.minecraft.get())
    }
}