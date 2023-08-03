plugins {
    id("root-plugin")

    id("com.modrinth.minotaur") version "2.8.2"
}

defaultTasks("build")

rootProject.group = "com.badbones69.crazycrates"
rootProject.description = "Add unlimited crates to your server with 10 different crate types to choose from!"
rootProject.version = "1.14"

val combine = tasks.register<Jar>("combine") {
    mustRunAfter("build")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    val jarFiles = subprojects.flatMap { subproject ->
        files(subproject.layout.buildDirectory.file("libs/${rootProject.name}-${subproject.name}-${subproject.version}.jar").get())
    }.filter { it.name != "MANIFEST.MF" }.map { file ->
        if (file.isDirectory) file else zipTree(file)
    }

    from(jarFiles)
}

tasks {
    assemble {
        subprojects.forEach { project ->
            dependsOn(":${project.name}:build")
        }

        finalizedBy(combine)
    }
}

val description = """
## New Features:
 * Added the ability for an item to have damage applied to it.
 ```yml
    2:
      DisplayName: '&b&lCheap Helmet'
      DisplayItem: 'GOLDEN_HELMET'
      # Only works on items with durability. This will make the item appear more damaged. Durability - Damage
      # Durability-Damage
      # It does not set the durability but subtracts this number from the durability
      # Durability is 100, It subtracts 50.
      DisplayDamage: 50
      DisplayAmount: 1
      Lore:
        - '&7Win a cheap helmet.'
        - '&6&lChance: &c&l60%'
      MaxRange: 100
      Chance: 60
      Items:
        - 'Item:GOLDEN_HELMET, Amount:1, Damage:50, Trim-Pattern:SENTRY, Trim-Material:QUARTZ, Name:&bCheap Helmet, PROTECTION_ENVIRONMENTAL:1, OXYGEN:1'
 ```
## Fix:
 * I was using the preview name instead of the crate name. It now uses the Crate Name.
    
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

    uploadFile.set(combine.get())

    gameVersions.addAll(versions)

    changelog.set(description)

    loaders.addAll("paper", "purpur")
}