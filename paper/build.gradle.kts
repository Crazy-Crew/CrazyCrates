plugins {
    alias(libs.plugins.shadow)
    alias(libs.plugins.userdev)
    alias(libs.plugins.modrinth)
}

val projectName = "${rootProject.name}-${project.name.substring(0, 1).uppercase() + project.name.substring(1)}"

base {
    archivesName.set(projectName)
}

repositories {
    flatDir { dirs("libs") }
}

dependencies {
    api(project(":core"))

    implementation("de.tr7zw", "item-nbt-api", "2.11.3")
    implementation("org.bstats", "bstats-bukkit", "3.0.2")

    implementation("dev.triumphteam", "triumph-cmd-bukkit", "2.0.0-SNAPSHOT")

    compileOnly(fileTree("libs").include("*.jar"))

    compileOnly("me.clip", "placeholderapi", "2.11.3")

    compileOnly("com.github.decentsoftware-eu", "decentholograms","2.8.3")

    paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")
}

val component: SoftwareComponent = components["java"]

tasks {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = project.group.toString()
                artifactId = "${rootProject.name.lowercase()}-${project.name.lowercase()}-api"
                version = project.version.toString()

                from(component)
            }
        }
    }

    shadowJar {
        archiveBaseName.set(projectName)
        archiveClassifier.set("")

        listOf(
            "dev.triumphteam",
            "org.bstats",
            "de.tr7zw"
        ).forEach {
            relocate(it, "libs.$it")
        }
    }

    reobfJar {
        outputJar.set(file("$buildDir/libs/$projectName-${project.version}.jar"))
    }

    assemble {
        dependsOn(reobfJar)
    }

    processResources {
        val props = mapOf(
            "name" to rootProject.name,
            "group" to project.group,
            "version" to project.version,
            "description" to rootProject.description,
            "authors" to rootProject.properties["authors"],
            "apiVersion" to "1.20",
            "website" to "https://modrinth.com/plugin/${rootProject.name.lowercase()}"
        )

        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}

val file = file("${rootProject.rootDir}/jars/$projectName-${project.version}.jar")

val description = """
## New Features:
* N/A

## Fix:
* N/A
    
## Other:
* [Feature Requests](https://github.com/Crazy-Crew/${rootProject.name}/discussions/categories/features)
* [Bug Reports](https://github.com/Crazy-Crew/${rootProject.name}/issues)
""".trimIndent()

val versions = listOf(
    "1.20",
    "1.20.1",
    "1.20.2"
)

modrinth {
    autoAddDependsOn.set(false)

    token.set(System.getenv("MODRINTH_TOKEN"))

    projectId.set(rootProject.name.lowercase())

    versionName.set("${rootProject.name} ${project.version}")
    versionNumber.set("${project.version}")

    uploadFile.set(file)

    gameVersions.addAll(versions)

    changelog.set(description)

    loaders.addAll("paper", "purpur")
}