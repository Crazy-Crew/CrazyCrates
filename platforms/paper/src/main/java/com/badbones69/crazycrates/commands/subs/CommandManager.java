package com.badbones69.crazycrates.commands.subs;

import com.badbones69.crazycrates.api.FileManager;
import com.badbones69.crazycrates.api.Universal;
import com.badbones69.crazycrates.api.enums.settings.Messages;
import com.badbones69.crazycrates.api.objects.CrateLocation;
import com.badbones69.crazycrates.commands.subs.admin.*;
import com.badbones69.crazycrates.commands.subs.admin.crates.*;
import com.badbones69.crazycrates.commands.subs.player.CommandKey;
import com.badbones69.crazycrates.commands.subs.player.CommandTransfer;
import com.badbones69.crazycrates.listeners.MenuListener;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import java.util.ArrayList;
import java.util.List;

@Command(value = "crates", alias = {"crazycrates", "cc", "crate", "crazycrate"})
public class CommandManager extends BaseCommand implements Universal {

    @Default
    @Permission(value = "crazycrates.command.player.menu", def = PermissionDefault.TRUE)
    public void onDefaultMenu(Player player) {
        FileConfiguration config = FileManager.Files.CONFIG.getFile();

        boolean openMenu = config.getBoolean("Settings.Enable-Crate-Menu");

        if (openMenu) MenuListener.openGUI(player); else player.sendMessage(Messages.FEATURE_DISABLED.getMessage());
    }

    @SubCommand("help")
    @Permission(value = "crazycrates.command.player.help", def = PermissionDefault.TRUE)
    public void onHelp(CommandSender sender) {
        sender.sendMessage(Messages.HELP.getMessage());
    }

    private static final BukkitCommandManager<CommandSender> commandManager = BukkitCommandManager.create(plugin);

    public static void setup() {
        commandManager.registerMessage(MessageKey.UNKNOWN_COMMAND, (sender, context) -> sender.sendMessage(Messages.UNKNOWN_COMMAND.getMessage()));

        commandManager.registerMessage(MessageKey.TOO_MANY_ARGUMENTS, (sender, context) -> {
            String command = context.getCommand();
            String subCommand = context.getSubCommand();

            String commandOrder = "/" + command + " " + subCommand + " ";

            String correctUsage = null;

            switch (command) {
                case "crates" -> correctUsage = switchCommands(subCommand, commandOrder);
                case "keys" -> {
                    if (subCommand.equals("view")) correctUsage = "/keys " + subCommand;
                }
            }

            if (correctUsage != null) sender.sendMessage(Messages.CORRECT_USAGE.getMessage().replace("%usage%", correctUsage));
        });

        commandManager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> {
            String command = context.getCommand();
            String subCommand = context.getSubCommand();

            String commandOrder = "/" + command + " " + subCommand + " ";

            String correctUsage = null;

            switch (command) {
                case "crates" -> correctUsage = switchCommands(subCommand, commandOrder);
                case "keys" -> {
                    if (subCommand.equals("view")) correctUsage = "/keys " + subCommand + " <player-name>";
                }
            }

            if (correctUsage != null) sender.sendMessage(Messages.CORRECT_USAGE.getMessage().replace("%usage%", correctUsage));
        });

        commandManager.registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) -> sender.sendMessage(Messages.NOT_ONLINE.getMessage().replace("%player%", context.getTypedArgument())));

        commandManager.registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) -> sender.sendMessage(Messages.NO_PERMISSION.getMessage()));

        commandManager.registerMessage(BukkitMessageKey.PLAYER_ONLY, (sender, context) -> sender.sendMessage(Messages.MUST_BE_A_PLAYER.getMessage()));

        commandManager.registerMessage(BukkitMessageKey.CONSOLE_ONLY, (sender, context) -> sender.sendMessage(Messages.MUST_BE_A_CONSOLE_SENDER.getMessage()));

        commandManager.registerSuggestion(SuggestionKey.of("crates"), (sender, context) -> fileManager.getAllCratesNames(plugin).stream().toList());

        commandManager.registerSuggestion(SuggestionKey.of("key-types"), (sender, context) -> List.of("virtual", "v", "physical", "p"));

        commandManager.registerSuggestion(SuggestionKey.of("online-players"), (sender, context) -> plugin.getServer().getOnlinePlayers().stream().map(Player::getName).toList());

        commandManager.registerSuggestion(SuggestionKey.of("locations"), (sender, context) -> crazyManager.getCrateLocations().stream().map(CrateLocation::getID).toList());

        commandManager.registerSuggestion(SuggestionKey.of("prizes"), (sender, context) -> {
            List<String> numbers = new ArrayList<>();

            crazyManager.getCrateFromName(context.getArgs().get(0)).getPrizes().forEach(prize -> numbers.add(prize.getName()));

            return numbers;
        });

        commandManager.registerSuggestion(SuggestionKey.of("numbers"), (sender, context) -> {
            List<String> numbers = new ArrayList<>();

            for (int i = 1; i <= 250; i++) numbers.add(String.valueOf(i));

            return numbers;
        });

        commandManager.registerArgument(CustomPlayer.class, (sender, context) -> new CustomPlayer(context));

        commandManager.registerSuggestion(SuggestionKey.of("booleans"), ((sender, context) -> List.of("true", "false")));

        commandManager.registerCommand(new CommandManager());

        // Admin Commands.
        commandManager.registerCommand(new CommandForceOpen());
        commandManager.registerCommand(new CommandList());
        commandManager.registerCommand(new CommandMassOpen());
        commandManager.registerCommand(new CommandOpen());
        commandManager.registerCommand(new CommandPreview());
        commandManager.registerCommand(new CommandAddItem());
        commandManager.registerCommand(new CommandAdmin());
        commandManager.registerCommand(new CommandAdminHelp());
        commandManager.registerCommand(new CommandDebug());
        commandManager.registerCommand(new CommandGive());
        commandManager.registerCommand(new CommandGiveAll());
        commandManager.registerCommand(new CommandReload());
        commandManager.registerCommand(new CommandSchematics());
        commandManager.registerCommand(new CommandSet());
        commandManager.registerCommand(new CommandTake());
        commandManager.registerCommand(new CommandTeleport());

        // Player Commands.
        commandManager.registerCommand(new CommandKey());
        commandManager.registerCommand(new CommandTransfer());
    }

    private static String switchCommands(String subCommand, String commandOrder) {
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
}

