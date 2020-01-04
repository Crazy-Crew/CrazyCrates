package me.badbones69.crazycrates.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class KeyTab implements TabCompleter {
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String commandLable, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {// /key
            if (hasPermission(sender, "key")) {
                Bukkit.getOnlinePlayers().forEach(player -> completions.add(player.getName()));
            }
            return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
        }
        return new ArrayList<>();
    }
    
    private boolean hasPermission(CommandSender sender, String node) {
        return sender.hasPermission("crazycrates." + node) || sender.hasPermission("crazycrates.admin");
    }
    
}