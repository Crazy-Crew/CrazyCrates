import java.awt.Color

plugins {
    id("crazycrates.root-plugin")
}

val legacyUpdate = Color(255, 73, 110)
val releaseUpdate = Color(27, 217, 106)
val snapshotUpdate = Color(255, 163, 71)

val commitMessage: String? = System.getenv("COMMIT_MESSAGE")
val isBeta: Boolean = extra["isBeta"].toString().toBoolean()

webhook {
    this.avatar("https://cdn.discordapp.com/avatars/209853986646261762/eefe3c03882cbb885d98107857d0b022.png?size=4096")

    this.username("Ryder Belserion")

    //this.content("New version of ${project.name} is ready! <@929463441159254066>")

    this.content("New version of ${project.name} is ready!")

    this.embeds {
        this.embed {
            if (isBeta) this.color(snapshotUpdate) else this.color(releaseUpdate)

            this.fields {
                this.field(
                    "Version ${project.version}",
                    "Download Link: https://modrinth.com/plugin/${project.name.toLowerCase()}/version/${project.version}"
                )

                if (isBeta) {
                    if (commitMessage != null) this.field("Commit Message", commitMessage)

                    this.field("Snapshots", "They will be hosted on the same page labeled as `Beta`")

                    this.field(
                        "API Update",
                        "Version ${project.version} has been pushed to https://repo.crazycrew.us/#/snapshots/"
                    )
                }

                if (!isBeta) this.field("API Update","Version ${project.version} has been pushed to https://repo.crazycrew.us/#/releases/")
            }

            this.author(
                project.name,
                "https://modrinth.com/mod/${project.name.toLowerCase()}/versions",
                "https://cdn-raw.modrinth.com/data/r3BBZyf3/4522ef0f83143c4803473d356160a3e877c2499c.png"
            )
        }
    }
}