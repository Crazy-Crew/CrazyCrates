plugins {
    id("root-plugin")

    id("com.modrinth.minotaur") version "2.8.2"
}

defaultTasks("build")

rootProject.group = "com.badbones69.crazycrates"
rootProject.description = "Add unlimited crates to your server with 10 different crate types to choose from!"
rootProject.version = "1.13"

val combine by tasks.registering(Jar::class) {
    dependsOn("build")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(files(subprojects.map {
        it.layout.buildDirectory.file("libs/${rootProject.name}-${it.name}-${it.version}.jar").get()
    }).filter { it.name != "MANIFEST.MF" }.map { if (it.isDirectory) it else zipTree(it) })
}

tasks {
    assemble {
        subprojects.forEach {
            dependsOn(":${it.project.name}:build")
        }

        finalizedBy(combine)
    }
}

val description = """
    ## New Features:
    * You can now require a player have a custom amount of keys to open a crate.
     * It will default to 0 if the option isn't added.
     * Add it to your crate config like below.
     ```yml
     Crate:
       # The amount of keys required to use the crate.
       RequiredKeys: 0
     ```
    * You can now define a default prize message in each crate instead of having to manually configure each prize.
     * It will do nothing until you add it.
     * This is an example of how to add it.
     ```yml
     Crate:
      # A default message if the prize doesn't have any Messages
      # i.e Messages: [] or the value isn't there.
      # Warning, this will override all values in Messages: for each prize.
      Prize-Message:
      - '&7You have won &c%reward% &7from &c%crate%.'
    ```
    * Added an extra placeholder called %crate% which simply returns the Preview Name.
     * Any current iterations of %crate% return the Preview Name. I might've missed one though lol..
    
    ## Misc:
    * Removed auto update config version as it's useless, You can remove it from your config.
    
    ## Other:
    * [Feature Requests](https://github.com/Crazy-Crew/${rootProject.name}/discussions/categories/features)
    * [Bug Reports](https://github.com/Crazy-Crew/${rootProject.name}/issues)
""".trimIndent()

val versions = listOf(
    "1.20",
    "1.20.1"
)

val isSnapshot = rootProject.version.toString().contains("snapshot")
val type = if (isSnapshot) "beta" else "release"

modrinth {
    autoAddDependsOn.set(false)

    token.set(System.getenv("MODRINTH_TOKEN"))

    projectId.set(rootProject.name.lowercase())

    versionName.set("${rootProject.name} ${rootProject.version}")
    versionNumber.set("${rootProject.version}")

    uploadFile.set(file("$buildDir/libs/${rootProject.name}-${project.version}.jar"))

    gameVersions.addAll(versions)

    changelog.set(description)

    loaders.addAll("paper", "purpur")
}