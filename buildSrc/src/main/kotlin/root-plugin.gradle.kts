plugins {
    `java-library`
    `maven-publish`

    id("com.github.johnrengelman.shadow")
}

repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    maven("https://repo.codemc.org/repository/maven-public/")

    maven("https://repo.aikar.co/content/groups/aikar/")

    maven("https://repo.triumphteam.dev/snapshots/")

    maven("https://repo.crazycrew.us/first-party/")

    maven("https://repo.crazycrew.us/third-party/")

    maven("https://repo.crazycrew.us/releases/")

    maven("https://jitpack.io/")

    mavenCentral()
}

val isSnapshot = rootProject.version.toString().contains("snapshot")

publishing {
    repositories {
        maven {
            credentials {
                this.username = System.getenv("gradle_username")
                this.password = System.getenv("gradle_password")
            }

            if (isSnapshot) {
                url = uri("https://repo.crazycrew.us/snapshots/")
                return@maven
            }

            url = uri("https://repo.crazycrew.us/releases/")
        }
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of("17"))
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }
}