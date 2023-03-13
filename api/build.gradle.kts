plugins {
    id("crazycrates.root-plugin")
}

dependencies {
    compileOnly(libs.adventure.api)
    compileOnly(libs.adventure.text)

    compileOnly(libs.config.me)

    compileOnly(libs.yaml)

    compileOnly(libs.crazycore.api)
}

tasks {
    shadowJar {
        archiveFileName.set("${rootProject.name}+${projectDir.name}+${rootProject.version}.jar")
    }
}