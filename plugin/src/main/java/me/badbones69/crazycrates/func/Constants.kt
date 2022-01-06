package me.badbones69.crazycrates.func

import me.badbones69.crazycrates.CrazyCrates
import net.md_5.bungee.api.ChatColor
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.regex.Matcher
import java.util.regex.Pattern

fun color(message: String): String {
    val pattern = Pattern.compile("#[a-fA-F0-9]{6}")
    val matcher: Matcher = pattern.matcher(message)
    val buffer = StringBuffer()
    while (matcher.find()) {
        matcher.appendReplacement(buffer, ChatColor.of(matcher.group()).toString())
    }
    return org.bukkit.ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString())
}

fun getFile(plugin: JavaPlugin, name: String): File {
    val dataFolder = File(plugin.dataFolder, "/data")
    return File(dataFolder, name)
}

fun CrazyCrates.registerListener(vararg listeners: Listener) = listeners.toList().forEach { server.pluginManager.registerEvents(it, this) }