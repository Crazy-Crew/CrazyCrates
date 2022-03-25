plugins {
    kotlin("jvm") version "1.6.10"
}

rootProject.group = "com.badbones69"
rootProject.version = "1.0.0"
rootProject.description = "A plugin to fit all your crate plugin needs."

subprojects {

    project(":plugin") {

        repositories {

            maven("https://papermc.io/repo/repository/maven-public/")
            maven("https://repo.triumphteam.dev/snapshots")

            maven {
                url = uri("https://repo.codemc.org/repository/maven-public/")
                content {
                    includeGroup("de.tr7zw")
                    includeGroup("com.gmail.filoghost.holographicdisplays")
                }
            }

            maven {
                url = uri("https://jitpack.io")
                content {
                    includeGroup("com.github.decentsoftware-eu")
                }
            }

            maven {
                url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
                content {
                    includeGroup("me.clip")
                }
            }

            maven {
                url = uri("https://repo.mvdw-software.com/content/groups/public/")
                content {
                    includeGroup("be.maximvdw")
                }
            }
        }
    }

    apply(plugin = "java")
    apply(plugin = "kotlin")

    repositories {
        mavenCentral()
    }

    dependencies {
        compileOnly(kotlin("stdlib", "1.6.10"))
    }
}