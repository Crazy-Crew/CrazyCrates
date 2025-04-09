plugins {
    id("root-plugin")
}

rootProject.group = "com.badbones69.crazycrates"

val buildNumber: String? = System.getenv("BUILD_NUMBER")

rootProject.version = if (buildNumber != null) "${libs.versions.minecraft.get()}-$buildNumber" else rootProject.properties["version"].toString()