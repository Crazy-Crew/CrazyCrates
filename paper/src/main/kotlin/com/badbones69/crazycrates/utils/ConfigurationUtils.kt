package com.badbones69.crazycrates.utils

import com.badbones69.crazycrates.CrazyCrates
import com.badbones69.crazycrates.utils.keys.Comment
import com.badbones69.crazycrates.utils.keys.Key
import org.apache.commons.lang.StringEscapeUtils
import org.bukkit.configuration.file.YamlConfiguration
import java.nio.file.Path
import java.util.*

abstract class ConfigurationUtils {

    private var config: YamlConfiguration? = null

    private fun getConfig(): YamlConfiguration? = this.config

    fun reload(path: Path, classPath: Class<out ConfigurationUtils>, plugin: CrazyCrates) {
        this.config = YamlConfiguration()

        getConfig()?.options()?.copyDefaults(true)
        getConfig()?.options()?.parseComments(true)
        getConfig()?.options()?.width(9999)

        val file = path.toFile()
        val fileName = path.fileName.toString()

        runCatching {
            println(file.name)
            getConfig()?.load(file)

        }.onFailure {
            plugin.logger.warning("Failed to load $fileName...")

            // it.printStackTrace()
        }

        classPath.declaredFields.forEach {
            it.trySetAccessible()

            val key = it.getDeclaredAnnotation(Key::class.java)
            val comment = it.getDeclaredAnnotation(Comment::class.java)

            if (key == null) return

            runCatching {
                println(key.value)
                println(comment.value)

                val value = getValue(key.value, it.get(classPath))

                it.set(classPath, value)
            }.onFailure {
                plugin.logger.warning("Failed to write to $fileName...")
            }

            println(it)

            if (comment != null) setComments(key.value, comment.value.split("\n").toList())
        }
    }

    protected fun getValue(path: String, def: Any): Any? {
        if (getConfig()?.get(path) == null) getConfig()?.set(path, def)

        return getConfig()?.get(path)
    }

    private fun setComments(path: String, comments: List<String>) = getConfig()?.setComments(path, comments)
}