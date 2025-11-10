plugins {
    id("com.modrinth.minotaur")

    id("shared-plugin")
}

modrinth {
    token = System.getenv("MODRINTH_TOKEN")

    projectId = "${rootProject.property("project_id")}"

    versionName = "${rootProject.name} ${rootProject.version}"
    versionNumber = "${rootProject.version}"
    versionType = rootProject.ext.get("release_type").toString()

    changelog = rootProject.ext.get("mc_changelog").toString()

    gameVersions.addAll(rootProject.property("project_versions").toString().split(",").map { it.trim() })

    uploadFile.set(tasks.named<Jar>("jar"))

    loaders.addAll(rootProject.property("project_platforms").toString().split(",").map { it.trim() })

    syncBodyFrom = rootProject.file("README.md").readText(Charsets.UTF_8)

    autoAddDependsOn = false
    detectLoaders = false

    dependencies {
        required.project("Pl3xMap")

        optional.project("ClaimChunk")
        optional.project("WorldGuard")
        optional.project("EssentialsX")
        optional.project("GriefPrevention")
    }
}