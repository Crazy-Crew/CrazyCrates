plugins {
    `java-library`

    `maven-publish`

    id("com.github.hierynomus.license")

    id("com.github.johnrengelman.shadow")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(project.properties["java_version"].toString()))
}

tasks {
    compileJava {
        options.release.set(project.properties["java_version"].toString().toInt())
    }
}