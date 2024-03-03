plugins {
    id("root-plugin")
}

base {
    archivesName.set(rootProject.name)
}

val mcVersion = providers.gradleProperty("mcVersion").get()
val fabricVersion = providers.gradleProperty("version").get()

project.version = if (System.getenv("BUILD_NUMBER") != null) "$fabricVersion-${System.getenv("BUILD_NUMBER")}" else fabricVersion

tasks {
    modrinth {
        loaders.addAll("fabric")
    }
}