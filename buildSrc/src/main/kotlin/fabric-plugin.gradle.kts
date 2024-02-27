import org.gradle.kotlin.dsl.support.uppercaseFirstChar

plugins {
    id("root-plugin")
}

base {
    archivesName.set("${rootProject.name}-${project.name.uppercaseFirstChar()}")
}

val mcVersion = providers.gradleProperty("mcVersion")
val fabricVersion = providers.gradleProperty("version")

project.version = if (System.getenv("BUILD_NUMBER") != null) "$fabricVersion-${System.getenv("BUILD_NUMBER")}" else fabricVersion

tasks {
    val isBeta: Boolean = providers.gradleProperty("isBeta").get().toBoolean()
    val type = if (isBeta) "Beta" else "Release"

    modrinth {
        versionType.set(type.lowercase())

        loaders.addAll("fabric")
    }
}