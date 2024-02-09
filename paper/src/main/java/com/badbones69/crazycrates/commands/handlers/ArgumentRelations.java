package com.badbones69.crazycrates.commands.handlers;

import com.badbones69.crazycrates.commands.handlers.interfaces.MessageHandler;
import dev.triumphteam.cmd.core.message.MessageKey;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.utils.MsgUtils;

public class ArgumentRelations extends MessageHandler {

    private String getContext(String subCommand, String commandOrder) {
        String correctUsage = null;

        switch (subCommand) {
            case "transfer" -> correctUsage = commandOrder + "<crate-name> <player-name> <amount>";
            case "debug", "open", "set" -> correctUsage = commandOrder + "<crate-name>";
            case "tp" -> correctUsage = commandOrder + "<id>";
            case "additem" -> correctUsage = commandOrder + "<crate-name> <prize-number>";
            case "preview", "open-others", "forceopen" -> correctUsage = commandOrder + "<crate-name> <player-name>";
            case "mass-open" -> correctUsage = commandOrder + "<crate-name> <key-type> <amount>";
            case "give-random" -> correctUsage = commandOrder + "<key-type> <amount> <player-name>";
            case "give", "take" -> correctUsage = commandOrder + "<key-type> <crate-name> <amount> <player-name>";
            case "giveall" -> correctUsage = commandOrder + "<key-type> <crate-name> <amount>";
        }

        return correctUsage;
    }

    @Override
    public void build() {
        getBukkitCommandManager().registerMessage(MessageKey.TOO_MANY_ARGUMENTS, (sender, context) -> {
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

            if (correctUsage != null) send(sender, Messages.correct_usage.getMessage("%usage%", correctUsage).toString());
        });

        getBukkitCommandManager().registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> {
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

            if (correctUsage != null) send(sender, Messages.correct_usage.getMessage("%usage%", correctUsage).toString());
        });

        getBukkitCommandManager().registerMessage(MessageKey.UNKNOWN_COMMAND, (sender, context) -> send(sender, Messages.unknown_command.getString()));
    }

    @Override
    public void send(@NotNull CommandSender sender, @NotNull String component) {
        sender.sendMessage(parse(component));
    }

    @Override
    public String parse(@NotNull String message) {
        return MsgUtils.color(message);
    }
}