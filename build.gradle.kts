plugins {
    `root-plugin`
}


tasks {
    assemble {
        doFirst {
            delete("$rootDir/jars")
        }
    }
}