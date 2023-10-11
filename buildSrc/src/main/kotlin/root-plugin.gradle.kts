plugins {
    id("com.github.johnrengelman.shadow")

    `maven-publish`
    `java-library`
}

repositories {
    maven("https://repo.codemc.io/repository/maven-public/")

    maven("https://repo.crazycrew.us/first-party/")

    maven("https://repo.crazycrew.us/third-party/")

    maven("https://repo.crazycrew.us/snapshots/")

    maven("https://repo.crazycrew.us/releases/")

    maven("https://jitpack.io")

    mavenCentral()
    //mavenLocal()
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

val isSnapshot = rootProject.version.toString().contains("snapshot")

publishing {
    repositories {
        maven {
            val releases = "https://repo.crazycrew.us/releases/"
            val snapshots = "https://repo.crazycrew.us/snapshots/"

            //url = if (!isSnapshot) uri(releases) else uri(snapshots)

            url = uri(snapshots)

            credentials {
                this.username = System.getenv("GRADLE_USERNAME")
                this.password = System.getenv("GRADLE_PASSWORD")
            }
        }
    }
}