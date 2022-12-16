plugins {
    `java-library`
}

repositories {
    maven("https://repo.crazycrew.us/plugins/")

    //maven("https://repo.crazycrew.us/snapshots/")

    maven("https://repo.triumphteam.dev/snapshots/")

    maven("https://libraries.minecraft.net/")

    maven("https://jitpack.io/")

    mavenCentral()
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    compileJava {
        options.release.set(17)
    }
}