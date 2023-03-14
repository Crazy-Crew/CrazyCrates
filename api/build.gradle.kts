plugins {
    id("crazycrates.root-plugin")
}

dependencies {
    compileOnly(libs.adventure.api)
    compileOnly(libs.adventure.text)
}

tasks {
    shadowJar {
        archiveFileName.set("${rootProject.name}+API+${rootProject.version}.jar")
    }
}