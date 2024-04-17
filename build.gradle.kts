plugins {
    `root-plugin`
}

rootProject.group = "com.badbones69.crazycrates"
rootProject.description = "Create unlimited crates with multiple crate types to choose from!"

tasks {
    assemble {
        doFirst {
            delete("$rootDir/jars")
        }
    }
}