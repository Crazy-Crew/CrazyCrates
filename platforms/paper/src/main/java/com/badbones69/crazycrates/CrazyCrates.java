package com.badbones69.crazycrates;

import com.badbones69.crazycrates.api.FileManager;
import com.badbones69.crazycrates.api.managers.quadcrates.SessionManager;
import com.badbones69.crazycrates.api.objects.CrateLocation;
import com.badbones69.crazycrates.commands.subs.CrateBaseCommand;
import com.badbones69.crazycrates.commands.subs.player.BaseKeyCommand;
import com.badbones69.crazycrates.configs.Config;
import com.badbones69.crazycrates.configs.Locale;
import com.badbones69.crazycrates.configs.convert.ConfigConversion;
import com.badbones69.crazycrates.configs.convert.LocaleConversion;
import com.badbones69.crazycrates.cratetypes.*;
import com.badbones69.crazycrates.listeners.*;
import com.badbones69.crazycrates.listeners.tasks.PlayerKeyTask;
import com.badbones69.crazycrates.support.MetricsHandler;
import com.badbones69.crazycrates.support.libraries.PluginSupport;
import com.badbones69.crazycrates.support.libraries.UpdateChecker;
import com.badbones69.crazycrates.support.placeholders.PlaceholderAPISupport;
import com.badbones69.crazycrates.utils.adventure.LogWrapper;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import net.dehya.ruby.PaperManager;
import net.dehya.ruby.RubyCore;
import net.dehya.ruby.utils.LoggerUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CrazyCrates extends JavaPlugin {

    private static CrazyCrates plugin;

    private final Starter starter;

    BukkitCommandManager<CommandSender> manager = BukkitCommandManager.create(this);

    private final PaperManager paperManager = new PaperManager(this, true, true);

    public CrazyCrates() {
        super();

        // Bind plugin variable on constructor build.
        plugin = this;

        starter = new Starter();

        File updateDir = this.paperManager.getDirectory().resolve("updates").toFile();

        if (updateDir.mkdirs()) getLogger().warning("Created the " + updateDir.getName() + " folder.");
    }

    @Override
    public void onLoad() {
        // Create config version instance.
        ConfigConversion configConversion = new ConfigConversion();

        // Convert config if need be.
        configConversion.convertConfig();

        // Create locale version instance.
        LocaleConversion localeConversion = new LocaleConversion();

        // Convert messages if need be.
        localeConversion.convertMessages();

        // Reload/create the config
        Config.reload(this);
        Locale.reload(this);

        getLogger().warning(Config.LOCALE_FILE);

        getLogger().warning(Locale.ADMIN_HELP);
        getLogger().warning(Locale.PLAYER_HELP);
    }

    @Override
    public void onEnable() {
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

        if (Config.TOGGLE_METRICS) {
            MetricsHandler metricsHandler = new MetricsHandler();

            metricsHandler.start();
        }

        checkUpdate();

        // Enables the rest of the plugin after the initial steps.
        enable();
    }

    private void checkUpdate() {
        boolean updaterEnabled = Config.UPDATE_CHECKER;

        if (!updaterEnabled) return;

        getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            UpdateChecker updateChecker = new UpdateChecker(17599);

            try {
                if (updateChecker.hasUpdate() && !getDescription().getVersion().contains("Beta")) {
                    MsgWrapper.send("<#E0115F>CrazyCrates has a new update available! New version: <#11e092>" + updateChecker.getNewVersion());
                    MsgWrapper.send("<#E0115F>Current Version: <#11e092>v" + getDescription().getVersion());
                    MsgWrapper.send("<#E0115F>Download: <#11e092>" + updateChecker.getResourcePage());

                    return;
                }

                MsgWrapper.send("<#E0115F>Plugin is up to date! - <#11e092>" + updateChecker.getNewVersion());
            } catch (Exception exception) {
                MsgWrapper.send("<#E0115F>Could not check for updates! Perhaps the call failed or you are using a snapshot build:");
                MsgWrapper.send("<#E0115F>You can turn off the update checker in config.yml if on a snapshot build.");
            }
        });
    }

    public void cleanFiles() {
        if (!FileManager.Files.LOCATIONS.getFile().contains("Locations")) {
            FileManager.Files.LOCATIONS.getFile().set("Locations.Clear", null);
            FileManager.Files.LOCATIONS.saveFile();
        }

        if (!FileManager.Files.DATA.getFile().contains("Players")) {
            FileManager.Files.DATA.getFile().set("Players.Clear", null);
            FileManager.Files.DATA.saveFile();
        }
    }

    public void enable() {
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

        pluginManager.registerEvents(new PlayerKeyTask(), this);

        if (PluginSupport.ITEMS_ADDER.isPluginEnabled()) {
            getServer().getPluginManager().registerEvents(new ItemsAdderListener(), this);
        } else {
            starter.getCrazyManager().loadCrates();
        }

        if (!starter.getCrazyManager().getBrokeCrateLocations().isEmpty()) pluginManager.registerEvents(new BrokeLocationsListener(), this);

        if (PluginSupport.PLACEHOLDERAPI.isPluginEnabled()) new PlaceholderAPISupport().register();

        //manager.registerMessage(MessageKey.UNKNOWN_COMMAND, (sender, context) -> sender.sendMessage(Messages.UNKNOWN_COMMAND.getMessage()));

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

            //if (correctUsage != null) sender.sendMessage(Messages.CORRECT_USAGE.getMessage().replace("%usage%", correctUsage));
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

            //if (correctUsage != null) sender.sendMessage(Messages.CORRECT_USAGE.getMessage().replace("%usage%", correctUsage));
        });

        //manager.registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) -> sender.sendMessage(Messages.NOT_ONLINE.getMessage().replace("%player%", context.getTypedArgument())));

        //manager.registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) -> sender.sendMessage(Messages.NO_PERMISSION.getMessage()));

        //manager.registerMessage(BukkitMessageKey.PLAYER_ONLY, (sender, context) -> sender.sendMessage(Messages.MUST_BE_A_PLAYER.getMessage()));

        //manager.registerMessage(BukkitMessageKey.CONSOLE_ONLY, (sender, context) -> sender.sendMessage(Messages.MUST_BE_A_CONSOLE_SENDER.getMessage()));

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

        manager.registerCommand(new BaseKeyCommand());
        manager.registerCommand(new CrateBaseCommand());

        printHooks();
    }

    @Override
    public void onDisable() {
        disable();
    }

    public void disable() {
        SessionManager.endCrates();

        QuickCrate.removeAllRewards();

        if (starter.getCrazyManager().getHologramController() != null) starter.getCrazyManager().getHologramController().removeAllHolograms();
    }

    public static CrazyCrates getPlugin() {
        return plugin;
    }

    public PaperManager getPaperManager() {
        return this.paperManager;
    }

    public Starter getStarter() {
        return this.starter;
    }

    private final List<String> KEYS = List.of("virtual", "v", "physical", "p");

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

    public void printHooks() {
        for (PluginSupport value : PluginSupport.values()) {

            if (value.isPluginEnabled()) {
                MsgWrapper.send("<#11e092>" + value.getName() + "<#E0115F> <bold><#7df060>FOUND</bold>");
                return;
            }

            MsgWrapper.send("<#11e092>" + value.getName() + "<#E0115F> <bold><#FE5F55>NOT FOUND</bold>");
        }
    }
}