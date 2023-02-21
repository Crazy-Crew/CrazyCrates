import task.WebhookExtension
import java.awt.Color

plugins {
    id("crazycrates.root-plugin")
}

val legacyUpdate = Color(255, 73, 110)
val releaseUpdate = Color(27, 217, 106)
val betaUpdate = Color(255, 163, 71)

val isBeta = settings.versions.projectBeta.get().toBoolean()
val projectVersion = settings.versions.projectVersion.get()
val projectName = settings.versions.projectName.get()
val projectExt = settings.versions.projectExtension.get()

val finalVersion = if (isBeta) "$projectVersion+Beta" else projectVersion

val color = if (isBeta) betaUpdate else releaseUpdate
val repo = if (isBeta) "beta" else "releases"

webhook {
    this.avatar("https://en.gravatar.com/avatar/${WebhookExtension.Gravatar().md5Hex("no-reply@ryderbelserion.com")}.jpeg")

    this.username("Ryder Belserion")

    this.content("New version of $projectName is ready! <@&929463441159254066>")

    this.embeds {
        this.embed {
            this.color(color)

            this.fields {
                this.field(
                    "Version $finalVersion",
                    "Download Link: https://modrinth.com/$projectExt/${projectName.lowercase()}/version/$finalVersion"
                )

                this.field(
                    "API Update",
                    "Version $finalVersion has been pushed to https://repo.crazycrew.us/#/$repo"
                )
            }

            this.author(
                projectName,
                "https://modrinth.com/$projectExt/${projectName.lowercase()}/versions",
                "https://cdn-raw.modrinth.com/data/r3BBZyf3/4522ef0f83143c4803473d356160a3e877c2499c.png"
            )
        }
    }
}