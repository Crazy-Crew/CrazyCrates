plugins {
    `root-plugin`
}

rootProject.version = if (System.getenv("BUILD_NUMBER") != null) "2.1-${System.getenv("BUILD_NUMBER")}" else "2.1"

tasks {
    assemble {
        doFirst {
            delete("$rootDir/jars")
        }
    }
}