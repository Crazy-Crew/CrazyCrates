package com.badbones69.crazycrates.utils

import com.badbones69.crazycrates.CrazyCrates
import com.badbones69.crazycrates.utils.keys.Comment
import com.badbones69.crazycrates.utils.keys.Key
import org.bukkit.configuration.file.YamlConfiguration
import java.nio.file.Path

abstract class ConfigurationUtils {

    private var config: YamlConfiguration? = null

    private fun getConfig(): YamlConfiguration? = this.config

    fun reload(path: Path, classPath: Class<out ConfigurationUtils>, plugin: CrazyCrates) {
        this.config = YamlConfiguration()

        getConfig()?.options()?.copyDefaults(true)
        getConfig()?.options()?.parseComments(true)
        getConfig()?.options()?.width(9999)

        val file = path.toFile()
        val fileName = path.fileName.toString();

        runCatching {
            getConfig()?.load(file)
        }.onFailure {
            plugin.logger.warning("Failed to load $fileName...")

            it.printStackTrace()
        }

        classPath.declaredFields.forEach {
            it.trySetAccessible()

            val key = it.getDeclaredAnnotation(Key::class.java)
            val comment = it.getDeclaredAnnotation(Comment::class.java)

            if (key == null) return

            runCatching {
                println(key.value)
                println(comment.value)
            }

            println(it)
        }
    }
}