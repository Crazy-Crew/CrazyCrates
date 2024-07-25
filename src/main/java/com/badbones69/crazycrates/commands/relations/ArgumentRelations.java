package com.badbones69.crazycrates.commands.relations;

import com.badbones69.crazycrates.commands.MessageManager;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.extention.meta.MetaKey;
import dev.triumphteam.cmd.core.message.MessageKey;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.api.enums.Messages;
import java.util.Optional;

public class ArgumentRelations extends MessageManager {

    private String getContext(String command, String order) {
        if (command.isEmpty() || order.isEmpty()) return "";

        String usage = null;

        switch (command) {
            case "transfer" -> usage = order + " <crate_name> <player_name> <amount>";
            case "debug", "set" -> usage = order + " <crate_name>";
            case "open" -> usage = order + " <crate_name> <key_type>";
            case "tp" -> usage = order + "<id>";
            case "additem" -> usage = order + " <crate_name> <prize_number> <weight> [tier]";
            case "preview", "forceopen" -> usage = order + " <crate_name> <player_name>";
            case "open-others" -> usage = order + " <crate_name> <player_name> [key_type]";
            case "mass-open" -> usage = order + " <crate_name> <key_type> <amount>";
            case "give-random" -> usage = order + " <key_type> <amount> <player_name>";
            case "give", "take" -> usage = order + " <key_type> <crate_name> <amount> <player_name>";
            case "giveall" -> usage = order + " <key_type> <crate-name> <amount>";
            case "migrate" -> usage = order + "<migration_type> [crate_name]";
            case "admin" -> usage = order;
        }

        return usage;
    }

    @Override
    public void build() {
        this.commandManager.registerMessage(BukkitMessageKey.UNKNOWN_COMMAND, (sender, context) -> send(sender, Messages.unknown_command.getMessage(sender, "{command}", context.getInvalidInput())));

        this.commandManager.registerMessage(MessageKey.TOO_MANY_ARGUMENTS, (sender, context) -> {
            Optional<String> meta = context.getMeta().get(MetaKey.NAME);

            meta.ifPresent(key -> {
                if (key.equalsIgnoreCase("view")) {
                    send(sender, Messages.correct_usage.getMessage(sender, "{usage}", getContext(key, "/keys " + key)));

                    return;
                }

                send(sender, Messages.correct_usage.getMessage(sender, "{usage}", getContext(key, "/crazycrates " + key)));
            });
        });

        this.commandManager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> {
            Optional<String> meta = context.getMeta().get(MetaKey.NAME);

            meta.ifPresent(key -> {
                if (key.equalsIgnoreCase("view")) {
                    send(sender, Messages.correct_usage.getMessage(sender, "{usage}", getContext(key, "/keys " + key)));

                    return;
                }

                send(sender, Messages.correct_usage.getMessage(sender, "{usage}", getContext(key, "/crazycrates " + key)));
            });
        });

        this.commandManager.registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) -> send(sender, Messages.correct_usage.getMessage(sender, "{usage}", context.getSyntax())));

        this.commandManager.registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) -> send(sender, Messages.no_permission.getMessage(sender, "{permission}", context.getPermission().toString())));

        this.commandManager.registerMessage(BukkitMessageKey.PLAYER_ONLY, (sender, context) -> send(sender, Messages.must_be_a_player.getMessage(sender)));

        this.commandManager.registerMessage(BukkitMessageKey.CONSOLE_ONLY, (sender, context) -> send(sender, Messages.must_be_console_sender.getMessage(sender)));
    }

    @Override
    public void send(@NotNull CommandSender sender, @NotNull String component) {
        sender.sendRichMessage(component);
    }
}
