import com.ryderbelserion.feather.enums.Repository
import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
    id("java-plugin")
}

feather {
    repository("https://repo.extendedclip.com/content/repositories/placeholderapi")

    repository("https://repo.triumphteam.dev/snapshots")

    repository(Repository.Paper.url)
}