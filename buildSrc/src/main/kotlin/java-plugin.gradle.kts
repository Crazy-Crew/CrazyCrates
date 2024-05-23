import com.ryderbelserion.feather.enums.Repository
import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
    id("com.ryderbelserion.feather-core")

    `maven-publish`

    `java-library`
}

repositories {
    flatDir { dirs("libs") }

    mavenCentral()
}

dependencies {
    compileOnlyApi(libs.annotations)
}

feather {
    repository("https://repo.codemc.io/repository/maven-public")

    repository(Repository.CrazyCrewReleases.url)

    repository(Repository.Jitpack.url)

    configureJava {
        javaSource(JvmVendorSpec.AMAZON)

        javaVersion(21)
    }
}