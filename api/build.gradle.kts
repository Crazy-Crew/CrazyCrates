plugins {
    id("crazycrates.root-plugin")
}

dependencies {
    compileOnly(libs.kyori)
    compileOnly(libs.kyori.mm)
}

tasks {
    shadowJar {
        archiveFileName.set("${rootProject.name}+API+${rootProject.version}.jar")
    }
}