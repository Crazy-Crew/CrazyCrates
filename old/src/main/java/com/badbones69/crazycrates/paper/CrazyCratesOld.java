package com.badbones69.crazycrates.paper;

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
import com.badbones69.crazycrates.paper.listeners.gui.CrateMenuListener;
import com.badbones69.crazycrates.paper.listeners.gui.CratePreviewListener;
import com.badbones69.crazycrates.paper.support.placeholders.PlaceholderAPISupport;
import com.ryderbelserion.cluster.bukkit.utils.LegacyLogger;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import us.crazycrew.crazycrates.common.config.types.PluginConfig;
import us.crazycrew.crazycrates.paper.api.plugin.CrazyHandler;
import java.util.ArrayList;
import java.util.List;

public class CrazyCratesOld extends JavaPlugin implements Listener {

    private CrazyHandler crazyHandler;

    private final BukkitCommandManager<CommandSender> manager = BukkitCommandManager.create(this);

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    public CrazyHandler getCrazyHandler() {
        return this.crazyHandler;
    }

    public boolean isLogging() {
        return this.crazyHandler.getConfigManager().getPluginConfig().getProperty(PluginConfig.verbose_logging);
    }

    private void enable() {
        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new CrateMenuListener(), this);
        pluginManager.registerEvents(new CratePreviewListener(), this);
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

        this.crazyHandler.getCrazyManager().loadCrates();

        if (!this.crazyHandler.getCrazyManager().getBrokeCrateLocations().isEmpty()) pluginManager.registerEvents(new BrokeLocationsListener(), this);

        //if (PluginSupport.PLACEHOLDERAPI.isPluginEnabled()) new PlaceholderAPISupport().register();

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

        manager.registerSuggestion(SuggestionKey.of("crates"), (sender, context) -> this.crazyHandler.getFileManager().getAllCratesNames().stream().toList());

        manager.registerSuggestion(SuggestionKey.of("key-types"), (sender, context) -> List.of("virtual", "v", "physical", "p"));

        manager.registerSuggestion(SuggestionKey.of("online-players"), (sender, context) -> getServer().getOnlinePlayers().stream().map(Player::getName).toList());

        manager.registerSuggestion(SuggestionKey.of("locations"), (sender, context) -> this.crazyHandler.getCrazyManager().getCrateLocations().stream().map(CrateLocation::getID).toList());

        manager.registerSuggestion(SuggestionKey.of("prizes"), (sender, context) -> {
            List<String> numbers = new ArrayList<>();

            this.crazyHandler.getCrazyManager().getCrateFromName(context.getArgs().get(0)).getPrizes().forEach(prize -> numbers.add(prize.getName()));

            return numbers;
        });

        manager.registerSuggestion(SuggestionKey.of("numbers"), (sender, context) -> {
            List<String> numbers = new ArrayList<>();

            for (int i = 1; i <= 250; i++) numbers.add(i + "");

            return numbers;
        });

        manager.registerArgument(CrateBaseCommand.CustomPlayer.class, (sender, context) -> new CrateBaseCommand.CustomPlayer(context, this));

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

    private void printHooks() {
        /*for (PluginSupport value : PluginSupport.values()) {
            if (value.isPluginEnabled()) {
                LegacyLogger.info("&6&l" + value.name() + " &a&lFOUND");
            } else {
                LegacyLogger.info("&6&l" + value.name() + " &c&lNOT FOUND");
            }
        }*/
    }
}