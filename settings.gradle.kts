enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "crazycrates"

listOf(
    "examples/paper" to "example-paper",

    "publish" to "publish",

    "spoon" to "spoon",
    "core" to "core",
    "api" to "api",
).forEach(::includeProject)

fun includeProject(pair: Pair<String, String>): Unit = includeProject(pair.first, pair.second)

fun includeProject(name: String, block: ProjectDescriptor.() -> Unit) {
    include(name)
    project(":$name").apply(block)
}

fun includeProject(path: String, name: String) {
    includeProject(name) {
        this.name = "${rootProject.name}-$name"
        this.projectDir = File(path)
    }
}

fun includeProject(name: String) {
    includeProject(name) {
        this.name = "${rootProject.name}-$name"
    }
}