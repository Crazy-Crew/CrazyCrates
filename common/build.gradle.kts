plugins {
    `java-plugin`
}

dependencies {
    compileOnly(libs.bundles.adventure)
    compileOnly(libs.fusion.kyori)

    implementation(libs.hikari.cp) {
        exclude("org.slf4j")
    }

    api(project(":api"))
}