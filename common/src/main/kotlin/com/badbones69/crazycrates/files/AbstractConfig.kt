package com.badbones69.crazycrates.files

import com.badbones69.crazycrates.files.annotations.Comment
import com.badbones69.crazycrates.files.annotations.Key
import org.simpleyaml.configuration.file.YamlFile
import java.nio.file.Path
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

/**
 * @author BillyGalbreath
 * @maintainer Ryder Belserion
 * @see <a href="https://github.com/BillyGalbreath">...</a>
 */
open class AbstractConfig {

    @OptIn(ExperimentalTime::class)
    fun reload(path: Path, classObject: Any) {
        val time = measureTime {
            val file = YamlFile(path.toFile())

            // Create file.
            if (!file.exists()) file.createNewFile()

            // Set option values.
            file.options().copyDefaults(true)

            // Load the file.
            file.load()

            classObject::class.java.declaredFields.forEach { method ->
                method.isAccessible = true

                val key = method.getAnnotation(Key::class.java)
                val comment = method.getAnnotation(Comment::class.java)

                val value = getValues(file, key.value, method[classObject])

                method.set(classObject, value)

                if (comment != null) file.setComment(key.value, comment.value)
            }

            file.save()
        }

        println("File creation completed in ${time}")
    }

    private fun getValues(file: YamlFile, path: String, default: Any?): Any? {
        if (file.get(path) == null) file.set(path, default)

        return file.get(path)
    }
}