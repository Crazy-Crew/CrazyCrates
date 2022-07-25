package com.badbones69.crazycrates.api.files

import com.badbones69.crazycrates.api.files.annotations.Comment
import com.badbones69.crazycrates.api.files.annotations.Key
import java.io.File
import java.nio.file.Path

/**
 * @author BillyGalbreath
 * @maintainer Ryder Belserion
 * @see <a href="https://github.com/BillyGalbreath">...</a>
 */
open class AbstractConfig {

    private val files: HashSet<File> = hashSetOf()

    private val configurations: HashMap<File, FileConfiguration> = hashMapOf()

    fun reload(path: Path, classObject: Any) {
        // Clear just in case.
        files.clear()
        configurations.clear()

        // Get file path
        val file = path.toFile()

        // Create file.
        if (!file.exists()) file.createNewFile()

        // Add file to hashSet.
        addFile(file)

        // Put file in hashmap.
        addConfiguration(file)

        val getConfig = getConfiguration(file)

        // Set option values.
        getConfig?.options()?.copyDefaults(true)
        getConfig?.options()?.parseComments(true)

        // Load the file.
        getConfig?.load(file)

        classObject::class.java.declaredFields.forEach { method ->
            method.isAccessible = true

            val key = method.getAnnotation(Key::class.java)
            val comment = method.getAnnotation(Comment::class.java)

            val value = getValues(file, key.value, method[classObject])

            method.set(classObject, value)

            if (comment != null) getConfiguration(file)?.setComments(key.value, comment.value.split("\n").toList())
        }

        // Save the file.
        getConfig?.save(file)
    }

    private fun addFile(file: File) = files.add(file)

    private fun addConfiguration(file: File) = configurations.put(file, YamlConfiguration.loadConfiguration(file))

    private fun getConfiguration(file: File) = configurations[file]

    private fun getValues(file: File, path: String, default: Any?): Any? {
        if (getConfiguration(file)?.get(path) == null) getConfiguration(file)?.set(path, default)

        return getConfiguration(file)?.get(path)
    }
}