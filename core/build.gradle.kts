plugins {
    `config-java`
}

dependencies {
    api(project(":crazycrates-api", configuration = "shadow"))

    compileOnly(libs.fusion.core)
}

tasks {
    compileJava {
        dependsOn(":crazycrates-api:jar")
    }
}