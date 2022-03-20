import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm")
}

repositories {
    mavenCentral()
    mavenLocal()

    maven("https://papermc.io/repo/repository/maven-public/")

    maven("https://repo.triumphteam.dev/releases/")
    maven("https://repo.triumphteam.dev/snapshots/")

    maven {
        url = uri("https://repo.codemc.org/repository/maven-public/")
        content {
            includeGroup("de.tr7zw")
            includeGroup("com.gmail.filoghost.holographicdisplays")
        }
    }

    maven {
        url = uri("https://jitpack.io")
        content {
            includeGroup("com.github.decentsoftware-eu")
        }
    }

    maven {
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        content {
            includeGroup("me.clip")
        }
    }

    maven {
        url = uri("https://repo.mvdw-software.com/content/groups/public/")
        content {
            includeGroup("be.maximvdw")
        }
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).apply { languageVersion.set(JavaLanguageVersion.of(17)) }
    }
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
            javaParameters = true
        }
    }
}