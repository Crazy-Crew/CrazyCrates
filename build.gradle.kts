import task.WebhookExtension
import java.awt.Color

plugins {
    id("crazycrates.root-plugin")
}

val releaseUpdate = Color(27, 217, 106)
val betaUpdate = Color(255, 163, 71)

val beta = settings.versions.beta.get().toBoolean()
val extension = settings.versions.extension.get()

val color = if (beta) betaUpdate else releaseUpdate
val repo = if (beta) "beta" else "releases"

webhook {
    this.avatar("https://en.gravatar.com/avatar/${WebhookExtension.Gravatar().md5Hex("no-reply@ryderbelserion.com")}.jpeg")

    this.username("Ryder Belserion")

    this.content("New version of ${rootProject.name} is ready! <@&929463441159254066>")

    this.embeds {
        this.embed {
            this.color(color)

            this.fields {
                this.field(
                    "Version ${rootProject.version}",
                    "Download Link: https://modrinth.com/$extension/${rootProject.name.toLowerCase()}/version/${rootProject.version}"
                )

                this.field(
                    "API Update",
                    "Version ${rootProject.version} has been pushed to https://repo.crazycrew.us/#/$repo"
                )
            }

            this.author(
                rootProject.name,
                "https://modrinth.com/$extension/${rootProject.name.toLowerCase()}/versions",
                "https://cdn-raw.modrinth.com/data/r3BBZyf3/4522ef0f83143c4803473d356160a3e877c2499c.png"
            )
        }
    }
}