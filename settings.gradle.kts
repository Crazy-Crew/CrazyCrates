rootProject.name = "CrazyCrates"

listOf("paper", "core").forEach(::includeProject)

fun includeProject(name: String) {
    include(name) {
        this.name = "${rootProject.name.lowercase()}-$name"
    }
}

fun include(name: String, block: ProjectDescriptor.() -> Unit) {
    include(name)
    project(":$name").apply(block)
}