plugins {
    id("crazycrates-base")

    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.google.gson)
    implementation(libs.simple.yaml)

    implementation(libs.adventure.api)
    implementation(libs.adventure.text)

    implementation(libs.apache.commons)
}

tasks {
    shadowJar {
        archiveFileName.set("${rootProject.name}-${rootProject.version}-COMMON.jar")

        doLast {
            copy {
                from("build/libs/${rootProject.name}-${rootProject.version}-COMMON.jar")
                into(rootProject.layout.buildDirectory.dir("libs"))
            }
        }
    }
}