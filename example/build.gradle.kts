plugins {
    alias(libs.plugins.runPaper)
    alias(libs.plugins.shadow)
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")

    maven("https://repo.crazycrew.us/releases/")
}

dependencies {
    // compileOnly(project(":api")) only used for dev testing, official api is below.

    compileOnly("us.crazycrew.crazycrates:api:0.8")

    compileOnly(libs.paper)
}

tasks {
    runServer {
        jvmArgs("-Dnet.kyori.ansi.colorLevel=truecolor")

        defaultCharacterEncoding = Charsets.UTF_8.name()

        minecraftVersion(libs.versions.minecraft.get())
    }

    processResources {
        val properties = hashMapOf(
            "name" to "ExamplePlugin",
            "version" to project.version,
            "group" to project.group,
            "description" to project.description,
            "apiVersion" to libs.versions.minecraft.get(),
            "authors" to listOf("ryderbelserion", "badbones69")
        )

        filesMatching("plugin.yml") {
            expand(properties)
        }
    }
}