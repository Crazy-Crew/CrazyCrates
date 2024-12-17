plugins {
    alias(libs.plugins.shadow)

    id("crates.base")
}

dependencies {
    compileOnly(libs.paper)
}