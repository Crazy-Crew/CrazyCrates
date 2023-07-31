import java.io.File

plugins {
    id("root-plugin")

    id("com.modrinth.minotaur")
}

val isSnapshot = rootProject.version.toString().contains("snapshot")
val type = if (isSnapshot) "beta" else "release"

val desc = """
## Changes:
 * Trim support has been added so you can display trims or give trim prizes via Items:

## API:
 * N/A

## Bugs:
 * Submit any bugs @ https://github.com/Crazy-Crew/${rootProject.name}/issues 

""".trimIndent()

val versions = listOf(
    "1.20.1",
    "1.20"
)

tasks {
    modrinth {
        token.set(System.getenv("MODRINTH_TOKEN"))
        projectId.set(rootProject.name.lowercase())

        versionName.set("${rootProject.name} ${rootProject.version}")
        versionNumber.set(rootProject.version.toString())

        versionType.set(type)

        val file = File("$rootDir/jars")
        if (!file.exists()) file.mkdirs()

        uploadFile.set(layout.buildDirectory.file("$file/${rootProject.name}-${rootProject.version}.jar"))

        autoAddDependsOn.set(true)

        gameVersions.addAll(versions)

        loaders.addAll(listOf("paper", "purpur"))

        changelog.set(desc)
    }
}