plugins {
    `root-plugin`
}

rootProject.group = "com.badbones69.crazycrates"
rootProject.description = "Create unlimited crates with multiple crate types to choose from!"

val buildNumber = if ((System.getenv("GITHUB_RUN_NUMBER")) != null) "${System.getenv("GITHUB_RUN_NUMBER")}-snapshot" else "-snapshot"
rootProject.version = "2.1$buildNumber"

tasks {
    assemble {
        doFirst {
            delete("$rootDir/jars")
        }
    }
}