plugins {
    id("io.papermc.paperweight.userdev")

    id("root-plugin")
}

project.version = if (System.getenv("BUILD_NUMBER") != null) "${rootProject.version}-${System.getenv("BUILD_NUMBER")}" else rootProject.version

dependencies {
    paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT")
}