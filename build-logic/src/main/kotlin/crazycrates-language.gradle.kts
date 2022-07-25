plugins {
    kotlin("jvm")
    java
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

dependencies {
    compileOnly(kotlin("stdlib", "1.7.10"))
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "17"
        }
    }

    compileJava {
        options.release.set(17)
    }
}