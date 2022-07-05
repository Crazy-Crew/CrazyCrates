package com.badbones69.crazycrates.commands;

import com.badbones69.crazycrates.api.CrazyManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class KeyTab implements TabCompleter {

    private static final CrazyManager crazyManager = CrazyManager.getInstance();
    
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String commandLabel, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) { // /key
            if (hasPermission(sender)) {
                crazyManager.getPlugin().getServer().getOnlinePlayers().forEach(player -> completions.add(player.getName()));
            }

            return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
        }

        return new ArrayList<>();
    }
    
    private boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("crazycrates." + "key") || sender.hasPermission("crazycrates.admin");
    }
    
}