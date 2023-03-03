package com.badbones69.crazycrates.utils;

import com.badbones69.crazycrates.CrazyCrates;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtils {

    private static final CrazyCrates plugin = CrazyCrates.getPlugin();

    public static String color(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder builder = new StringBuilder();

        while (matcher.find()) matcher.appendReplacement(builder, String.valueOf(TextColor.fromHexString(matcher.group())));

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(builder).toString());
    }

    private final static Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F\\d]{6}");

    public static String prefix() {
        return color("");
    }

    public static void broadcast(FileConfiguration crateFile, Player player) {
        String broadcast = crateFile.getString("Crate.BroadCast");
        String broadcastBoolean = crateFile.getString("Crate.OpeningBroadCast");
        boolean broadcastToggle = crateFile.getBoolean("Crate.OpeningBroadCast");

        if (broadcastToggle && broadcastBoolean == null || broadcast == null || broadcast.isEmpty()) return;

        //noinspection deprecation
        plugin.getServer().broadcastMessage(color(broadcast.replaceAll("%prefix%", "")).replaceAll("%player%", player.getName()).replaceAll("%Prefix%", "").replaceAll("%Player%", player.getName()));
    }

    public static void sendMessage(CommandSender commandSender, String message, boolean prefixToggle) {
        if (message == null || message.isEmpty()) return;

        String prefix = "";

        if (commandSender instanceof Player player) {
            if (!prefix.isEmpty() && prefixToggle) player.sendMessage(color(message.replaceAll("%prefix%", prefix).replaceAll("%Prefix%", prefix))); else player.sendMessage(color(message));

            return;
        }

        if (!prefix.isEmpty() && prefixToggle) commandSender.sendMessage(color(message.replaceAll("%prefix%", prefix)).replaceAll("%Prefix%", prefix)); else commandSender.sendMessage(color(message));
    }
}