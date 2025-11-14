plugins {
    `java-plugin`
}

repositories {

}

dependencies {
    api(libs.hikari.cp)
    api(project(":api"))
}