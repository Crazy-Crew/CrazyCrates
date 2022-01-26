package com.badbones69.crazycrates.func

import com.badbones69.crazycrates.CrazyCrates
import net.md_5.bungee.api.ChatColor
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.regex.Pattern

private val hexPattern = Pattern.compile("<#([A-Fa-f0-9]){6}>")

fun color(message: String): String? {
    var message = message
    var matcher = hexPattern.matcher(message)
    while (matcher.find()) {
        val hexColor = ChatColor.of(matcher.group().substring(1, matcher.group().length - 1))
        val before = message.substring(0, matcher.start())
        val after = message.substring(matcher.end())
        message = before + hexColor + after
        matcher = hexPattern.matcher(message)
    }
    return ChatColor.translateAlternateColorCodes('&', message)
}

fun getFile(plugin: JavaPlugin, name: String): File {
    val dataFolder = File(plugin.dataFolder, "/data")
    return File(dataFolder, name)
}

fun CrazyCrates.registerListener(vararg listeners: Listener) = listeners.toList().forEach { server.pluginManager.registerEvents(it, this) }