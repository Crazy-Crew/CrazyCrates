plugins {
    id("crazycrates.root-plugin")
}

compile {
    val versionString = if (isBeta()) getProjectVersion() else getProjectVersion()
    project.version = versionString

    tasks {
        shadowJar {
            archiveFileName.set("${getProjectName()}+Core+$versionString.jar")
        }
    }

    dependencies {
        compileOnly("org.jetbrains","annotations","23.0.0")

        val adventureVersion = "4.12.0"

        compileOnly("net.kyori", "adventure-api", adventureVersion)
        compileOnly("net.kyori", "adventure-text-minimessage", adventureVersion)
    }
}