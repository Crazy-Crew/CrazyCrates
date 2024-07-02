import com.ryderbelserion.feather.enums.Repository

plugins {
    id("java-plugin")
}

repositories {
    maven("https://repo.triumphteam.dev/snapshots")

    maven(Repository.Paper.url)
}