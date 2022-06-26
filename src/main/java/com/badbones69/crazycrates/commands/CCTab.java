package com.badbones69.crazycrates.commands;

import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.enums.CrateType;
import com.badbones69.crazycrates.api.enums.Permissions;
import com.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CCTab implements TabCompleter {
    
    private final CrazyManager cc = CrazyManager.getInstance();
    
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String commandLabel, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) { // /cc
            if (Methods.permCheck(sender, Permissions.CRAZY_CRATES_PLAYER_HELP, true)) completions.add("help");
            if (Methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_ADD_ITEM, true)) completions.add("additem");
            if (Methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_ACCESS, true)) completions.add("admin");
            if (Methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_CONVERT, true)) completions.add("convert");
            if (Methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_LIST, true)) completions.add("list");
            if (Methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_OPEN, true)) completions.add("open");
            if (Methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_FORCE_OPEN, true)) completions.add("forceopen");
            if (Methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_TELEPORT, true)) completions.add("tp");
            if (Methods.permCheck(sender, Permissions.CRAZY_CRATES_PLAYER_TRANSFER_KEYS, true)) completions.add("transfer");
            if (Methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_GIVE_KEY, true)) completions.add("give");
            if (Methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_GIVE_ALL, true)) completions.add("giveall");
            if (Methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_TAKE_KEY, true)) completions.add("take");
            if (Methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_SET_CRATE, true)) completions.add("set");
            if (Methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_RELOAD, true)) completions.add("reload");
            if (Methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_SCHEMATIC_SET, true)) completions.add("set1");
            if (Methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_SCHEMATIC_SET, true)) completions.add("set2");
            if (Methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_SCHEMATIC_SAVE, true)) completions.add("save");
            return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
        } else if (args.length == 2) { // /cc arg0
            switch (args[0].toLowerCase()) {
                case "additem", "open", "forceopen", "transfer" -> {
                    cc.getCrates().forEach(crate -> completions.add(crate.getName()));
                    completions.remove("Menu"); // Takes out a crate that doesn't exist as a file.
                }
                case "set" -> cc.getCrates().forEach(crate -> completions.add(crate.getName()));
                case "tp" -> cc.getCrateLocations().forEach(location -> completions.add(location.getID()));
                case "give", "giveall", "take" -> {
                    completions.add("physical");
                    completions.add("p");
                    completions.add("virtual");
                    completions.add("v");
                }
                case "save" -> completions.add("<Schematic Name>");
            }
            return StringUtil.copyPartialMatches(args[1], completions, new ArrayList<>());
        } else if (args.length == 3) { // /cc arg0 arg1
            switch (args[0].toLowerCase()) {
                case "additem" -> {
                    Crate crateFromName = cc.getCrateFromName(args[1]);
                    if (crateFromName != null && crateFromName.getCrateType() != CrateType.MENU) {
                        cc.getCrateFromName(args[1]).getPrizes().forEach(prize -> completions.add(prize.getName()));
                    }
                }
                case "open", "forceopen", "transfer" -> CrazyManager.getJavaPlugin().getServer().getOnlinePlayers().forEach(player -> completions.add(player.getName()));
                case "give", "giveall", "take" -> {
                    cc.getCrates().forEach(crate -> completions.add(crate.getName()));
                    completions.remove("Menu"); // Takes out a crate that doesn't exist as a file.
                }
            }
            return StringUtil.copyPartialMatches(args[2], completions, new ArrayList<>());
        } else if (args.length == 4) { // /cc arg0 arg1 arg2
            switch (args[0].toLowerCase()) {
                case "give":
                case "giveall":
                case "take":
                case "transfer":
                    for (int i = 1; i <= 100; i++) completions.add(i + "");
                    break;
            }
            return StringUtil.copyPartialMatches(args[3], completions, new ArrayList<>());
        } else if (args.length == 5) { // /cc arg0 arg1 arg2 arg3
            switch (args[0].toLowerCase()) {
                case "give", "giveall", "take" -> CrazyManager.getJavaPlugin().getServer().getOnlinePlayers().forEach(player -> completions.add(player.getName()));
            }
            return StringUtil.copyPartialMatches(args[4], completions, new ArrayList<>());
        }
        return new ArrayList<>();
    }
}