plugins {
    `java-plugin`
}

dependencies {
    compileOnly(libs.bundles.adventure)
    compileOnly(libs.fusion.kyori)

    api(project(":api"))
}