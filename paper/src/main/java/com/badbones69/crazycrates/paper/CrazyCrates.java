package com.badbones69.crazycrates.paper;

import com.badbones69.crazycrates.paper.api.FileManager.Files;
import com.badbones69.crazycrates.paper.api.enums.settings.Messages;
import com.badbones69.crazycrates.paper.api.managers.quadcrates.SessionManager;
import com.badbones69.crazycrates.paper.api.objects.CrateLocation;
import com.badbones69.crazycrates.paper.commands.subs.CrateBaseCommand;
import com.badbones69.crazycrates.paper.commands.subs.player.BaseKeyCommand;
import com.badbones69.crazycrates.paper.cratetypes.CSGO;
import com.badbones69.crazycrates.paper.cratetypes.Cosmic;
import com.badbones69.crazycrates.paper.cratetypes.CrateOnTheGo;
import com.badbones69.crazycrates.paper.cratetypes.QuadCrate;
import com.badbones69.crazycrates.paper.cratetypes.QuickCrate;
import com.badbones69.crazycrates.paper.cratetypes.Roulette;
import com.badbones69.crazycrates.paper.cratetypes.War;
import com.badbones69.crazycrates.paper.cratetypes.Wheel;
import com.badbones69.crazycrates.paper.cratetypes.Wonder;
import com.badbones69.crazycrates.paper.listeners.*;
import com.badbones69.crazycrates.paper.support.MetricsHandler;
import com.badbones69.crazycrates.paper.support.libraries.PluginSupport;
import com.badbones69.crazycrates.paper.support.placeholders.PlaceholderAPISupport;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;
import java.util.List;

public class CrazyCrates extends JavaPlugin implements Listener {

    private static CrazyCrates plugin;

    private Starter starter;

    BukkitCommandManager<CommandSender> manager = BukkitCommandManager.create(this);

    @Override
    public void onEnable() {
        plugin = this;

        starter = new Starter();

        starter.run();

        starter.getFileManager().setLog(true)
                .registerDefaultGenerateFiles("CrateExample.yml", "/crates", "/crates")
                .registerDefaultGenerateFiles("QuadCrateExample.yml", "/crates", "/crates")
                .registerDefaultGenerateFiles("CosmicCrateExample.yml", "/crates", "/crates")
                .registerDefaultGenerateFiles("QuickCrateExample.yml", "/crates", "/crates")
                .registerDefaultGenerateFiles("classic.nbt", "/schematics", "/schematics")
                .registerDefaultGenerateFiles("nether.nbt", "/schematics", "/schematics")
                .registerDefaultGenerateFiles("outdoors.nbt", "/schematics", "/schematics")
                .registerDefaultGenerateFiles("sea.nbt", "/schematics", "/schematics")
                .registerDefaultGenerateFiles("soul.nbt", "/schematics", "/schematics")
                .registerDefaultGenerateFiles("wooden.nbt", "/schematics", "/schematics")
                .registerCustomFilesFolder("/crates")
                .registerCustomFilesFolder("/schematics")
                .setup();

        // Clean files if we have to.
        cleanFiles();

        // Add extra messages.
        Messages.addMissingMessages();

        FileConfiguration config = Files.CONFIG.getFile();

        boolean metricsEnabled = config.getBoolean("Settings.Toggle-Metrics");

        String menu = config.getString("Settings.Enable-Crate-Menu");

        String full = config.getString("Settings.Give-Virtual-Keys-When-Inventory-Full-Message");

        String phys = config.getString("Settings.Physical-Accepts-Physical-Keys");

        if (phys == null) {
            config.set("Settings.Physical-Accepts-Physical-Keys", true);

            Files.CONFIG.saveFile();
        }

        if (full == null) {
            config.set("Settings.Give-Virtual-Keys-When-Inventory-Full-Message", false);

            Files.CONFIG.saveFile();
        }

        if (menu == null) {
            String oldBoolean = config.getString("Settings.Disable-Crate-Menu");
            boolean switchBoolean = config.getBoolean("Settings.Disable-Crate-Menu");

            if (oldBoolean != null) {
                config.set("Settings.Enable-Crate-Menu", switchBoolean);
                config.set("Settings.Disable-Crate-Menu", null);
            } else {
                config.set("Settings.Enable-Crate-Menu", true);
            }

            Files.CONFIG.saveFile();
        }

        if (metricsEnabled) {
            MetricsHandler metricsHandler = new MetricsHandler();

            metricsHandler.start();
        }

        enable();

        starter.getCrazyManager().loadCrates();
    }

    @Override
    public void onDisable() {
        SessionManager.endCrates();

        QuickCrate.removeAllRewards();

        if (starter.getCrazyManager().getHologramController() != null) starter.getCrazyManager().getHologramController().removeAllHolograms();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent e) {
        starter.getCrazyManager().setNewPlayerKeys(e.getPlayer());
        starter.getCrazyManager().loadOfflinePlayersKeys(e.getPlayer());
    }

    public void cleanFiles() {
        if (!Files.LOCATIONS.getFile().contains("Locations")) {
            Files.LOCATIONS.getFile().set("Locations.Clear", null);
            Files.LOCATIONS.saveFile();
        }

        if (!Files.DATA.getFile().contains("Players")) {
            Files.DATA.getFile().set("Players.Clear", null);
            Files.DATA.saveFile();
        }
    }

    private void enable() {
        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new MenuListener(), this);
        pluginManager.registerEvents(new PreviewListener(), this);
        pluginManager.registerEvents(new FireworkDamageListener(), this);
        pluginManager.registerEvents(new CrateControlListener(), this);
        pluginManager.registerEvents(new MiscListener(), this);

        pluginManager.registerEvents(new War(), this);
        pluginManager.registerEvents(new CSGO(), this);
        pluginManager.registerEvents(new Wheel(), this);
        pluginManager.registerEvents(new Wonder(), this);
        pluginManager.registerEvents(new Cosmic(), this);
        pluginManager.registerEvents(new Roulette(), this);
        pluginManager.registerEvents(new QuickCrate(), this);
        pluginManager.registerEvents(new CrateOnTheGo(), this);
        pluginManager.registerEvents(new QuadCrate(), this);

        pluginManager.registerEvents(this, this);

        starter.getCrazyManager().loadCrates();

        if (!starter.getCrazyManager().getBrokeCrateLocations().isEmpty()) pluginManager.registerEvents(new BrokeLocationsListener(), this);

        if (PluginSupport.PLACEHOLDERAPI.isPluginEnabled()) new PlaceholderAPISupport().register();

        manager.registerMessage(MessageKey.UNKNOWN_COMMAND, (sender, context) -> sender.sendMessage(Messages.UNKNOWN_COMMAND.getMessage()));

        manager.registerMessage(MessageKey.TOO_MANY_ARGUMENTS, (sender, context) -> {
            String command = context.getCommand();
            String subCommand = context.getSubCommand();

            String commandOrder = "/" + command + " " + subCommand + " ";

            String correctUsage = null;

            switch (command) {
                case "crates" -> correctUsage = getString(subCommand, commandOrder);
                case "keys" -> {
                    if (subCommand.equals("view")) correctUsage = "/keys " + subCommand;
                }
            }

            if (correctUsage != null) sender.sendMessage(Messages.CORRECT_USAGE.getMessage().replace("%usage%", correctUsage));
        });

        manager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> {
            String command = context.getCommand();
            String subCommand = context.getSubCommand();

            String commandOrder = "/" + command + " " + subCommand + " ";

            String correctUsage = null;

            switch (command) {
                case "crates" -> correctUsage = getString(subCommand, commandOrder);
                case "keys" -> {
                    if (subCommand.equals("view")) correctUsage = "/keys " + subCommand + " <player-name>";
                }
            }

            if (correctUsage != null) sender.sendMessage(Messages.CORRECT_USAGE.getMessage().replace("%usage%", correctUsage));
        });

        manager.registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) -> sender.sendMessage(Messages.NOT_ONLINE.getMessage().replace("%player%", context.getTypedArgument())));

        manager.registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) -> sender.sendMessage(Messages.NO_PERMISSION.getMessage()));

        manager.registerMessage(BukkitMessageKey.PLAYER_ONLY, (sender, context) -> sender.sendMessage(Messages.MUST_BE_A_PLAYER.getMessage()));

        manager.registerMessage(BukkitMessageKey.CONSOLE_ONLY, (sender, context) -> sender.sendMessage(Messages.MUST_BE_A_CONSOLE_SENDER.getMessage()));

        manager.registerSuggestion(SuggestionKey.of("crates"), (sender, context) -> starter.getFileManager().getAllCratesNames(plugin).stream().toList());

        manager.registerSuggestion(SuggestionKey.of("key-types"), (sender, context) -> KEYS);

        manager.registerSuggestion(SuggestionKey.of("online-players"), (sender, context) -> getServer().getOnlinePlayers().stream().map(Player::getName).toList());

        manager.registerSuggestion(SuggestionKey.of("locations"), (sender, context) -> starter.getCrazyManager().getCrateLocations().stream().map(CrateLocation::getID).toList());

        manager.registerSuggestion(SuggestionKey.of("prizes"), (sender, context) -> {
            List<String> numbers = new ArrayList<>();

            starter.getCrazyManager().getCrateFromName(context.getArgs().get(0)).getPrizes().forEach(prize -> numbers.add(prize.getName()));

            return numbers;
        });

        manager.registerSuggestion(SuggestionKey.of("numbers"), (sender, context) -> {
            List<String> numbers = new ArrayList<>();

            for (int i = 1; i <= 250; i++) numbers.add(i + "");

            return numbers;
        });

        manager.registerArgument(CrateBaseCommand.CustomPlayer.class, (sender, context) -> {
            return new CrateBaseCommand.CustomPlayer(context);
        });

        manager.registerCommand(new BaseKeyCommand());
        manager.registerCommand(new CrateBaseCommand());

        printHooks();
    }

    private String getString(String subCommand, String commandOrder) {
        String correctUsage = null;

        switch (subCommand) {
            case "transfer" -> correctUsage = commandOrder + "<crate-name> " + "<player-name> " + "<amount>";
            case "debug", "open", "set" -> correctUsage = commandOrder + "<crate-name>";
            case "tp" -> correctUsage = commandOrder + "<id>";
            case "additem" -> correctUsage = commandOrder + "<crate-name> " + "<prize-number>";
            case "preview", "open-others", "forceopen" -> correctUsage = commandOrder + "<crate-name> " + "<player-name>";
            case "mass-open" -> correctUsage = commandOrder + "<crate-name> " + "<amount>";
            case "give-random" -> correctUsage = commandOrder + "<key-type> " + "<amount> " + "<player-name>";
            case "give", "take" -> correctUsage = commandOrder + "<key-type> " + "<crate-name> " + "<amount> " + "<player-name>";
            case "giveall" -> correctUsage = commandOrder + "<key-type> " + "<crate-name> " + "<amount>";
        }

        return correctUsage;
    }

    private final List<String> KEYS = List.of("virtual", "v", "physical", "p");

    public static CrazyCrates getPlugin() {
        return plugin;
    }

    public void printHooks() {
        for (PluginSupport value : PluginSupport.values()) {
            if (value.isPluginEnabled()) {
                plugin.getLogger().info(Methods.color("&6&l" + value.name() + " &a&lFOUND"));
            } else {
                plugin.getLogger().info(Methods.color("&6&l" + value.name() + " &c&lNOT FOUND"));
            }
        }
    }

    public Starter getStarter() {
        return starter;
    }
}