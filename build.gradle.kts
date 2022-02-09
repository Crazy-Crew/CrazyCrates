plugins {
    kotlin("jvm") version "1.6.0"
}

rootProject.group = "com.badbones69"
rootProject.version = "1.10.3"

allprojects {

    apply(plugin = "java")
    apply(plugin = "kotlin")

    repositories {
        mavenCentral()

        //maven("https://nexus.badbones69.com/repository/maven-snapshots/")
        //maven("https://nexus.badbones69.com/repository/maven-releases/")
    }

    dependencies {

        compileOnly(kotlin("stdlib", "1.6.0"))
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))

    }
}