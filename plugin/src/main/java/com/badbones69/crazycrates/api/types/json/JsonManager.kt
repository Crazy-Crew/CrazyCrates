package com.badbones69.crazycrates.api.types.json

import com.badbones69.crazycrates.api.FileManager
import com.badbones69.crazycrates.getPlugin
import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

object JsonManager {

    private val json = GsonBuilder()
        .disableHtmlEscaping()
        .enableComplexMapKeySerialization()
        .excludeFieldsWithModifiers(128, 64)
        .create()

    private fun loadClass(classObject: Any?, fileName: String): Any? {
        val file = File(fileName)
        if (file.exists()) {
            runCatching {
                val stream = FileInputStream(file)
                val reader = InputStreamReader(stream, StandardCharsets.UTF_8)
                return json.fromJson(reader, classObject?.javaClass)
            }
        }
        return null
    }

    fun load(fileName: String, classObject: Any, default: Any): Any? {
        val currentFile = File(fileName)
        runCatching {
            when {
                !currentFile.exists() -> {
                    save(currentFile.name, default)
                    return default
                }
                else -> {
                    return loadClass(classObject, currentFile.name)
                }
            }
        }.onFailure {
            if (FileManager.getInstance().isLogging) {
                getPlugin().logger.warning("Loading ${currentFile.name} failed.")
                getPlugin().logger.info("Error: ${it.message}")
            }
        }
        return null
    }

    fun save(fileName: String, default: Any) = writeFile(fileName, json.toJson(default))

    private fun writeFile(fileName: String, fileContents: String) {
        val currentFile = File("${getPlugin().dataFolder}/data/$fileName")
        if (!currentFile.exists()) currentFile.createNewFile()
        if (!getPlugin().dataFolder.exists()) getPlugin().dataFolder.mkdir()

        return currentFile.writer(StandardCharsets.UTF_8).use { it.write(fileContents) }
    }
}