plugins {
    id("crazycrates-base")

    id("com.modrinth.minotaur") version "2.+"
    id("co.uzzu.dotenv.gradle") version "2.0.0"
}

tasks {
    modrinth {
        token.set(env.MODRINTH_TOKEN.value)
        projectId.set("crazycrates")
        versionName.set("${rootProject.version}")
        versionNumber.set("${rootProject.version}")

        versionType.set("alpha")

        /**
         * Uncomment which one you are uploading Badbones.
         * Default is PAPER
         */
        uploadFile.set(jar(rootProject.name, "PAPER"))
        //uploadFile.set(jar(rootProject.name, "SPONGE"))
        //uploadFile.set(jar(rootProject.name, "FABRIC"))

        gameVersions.addAll(listOf("1.19.2"))

        loaders.addAll(listOf("paper", "purpur"))

        changelog.set(System.getenv("COMMIT_MESSAGE"))
    }
}

fun jar(name: String, platform: String): RegularFile {
    return rootProject.layout.buildDirectory.file("libs/${name}-${rootProject.version}-${platform}.jar").get()
}