import org.gradle.api.Project
import java.io.ByteArrayOutputStream

fun Project.latestCommitHistory(start: String, end: String): List<String> {
    return runGitCommand(listOf("log", "$start..$end", "--format=format:%h %s")).split("\n")
}

fun formatLog(commit: String, project: String): String {
    val hash = commit.take(7)
    val message = commit.substring(8) // Get message after commit hash + space between
    return "[$hash](https://github.com/Crazy-Crew/$project/commit/$hash) $message<br>"
}

fun Project.latestCommitsHistory(): List<String> {
    return runGitCommand(listOf("log", "--format=format:%h %s")).split("\n")
}

fun Project.latestCommitHash(): String {
    return runGitCommand(listOf("rev-parse", "--short", "HEAD"))
}

fun Project.latestCommitMessage(): String {
    return runGitCommand(listOf("log", "-1", "--pretty=%B"))
}

fun Project.branchName(): String {
    return runGitCommand(listOf("rev-parse", "--abbrev-ref", "HEAD"))
}

fun Project.runGitCommand(value: List<String>): String {
    val output: String = ByteArrayOutputStream().use {
        exec {
            executable("git")
            args(value)
            standardOutput = it
        }

        it.toString(Charsets.UTF_8).trim()
    }

    return output
}