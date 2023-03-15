plugins {
    id("crazycrates.root-plugin")
}

dependencies {
    compileOnly(libs.kyori)
    compileOnly(libs.kyori.mm)

    compileOnly(libs.config.me)

    compileOnly(libs.yaml)

    compileOnly(libs.crazycore.api)
}

tasks {
    shadowJar {
        archiveFileName.set("${rootProject.name}+API+${rootProject.version}.jar")
    }
}