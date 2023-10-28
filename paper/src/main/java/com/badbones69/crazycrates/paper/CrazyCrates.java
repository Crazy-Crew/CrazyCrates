package com.badbones69.crazycrates.paper;

import com.badbones69.crazycrates.paper.api.FileManager;
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
import us.crazycrew.crazycrates.paper.listeners.crates.CrateOpenListener;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.common.config.ConfigManager;
import us.crazycrew.crazycrates.common.config.types.PluginConfig;
import us.crazycrew.crazycrates.paper.api.crates.CrateManager;
import com.badbones69.crazycrates.paper.support.libraries.PluginSupport;
import us.crazycrew.crazycrates.paper.api.support.placeholders.PlaceholderAPISupport;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import us.crazycrew.crazycrates.paper.CrazyHandler;
import us.crazycrew.crazycrates.paper.listeners.PlayerKeyListener;
import java.util.ArrayList;
import java.util.List;

public class CrazyCrates extends JavaPlugin {

    // Deprecated Start
    private static CrazyCrates plugin;

    private Starter starter;
    // Deprecated End

    private final BukkitCommandManager<CommandSender> manager = BukkitCommandManager.create(this);

    private CrazyHandler crazyHandler;

    @Override
    public void onEnable() {
        plugin = this;

        this.crazyHandler = new CrazyHandler(getDataFolder());
        this.crazyHandler.load();

        // Deprecated Start
        this.starter = new Starter();
        this.starter.run();
        // Deprecated End

        // Clean files if we have to.
        cleanFiles();

        // Add extra messages.
        Messages.addMissingMessages();

        FileConfiguration config = Files.CONFIG.getFile();

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

        enable();
    }

    @Override
    public void onDisable() {
        SessionManager.endCrates();

        QuickCrate.removeAllRewards();

        // Deprecated Start
        if (this.starter.getCrazyManager().getHologramController() != null) this.starter.getCrazyManager().getHologramController().removeAllHolograms();
        // Deprecated End

        this.crazyHandler.unload();
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

        pluginManager.registerEvents(new PlayerKeyListener(), this);

        // Deprecated Start
        this.starter.getCrazyManager().loadCrates();
        // Deprecated End

        pluginManager.registerEvents(new CrateOpenListener(), this);

        // Deprecated Start
        if (!this.starter.getCrazyManager().getBrokeCrateLocations().isEmpty()) pluginManager.registerEvents(new BrokeLocationsListener(), this);
        // Deprecated End

        if (PluginSupport.PLACEHOLDERAPI.isPluginEnabled()) new PlaceholderAPISupport().register();

        this.manager.registerMessage(MessageKey.UNKNOWN_COMMAND, (sender, context) -> sender.sendMessage(Messages.UNKNOWN_COMMAND.getMessage()));

        this.manager.registerMessage(MessageKey.TOO_MANY_ARGUMENTS, (sender, context) -> {
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

        this.manager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> {
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

        this.manager.registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) -> sender.sendMessage(Messages.NOT_ONLINE.getMessage().replace("%player%", context.getTypedArgument())));

        this.manager.registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) -> sender.sendMessage(Messages.NO_PERMISSION.getMessage()));

        this.manager.registerMessage(BukkitMessageKey.PLAYER_ONLY, (sender, context) -> sender.sendMessage(Messages.MUST_BE_A_PLAYER.getMessage()));

        this.manager.registerMessage(BukkitMessageKey.CONSOLE_ONLY, (sender, context) -> sender.sendMessage(Messages.MUST_BE_A_CONSOLE_SENDER.getMessage()));

        this.manager.registerSuggestion(SuggestionKey.of("crates"), (sender, context) -> this.crazyHandler.getFileManager().getAllCratesNames().stream().toList());

        this.manager.registerSuggestion(SuggestionKey.of("key-types"), (sender, context) -> List.of("virtual", "v", "physical", "p"));

        this.manager.registerSuggestion(SuggestionKey.of("online-players"), (sender, context) -> getServer().getOnlinePlayers().stream().map(Player::getName).toList());

        // Deprecated Start
        this.manager.registerSuggestion(SuggestionKey.of("locations"), (sender, context) -> this.starter.getCrazyManager().getCrateLocations().stream().map(CrateLocation::getID).toList());
        // Deprecated End

        this.manager.registerSuggestion(SuggestionKey.of("prizes"), (sender, context) -> {
            List<String> numbers = new ArrayList<>();

            this.crazyHandler.getCrateManager().getCrateFromName(context.getArgs().get(0)).getPrizes().forEach(prize -> numbers.add(prize.getName()));

            return numbers;
        });

        this.manager.registerSuggestion(SuggestionKey.of("numbers"), (sender, context) -> {
            List<String> numbers = new ArrayList<>();

            for (int i = 1; i <= 250; i++) numbers.add(i + "");

            return numbers;
        });

        this.manager.registerArgument(CrateBaseCommand.CustomPlayer.class, (sender, context) -> new CrateBaseCommand.CustomPlayer(context));

        this.manager.registerCommand(new BaseKeyCommand());
        this.manager.registerCommand(new CrateBaseCommand());

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

    // Deprecated Start
    public static CrazyCrates getPlugin() {
        return plugin;
    }

    public Starter getStarter() {
        return this.starter;
    }
    // Deprecated End

    @NotNull
    public CrazyHandler getCrazyHandler() {
        return this.crazyHandler;
    }

    private void printHooks() {
        for (PluginSupport value : PluginSupport.values()) {
            if (value.isPluginEnabled()) {
                getLogger().info(getCrazyHandler().getMethods().color("&6&l" + value.name() + " &a&lFOUND"));
            } else {
                getLogger().info(getCrazyHandler().getMethods().color("&6&l" + value.name() + " &c&lNOT FOUND"));
            }
        }
    }

    @NotNull
    public ConfigManager getConfigManager() {
        return getCrazyHandler().getConfigManager();
    }

    @NotNull
    public FileManager getFileManager() {
        return getCrazyHandler().getFileManager();
    }

    @NotNull
    public CrateManager getCrateManager() {
        return getCrazyHandler().getCrateManager();
    }

    public boolean isLogging() {
        return getConfigManager().getPluginConfig().getProperty(PluginConfig.verbose_logging);
    }
}