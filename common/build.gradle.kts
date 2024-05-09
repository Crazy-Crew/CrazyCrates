plugins {
    `java-plugin`
}

repositories {
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")

    maven("https://repo.papermc.io/repository/maven-public")
}

dependencies {
    compileOnly("io.papermc.paper", "paper-api", "1.20.6-R0.1-SNAPSHOT")

    compileOnly("com.ryderbelserion", "vital-common", "1.20.6-snapshot")

    api("ch.jalu", "configme", "1.4.1")

    api(project(":crazycrates-api"))
}