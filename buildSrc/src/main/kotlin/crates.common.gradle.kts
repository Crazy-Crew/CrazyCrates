import gradle.kotlin.dsl.accessors._e955592cfcca1783c48ac959ec339844.compileJava
import gradle.kotlin.dsl.accessors._e955592cfcca1783c48ac959ec339844.compileKotlin

plugins {
    kotlin("jvm")
    java
}

repositories {

    // PAPI API
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    // MVDW API
    maven("https://repo.mvdw-software.com/content/groups/public/")

    // NBT API
    maven("https://repo.codemc.org/repository/maven-public/")

    // Paper API
    maven("https://repo.papermc.io/repository/maven-public/")

    // Our Repo
    maven("https://repo.badbones69.com/releases/")

    // Vault API
    maven("https://jitpack.io/")

    mavenCentral()
    mavenLocal()
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19-R0.1-SNAPSHOT")

    compileOnly("org.apache.commons:commons-text:1.9")

    compileOnly("com.github.MilkBowl:VaultAPI:1.7")

    compileOnly(kotlin("stdlib", "1.6.20"))
}

tasks.compileKotlin {
    kotlinOptions {
        jvmTarget = "17"
        javaParameters = true
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks.compileJava {
    options.encoding = "UTF-8"
}