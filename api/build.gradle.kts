plugins {
    id("crates.base")
}

project.group = "us.crazycrew.crazycrates"
project.version = "0.8"

dependencies {
    compileOnly(libs.bundles.adventure)
}