plugins {
    `root-plugin`
}

rootProject.group = "com.badbones69.crazycrates"
rootProject.description = "Create unlimited crates with multiple crate types to choose from!"
rootProject.version = if (System.getenv("BUILD_NUMBER") != null) "2.0.1-${System.getenv("BUILD_NUMBER")}" else "2.0.1"

tasks {
    assemble {
        doFirst {
            delete("$rootDir/jars")
        }
    }
}