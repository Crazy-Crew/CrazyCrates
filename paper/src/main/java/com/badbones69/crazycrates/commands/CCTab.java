package com.badbones69.crazycrates.commands;

/*
public class CCTab implements TabCompleter {

    private final CrazyCrates plugin;
    private final CrazyManager crazyManager;
    private final Methods methods;

    public CCTab(CrazyCrates plugin, CrazyManager crazyManager, Methods methods) {
        this.plugin = plugin;
        this.crazyManager = crazyManager;
        this.methods = methods;
    }
    
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String commandLabel, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) { // /cc
            if (methods.permCheck(sender, Permissions.CRAZY_CRATES_PLAYER_HELP, true)) completions.add("help");
            if (methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_ADD_ITEM, true)) completions.add("additem");
            if (methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_ACCESS, true)) completions.add("admin");
            if (methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_CONVERT, true)) completions.add("convert");
            if (methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_LIST, true)) completions.add("list");
            if (methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_OPEN, true)) completions.add("open");
            if (methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_FORCE_OPEN, true)) completions.add("forceopen");
            if (methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_TELEPORT, true)) completions.add("tp");
            if (methods.permCheck(sender, Permissions.CRAZY_CRATES_PLAYER_TRANSFER_KEYS, true)) completions.add("transfer");
            if (methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_GIVE_KEY, true)) completions.add("give");
            if (methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_GIVE_ALL, true)) completions.add("giveall");
            if (methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_TAKE_KEY, true)) completions.add("take");
            if (methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_SET_CRATE, true)) completions.add("set");
            if (methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_RELOAD, true)) completions.add("reload");
            if (methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_SCHEMATIC_SET, true)) completions.add("set1");
            if (methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_SCHEMATIC_SET, true)) completions.add("set2");
            if (methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_SCHEMATIC_SAVE, true)) completions.add("save");

            return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
        } else if (args.length == 2) { // /cc arg0
            switch (args[0].toLowerCase()) {
                case "additem", "open", "forceopen", "transfer" -> {
                    if (methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_ADD_ITEM, true)
                    || methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_OPEN, true)
                    || methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_FORCE_OPEN, true)
                    || methods.permCheck(sender, Permissions.CRAZY_CRATES_PLAYER_TRANSFER_KEYS, true)) {
                        crazyManager.getCrates().forEach(crate -> completions.add(crate.getName()));
                        completions.remove("Menu"); // Takes out a crate that doesn't exist as a file.
                    }
                }

                case "set" -> {
                    if (methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_SET_CRATE, true)) {
                        crazyManager.getCrates().forEach(crate -> completions.add(crate.getName()));
                    }
                }

                case "tp" -> {
                    if (methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_TELEPORT, true)) {
                        crazyManager.getCrateLocations().forEach(location -> completions.add(location.getID()));
                    }
                }

                case "give", "giveall", "take" -> {
                    if (methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_TAKE_KEY, true)
                            || methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_GIVE_ALL, true)
                            || methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_GIVE_KEY, true)) {
                        completions.add("physical");
                        completions.add("p");
                        completions.add("virtual");
                        completions.add("v");
                    }
                }

                case "save" -> {
                    if (methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_SCHEMATIC, true)) {
                        completions.add("<Schematic Name>");
                    }
                }
            }

            return StringUtil.copyPartialMatches(args[1], completions, new ArrayList<>());
        } else if (args.length == 3) { // /cc arg0 arg1
            switch (args[0].toLowerCase()) {
                case "additem" -> {
                    if (methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_ADD_ITEM, true)) {
                        Crate crateFromName = crazyManager.getCrateFromName(args[1]);
                        if (crateFromName != null && crateFromName.getCrateType() != CrateType.MENU) {
                            crazyManager.getCrateFromName(args[1]).getPrizes().forEach(prize -> completions.add(prize.getName()));
                        }
                    }
                }

                case "open", "forceopen", "transfer" -> {
                    if (methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_OPEN, true)
                            || methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_FORCE_OPEN, true)
                            || methods.permCheck(sender, Permissions.CRAZY_CRATES_PLAYER_TRANSFER_KEYS, true)) {
                        plugin.getServer().getOnlinePlayers().forEach(player -> completions.add(player.getName()));
                    }
                }

                case "give", "giveall", "take" -> {
                    if (methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_TAKE_KEY, true)
                            || methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_GIVE_ALL, true)
                            || methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_GIVE_KEY, true)) {
                        crazyManager.getCrates().forEach(crate -> completions.add(crate.getName()));
                        completions.remove("Menu"); // Takes out a crate that doesn't exist as a file.
                    }
                }
            }

            return StringUtil.copyPartialMatches(args[2], completions, new ArrayList<>());
        } else if (args.length == 4) { // /cc arg0 arg1 arg2
            if (methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_TAKE_KEY, true)
                    || methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_GIVE_ALL, true)
                    || methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_GIVE_KEY, true)) {
                switch (args[0].toLowerCase()) {
                    case "give":
                    case "giveall":
                    case "take":
                    case "transfer":
                        for (int i = 1; i <= 100; i++) completions.add(i + "");
                        break;
                }
            }

            return StringUtil.copyPartialMatches(args[3], completions, new ArrayList<>());
        } else if (args.length == 5) { // /cc arg0 arg1 arg2 arg3
            if (methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_TAKE_KEY, true)
                    || methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_GIVE_ALL, true)
                    || methods.permCheck(sender, Permissions.CRAZY_CRATES_ADMIN_GIVE_KEY, true)) {
                switch (args[0].toLowerCase()) {
                    case "give", "giveall", "take" -> plugin.getServer().getOnlinePlayers().forEach(player -> completions.add(player.getName()));
                }
            }

            return StringUtil.copyPartialMatches(args[4], completions, new ArrayList<>());
        }

        return new ArrayList<>();
    }
}
 */