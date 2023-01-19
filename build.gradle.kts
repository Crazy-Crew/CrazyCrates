import java.awt.Color

plugins {
    id("crazycrates.root-plugin")
}

val legacyUpdate = Color(255, 73, 110)
val releaseUpdate = Color(27, 217, 106)
val betaUpdate = Color(255, 163, 71)

compile {
    val pluginVersion = getProjectVersion()
    val pluginName = getProjectName()

    val versionColor = if (isBeta()) betaUpdate else releaseUpdate

    val pageExtension = getExtension()

    webhook {
        this.avatar("https://cdn.discordapp.com/avatars/209853986646261762/eefe3c03882cbb885d98107857d0b022.png")

        this.username("Ryder Belserion")

        this.content("New version of $pluginName is ready! <@&929463441159254066>")

        this.embeds {
            this.embed {
                this.color(versionColor)

                this.fields {
                    this.field(
                        "Version $pluginVersion",
                        "Download Link / Changelog: https://modrinth.com/$pageExtension/${pluginName.toLowerCase()}/version/$pluginVersion"
                    )

                    val urlExt = if (isBeta()) "beta" else "releases"

                    this.field(
                        "API Update",
                        "Version $pluginVersion has been pushed to https://repo.crazycrew.us/#/$urlExt/"
                    )
                }

                this.author(
                    pluginName,
                    "https://modrinth.com/$pageExtension/${pluginName.toLowerCase()}/versions",
                    "https://cdn-raw.modrinth.com/data/r3BBZyf3/4522ef0f83143c4803473d356160a3e877c2499c.png"
                )
            }
        }
    }
}