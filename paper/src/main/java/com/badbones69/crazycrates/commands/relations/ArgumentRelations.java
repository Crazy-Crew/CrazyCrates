package com.badbones69.crazycrates.commands.relations;

import com.badbones69.crazycrates.commands.MessageManager;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.message.MessageKey;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.api.enums.Messages;

public class ArgumentRelations extends MessageManager {

    private String getContext(String subCommand, String commandOrder) {
        String correctUsage = null;

        switch (subCommand) {
            case "transfer" -> correctUsage = commandOrder + "<crate-name> <player-name> <amount>";
            case "debug", "open", "set" -> correctUsage = commandOrder + "<crate-name>";
            case "tp" -> correctUsage = commandOrder + "<id>";
            case "additem" -> correctUsage = commandOrder + "<crate-name> <prize-number> <chance> [tier]";
            case "preview", "forceopen" -> correctUsage = commandOrder + "<crate-name> <player-name>";
            case "open-others" -> correctUsage = commandOrder + "<crate-name> <player-name> [key-type]";
            case "mass-open" -> correctUsage = commandOrder + "<crate-name> <key-type> <amount>";
            case "give-random" -> correctUsage = commandOrder + "<key-type> <amount> <player-name>";
            case "give", "take" -> correctUsage = commandOrder + "<key-type> <crate-name> <amount> <player-name>";
            case "giveall" -> correctUsage = commandOrder + "<key-type> <crate-name> <amount>";
        }

        return correctUsage;
    }

    @Override
    public void build() {
        commandManager.registerMessage(MessageKey.TOO_MANY_ARGUMENTS, (sender, context) -> {
            String command = context.getCommand();
            String subCommand = context.getSubCommand();

            String commandOrder = "/" + command + " " + subCommand + " ";

            String correctUsage = null;

            switch (command) {
                case "crates" -> correctUsage = getContext(subCommand, commandOrder);
                case "keys" -> {
                    if (subCommand.equals("view")) correctUsage = "/keys " + subCommand;
                }
            }

            if (correctUsage != null) {
                send(sender, Messages.correct_usage.getMessage(sender, "{usage}", correctUsage));
            }
        });

        commandManager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> {
            String command = context.getCommand();
            String subCommand = context.getSubCommand();

            String commandOrder = "/" + command + " " + subCommand + " ";

            String correctUsage = null;

            switch (command) {
                case "crates" -> correctUsage = getContext(subCommand, commandOrder);
                case "keys" -> {
                    if (subCommand.equals("view")) correctUsage = "/keys " + subCommand + " <player-name>";
                }
            }

            if (correctUsage != null) {
                send(sender, Messages.correct_usage.getMessage(sender, "{usage}", correctUsage));
            }
        });

        commandManager.registerMessage(MessageKey.UNKNOWN_COMMAND, (sender, context) -> send(sender, Messages.unknown_command.getMessage(sender)));

        commandManager.registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) -> send(sender, Messages.correct_usage.getMessage(sender, "{usage}", context.getTypedArgument())));

        commandManager.registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) -> send(sender, Messages.no_permission.getMessage(sender)));

        commandManager.registerMessage(BukkitMessageKey.PLAYER_ONLY, (sender, context) -> send(sender, Messages.must_be_a_player.getMessage(sender)));

        commandManager.registerMessage(BukkitMessageKey.CONSOLE_ONLY, (sender, context) -> send(sender, Messages.must_be_console_sender.getMessage(sender)));
    }

    @Override
    public void send(@NotNull CommandSender sender, @NotNull String component) {
        sender.sendRichMessage(component);
    }
}