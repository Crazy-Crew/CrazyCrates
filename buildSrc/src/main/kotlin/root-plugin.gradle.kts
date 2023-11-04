plugins {
    id("com.github.johnrengelman.shadow")

    `maven-publish`
    `java-library`
}

repositories {
    maven("https://repo.triumphteam.dev/snapshots/")

    maven("https://repo.crazycrew.us/releases/")

    maven("https://jitpack.io")

    mavenCentral()
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of("17"))
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    shadowJar {
        mergeServiceFiles()

        exclude("META-INF/**")
    }
}

publishing {
    repositories {
        maven {
            credentials {
                this.username = System.getenv("gradle_username")
                this.password = System.getenv("gradle_password")
            }

            url = uri("https://repo.crazycrew.us/releases/")
        }
    }
}