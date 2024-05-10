plugins {
    `java-plugin`
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper", "paper-api", "1.20.6-R0.1-SNAPSHOT")

    api("com.ryderbelserion", "vital-paper", "1.0")

    api("ch.jalu", "configme", "1.4.1") {
        exclude("org.yaml")
    }

    api(project(":crazycrates-api"))
}