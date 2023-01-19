plugins {
    id("crazycrates.root-plugin")
}

val projectBeta = settings.versions.projectBeta.get().toBoolean()
val projectVersion = settings.versions.projectVersion.get()
val projectName = settings.versions.projectName.get()

val finalVersion = if (projectBeta) "$projectVersion+beta" else projectVersion

project.version = finalVersion

tasks {
    shadowJar {
        archiveFileName.set("$projectName+core+$finalVersion.jar")
    }
}

dependencies {
    implementation(libs.adventure.api)
    implementation(libs.adventure.text)
}