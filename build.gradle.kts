import io.papermc.hangarpublishplugin.model.Platforms

plugins {
    id("root-plugin")

    id("com.modrinth.minotaur") version "2.8.2"
    id("io.papermc.hangar-publish-plugin") version "0.0.5"
}

defaultTasks("build")

rootProject.group = "com.badbones69.crazycrates"
rootProject.description = "Add unlimited crates to your server with 10 different crate types to choose from!"
rootProject.version = "1.11.7"

val combine by tasks.registering(Jar::class) {
    dependsOn("build")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(files(subprojects.map {
        it.layout.buildDirectory.file("libs/${rootProject.name}-${it.name}-${it.version}.jar").get()
    }).filter { it.name != "MANIFEST.MF" }.map { if (it.isDirectory) it else zipTree(it) })
}

tasks {
    assemble {
        subprojects.forEach {
            dependsOn(":${it.project.name}:build")
        }

        finalizedBy(combine)
    }
}

val description = """
    ## New Features:
    * Armor Trims with all pattern/material support has been added. View how to use it below!
      * https://docs.crazycrew.us/crazycrates/prizes/armor-trims
     
    ## Api Changes:
    * `com.badbones69.crazycrates:crazycrates-api:1.11.16` from this point on is outdated.
      * Please update your dependencies to match this version accordingly before updating!
    * `crazycrates-api` has been split into `crazycrates-core-api` and `crazycrates-paper-api` due to future plans for CrazyCrates
    * https://repo.crazycrew.us/#/releases/com/badbones69/crazycrates You can browse the new section here.
    
    ## Other:
    * [Feature Requests](https://github.com/Crazy-Crew/${rootProject.name}/discussions/categories/features)
    * [Bug Reports](https://github.com/Crazy-Crew/${rootProject.name}/issues)
""".trimIndent()

val versions = listOf(
    "1.20",
    "1.20.1"
)

val isSnapshot = rootProject.version.toString().contains("snapshot")
val type = if (isSnapshot) "beta" else "release"
val hangar = if (isSnapshot) "Beta" else "Release"

val builtJar: RegularFile = rootProject.layout.buildDirectory.file("libs/${rootProject.name}-${project.version}.jar").get()

modrinth {
    autoAddDependsOn.set(false)

    token.set(System.getenv("MODRINTH_TOKEN"))

    projectId.set(rootProject.name.lowercase())

    versionName.set("${rootProject.name} ${rootProject.version}")
    versionNumber.set("${rootProject.version}")

    uploadFile = builtJar

    gameVersions.addAll(versions)

    changelog.set(description)

    loaders.addAll("paper", "purpur")
}

hangarPublish {
    publications.register("plugin") {
        version.set("${rootProject.version}")

        namespace("CrazyCrew", rootProject.name)

        channel.set(hangar)

        apiKey.set(System.getenv("hangar_key"))

        changelog.set(description)

        platforms {
            register(Platforms.PAPER) {
                jar.set(builtJar)

                platformVersions.set(versions)
            }
        }
    }
}