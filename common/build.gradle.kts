plugins {
    `java-plugin`
}

repositories {
    //maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    //compileOnly("io.papermc.paper", "paper-api", "1.20.6-R0.1-SNAPSHOT")

    compileOnly("com.ryderbelserion", "vital-common", "1.0")

    compileOnly("org.jetbrains", "annotations", "24.1.0")

    api(project(":crazycrates-api"))
}