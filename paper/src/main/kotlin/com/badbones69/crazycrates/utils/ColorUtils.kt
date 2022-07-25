package com.badbones69.crazycrates.utils

import net.md_5.bungee.api.ChatColor
import java.util.regex.Matcher
import java.util.regex.Pattern

fun color(message: String): String {
    if (message.startsWith("tellraw")) return org.bukkit.ChatColor.translateAlternateColorCodes('&', message)
    val hexPattern = Pattern.compile("#([A-Fa-f0-9]){6}")
    val matcher: Matcher = hexPattern.matcher(message)
    val buffer = StringBuilder()

    while (matcher.find()) {
        matcher.appendReplacement(buffer, ChatColor.of(matcher.group()).toString())
    }

    return org.bukkit.ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString())
}