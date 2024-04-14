plugins {
    `root-plugin`
}

rootProject.group = "com.badbones69.crazycrates"
rootProject.description = "Create unlimited crates with multiple crate types to choose from!"

rootProject.version = "2.1-snapshot"

tasks {
    assemble {
        doFirst {
            delete("$rootDir/jars")
        }
    }
}