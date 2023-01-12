import java.awt.Color

plugins {
    id("crazycrates.root-plugin")
}

val legacyUpdate = Color(255, 73, 110)
val releaseUpdate = Color(27, 217, 106)
val snapshotUpdate = Color(255, 163, 71)

val commitMessage: String? = System.getenv("COMMIT_MESSAGE")

releaseBuild {
    val pluginVersion = getProjectVersion()
    val pluginName = getProjectName()

    val versionColor = if (isBeta()) snapshotUpdate else releaseUpdate

    val pageExtension = getExtension()

    webhook {
        this.avatar("https://cdn.discordapp.com/avatars/209853986646261762/eefe3c03882cbb885d98107857d0b022.png")

        this.username("Ryder Belserion")

        //this.content("New version of $pluginName is ready! <@&929463441159254066>")

        this.content("New version of $pluginName is ready!")

        this.embeds {
            this.embed {
                this.color(versionColor)

                this.fields {
                    this.field(
                        "Version $pluginVersion",
                        "Download Link: https://modrinth.com/$pageExtension/${pluginName.toLowerCase()}/version/$pluginVersion"
                    )

                    if (isBeta()) {
                        if (commitMessage != null) this.field("Commit Message", commitMessage)

                        this.field(
                            "API Update",
                            "Version $pluginVersion has been pushed to https://repo.crazycrew.us/#/beta/"
                        )
                    }

                    if (!isBeta()) this.field(
                        "API Update",
                        "Version $pluginVersion has been pushed to https://repo.crazycrew.us/#/releases/"
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