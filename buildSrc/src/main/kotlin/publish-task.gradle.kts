import java.io.File

plugins {
    id("root-plugin")

    id("com.modrinth.minotaur")
}

val build: String? = System.getenv("BUILD_NUMBER")
val isSnapshot = rootProject.version.toString().contains("snapshot") || build?.let {
    rootProject.version.toString().contains(
        it
    )
} == true
val type = if (isSnapshot) "beta" else "release"

val desc = """
## Changes:
 * Fixed a bug with giving keys to offline players.
 * Allow logging for giving keys to offline players.
 * Remove the static random variable and just have a new random generate each time.
  * A static random in turn makes it less random because the seed is re-used for everything.

## API:
 * N/A

## Bugs:
 * Submit any bugs @ https://github.com/Crazy-Crew/${rootProject.name}/issues 

""".trimIndent()

val versions = listOf(
    "1.20.1",
    "1.20"
)

val javaComponent: SoftwareComponent = components["java"]

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

    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = rootProject.group.toString()
                artifactId = "${rootProject.name.lowercase()}-api"
                version = rootProject.version.toString()

                from(javaComponent)
            }
        }

        repositories {
            maven {
                credentials {
                    this.username = System.getenv("gradle_username")
                    this.password = System.getenv("gradle_password")
                }

                if (isSnapshot) {
                    url = uri("https://repo.crazycrew.us/snapshots/")
                    return@maven
                }

                url = uri("https://repo.crazycrew.us/releases/")
            }
        }
    }
}