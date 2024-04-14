plugins {
    `root-plugin`
}

rootProject.group = "com.badbones69.crazycrates"
rootProject.description = "Create unlimited crates with multiple crate types to choose from!"

rootProject.version = if (System.getenv("GITHUB_RUN_NUMBER") != null) "2.1-${System.getenv("GITHUB_RUN_NUMBER")}" else "2.1"

tasks {
    assemble {
        doFirst {
            delete("$rootDir/jars")
        }
    }
}