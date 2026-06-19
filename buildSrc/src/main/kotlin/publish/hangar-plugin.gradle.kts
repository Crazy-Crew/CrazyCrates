import io.papermc.hangarpublishplugin.model.Platforms
import org.gradle.kotlin.dsl.support.uppercaseFirstChar

plugins {
    id("io.papermc.hangar-publish-plugin")

    id("shared-plugin")
}

hangarPublish {
    publications.register("plugin") {
        apiKey.set(System.getenv("HANGAR_KEY"))

        id.set("${rootProject.property("project_id")}")

        version.set("${rootProject.version}")

        changelog.set(rootProject.ext.get("mc_changelog").toString())

        channel.set(rootProject.ext.get("release_type").toString().uppercaseFirstChar())

        pages.resourcePage(rootProject.file("README.md").readText(Charsets.UTF_8))

        platforms {
            register(Platforms.PAPER) {
                jar.set(tasks.named<Jar>("jar").flatMap { it.archiveFile })

                platformVersions.set(rootProject.property("project_versions").toString().split(",").map { it.trim() })

                dependencies {
                    hangar("PlaceholderAPI") {
                        required = false
                    }

                    hangar("FancyHolograms") {
                        required = false
                    }

                    url("CMI", "https://www.spigotmc.org/resources/cmi-300-commands-insane-kits-portals-essentials-economy-mysql-sqlite-much-more.3742/") {
                        required = false
                    }

                    url("DecentHolograms", "https://modrinth.com/plugin/decentholograms") {
                        required = false
                    }

                    url("ItemsAdder", "https://polymart.org/product/1851/itemsadder") {
                        required = false
                    }

                    url("HMCWraps", "https://www.spigotmc.org/resources/%E2%9C%85-1-20-4-1-21-%E2%9C%A8-hmcwraps-%E2%9A%99%EF%B8%8F-custom-skins-colors-for-tools-and-armor-%E2%AD%95-nexo-itemsadder-ready.107099/") {
                        required = false
                    }

                    url("CraftEngine", "https://modrinth.com/plugin/craftengine") {
                        required = false
                    }

                    url("Oraxen", "https://polymart.org/product/629/oraxen") {
                        required = false
                    }

                    url("Nexo", "https://polymart.org/resource/nexo.6901") {
                        required = false
                    }
                }
            }
        }
    }
}