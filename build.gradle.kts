plugins {
    `maven-publish`
    `java-library`

    id("com.modrinth.minotaur") version "2.8.2"
}

defaultTasks("build")

rootProject.group = "com.badbones69.crazycrates"
rootProject.description = "Add unlimited crates to your server with 10 different crate types to choose from!"
rootProject.version = "1.14"

val combine = tasks.register<Jar>("combine") {
    mustRunAfter("build")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    val jarFiles = subprojects.flatMap { subproject ->
        files(subproject.layout.buildDirectory.file("libs/${rootProject.name}-${subproject.name}-${subproject.version}.jar").get())
    }.filter { it.name != "MANIFEST.MF" }.map { file ->
        if (file.isDirectory) file else zipTree(file)
    }

    from(jarFiles)
}

subprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "java-library")

    repositories {
        maven("https://repo.codemc.org/repository/maven-public/")

        maven("https://repo.triumphteam.dev/snapshots/")

        maven("https://repo.crazycrew.us/first-party/")

        maven("https://repo.crazycrew.us/third-party/")

        maven("https://repo.crazycrew.us/releases/")

        maven("https://maven.fabricmc.net/")

        maven("https://jitpack.io")

        mavenCentral()
        mavenLocal()
    }

    listOf(
        ":core",
        ":paper"
    ).forEach {
        project(it) {
            group = "${rootProject.group}.${this.name}"
            version = rootProject.version

            if (this.name == "paper") {
                repositories {
                    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
                }
            }
        }

        dependencies {
            implementation("de.tr7zw", "item-nbt-api", "2.11.3")
            implementation("org.bstats", "bstats-bukkit", "3.0.2")

            implementation("dev.triumphteam", "triumph-cmd-bukkit", "2.0.0-SNAPSHOT")
        }
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of("17"))
    }

    tasks {
        compileJava {
            options.encoding = Charsets.UTF_8.name()
            options.release.set(17)
        }
    }
}

tasks {
    assemble {
        subprojects.forEach { project ->
            dependsOn(":${project.name}:build")
        }

        finalizedBy(combine)
    }
}

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
    "1.20.1"
)

val isSnapshot = rootProject.version.toString().contains("snapshot")
val type = if (isSnapshot) "beta" else "release"

publishing {
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

modrinth {
    autoAddDependsOn.set(false)

    token.set(System.getenv("MODRINTH_TOKEN"))

    projectId.set(rootProject.name.lowercase())

    versionName.set("${rootProject.name} ${rootProject.version}")
    versionNumber.set("${rootProject.version}")

    uploadFile.set(combine.get())

    gameVersions.addAll(versions)

    changelog.set(description)

    loaders.addAll("paper", "purpur")
}