plugins {
    alias(libs.plugins.loom)
}

base {
    archivesName.set("${rootProject.name}-${project.name}")
}

dependencies {
    // Fabric
    minecraft("com.mojang", "minecraft", "${project.properties["minecraftVersion"]}")

    mappings(loom.officialMojangMappings())

    modImplementation("net.fabricmc", "fabric-loader", "${project.properties["fabricLoaderVersion"]}")
    modImplementation("net.fabricmc.fabric-api", "fabric-api", "${project.properties["fabricApiVersion"]}")
}

val component: SoftwareComponent = components["java"]

tasks {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = rootProject.group.toString()
                artifactId = "${rootProject.name.lowercase()}-${project.name.lowercase()}-api"
                version = rootProject.version.toString()

                from(component)
            }
        }
    }

    processResources {
        val props = mapOf(
            "name" to rootProject.name,
            "group" to project.group,
            "version" to rootProject.version.toString(),
            "description" to project.properties["description"],
            "fabricApiVersion" to project.properties["fabricApiVersion"],
            "fabricLoaderVersion" to project.properties["fabricLoaderVersion"],
            "minecraftVersion" to project.properties["minecraftVersion"],
            "website" to project.properties["website"],
            "sources" to project.properties["sources"],
            "issues" to project.properties["issues"]
        )

        listOf(
            "fabric.mod.json"
        ).forEach {
            filesMatching(it) {
                expand(props)
            }
        }
    }
}