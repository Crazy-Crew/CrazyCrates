plugins {
    `root-plugin`
}

rootProject.version = if (System.getenv("NEXT_BUILD_NUMBER") != null) "2.1-${System.getenv("NEXT_BUILD_NUMBER")}" else "2.1"

tasks {
    assemble {
        doFirst {
            delete("$rootDir/jars")
        }
    }
}