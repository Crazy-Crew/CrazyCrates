dependencyResolutionManagement {
    includeBuild("build-logic")
}

// Project Name!
rootProject.name = "Crazy-Crates"

include("paper", "api", "common")

enableFeaturePreview("VERSION_CATALOGS")