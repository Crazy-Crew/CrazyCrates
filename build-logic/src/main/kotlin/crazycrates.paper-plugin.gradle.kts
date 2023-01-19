plugins {
    id("crazycrates.root-plugin")
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper", "paper-api", "${project.properties["minecraft_version"]}-R0.1-SNAPSHOT")
}