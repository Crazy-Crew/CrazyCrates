plugins {
    id("crazycrates-base")

    id("com.github.johnrengelman.shadow") version "7.1.2"
}

dependencies {
    implementation(libs.google.gson)
}

tasks {
    shadowJar {
        archiveFileName.set("${rootProject.name}-${rootProject.version}-COMMON.jar")

        listOf(
            "com.google.code.gson"
        ).onEach {
            relocate(it, "${rootProject.group}.plugin.common.$it")
        }

        doLast {
            copy {
                from("build/libs/${rootProject.name}-${rootProject.version}-COMMON.jar")
                into(rootProject.layout.buildDirectory.file("libs"))
            }
        }
    }
}