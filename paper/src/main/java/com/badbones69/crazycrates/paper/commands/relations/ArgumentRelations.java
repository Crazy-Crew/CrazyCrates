package com.badbones69.crazycrates.paper.commands.relations;

import com.badbones69.crazycrates.paper.commands.MessageManager;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.extension.meta.MetaKey;
import dev.triumphteam.cmd.core.message.MessageKey;
import com.badbones69.crazycrates.paper.api.enums.Messages;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;

public class ArgumentRelations extends MessageManager {

    private String getContext(@NotNull final String command, @NotNull final String order) {
        if (command.isEmpty() || order.isEmpty()) return "";

        String usage = null;

        switch (command) {
            case "transfer" -> usage = order + " <crate_name> <player_name> <amount>";
            case "claim", "preview", "set" -> usage = order + " <crate_name>";
            case "editor" -> usage = order + "-c/--crate <crate> or -e/--exit";
            case "debug" -> usage = order + " <crate_name> [player_name]";
            case "open" -> usage = order + " <crate_name> <key_type>";
            case "tp" -> usage = order + " <id>";
            case "additem" -> usage = order + " <crate_name> <prize_number> <chance> [tier]";
            case "preview-others", "forceopen" -> usage = order + " <crate_name> <player_name>";
            case "open-others" -> usage = order + " <crate_name> <player_name> [key_type]";
            case "mass-open" -> usage = order + " <crate_name> <key_type> <amount>";
            case "give-random" -> usage = order + " <key_type> <amount> <player_name> [-s/--silent]";
            case "take" -> usage = order + " <key_type> <crate_name> <amount> <player_name>";
            case "give" -> usage = order + " <key_type> <crate_name> <amount> <player_name> [-s/--silent]";
            case "giveall" -> usage = order + " <key_type> <crate_name> <amount> [-s/--silent]";
            case "migrate" -> usage = order + " -mt <migration_type> [-c/--crate] <crate> [-d/--data]";
            case "respin-accept" -> usage = order + " <player> <crate_name> [prize_name]";
            case "respin-deny" -> usage = order + " <player> <crate_name>";
            case "respin-remove", "respin-add" -> usage = order + " <player> <crate_name> <amount>";
            case "admin" -> usage = order;
        }

        return usage;
    }

    @Override
    public void build() {
        this.commandManager.registerMessage(BukkitMessageKey.UNKNOWN_COMMAND, (sender, context) -> Messages.unknown_command.sendMessage(sender, "{command}", context.getInvalidInput()));

        this.commandManager.registerMessage(MessageKey.TOO_MANY_ARGUMENTS, (sender, context) -> {
            Optional<String> meta = context.getMeta().get(MetaKey.NAME);

            meta.ifPresent(key -> {
                if (key.equalsIgnoreCase("view")) {
                    Messages.correct_usage.sendMessage(sender, "{usage}", getContext(key, "/keys " + key));

                    return;
                }

                Messages.correct_usage.sendMessage(sender, "{usage}", getContext(key, "/crazycrates " + key));
            });
        });

        this.commandManager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> {
            Optional<String> meta = context.getMeta().get(MetaKey.NAME);

            meta.ifPresent(key -> {
                if (key.equalsIgnoreCase("view")) {
                    Messages.correct_usage.sendMessage(sender, "{usage}", getContext(key, "/keys " + key));

                    return;
                }

                Messages.correct_usage.sendMessage(sender, "{usage}", getContext(key, "/crazycrates " + key));
            });
        });

        this.commandManager.registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) -> Messages.correct_usage.sendMessage(sender, "{usage}", context.getSyntax()));

        this.commandManager.registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) -> Messages.no_permission.sendMessage(sender, "{permission}", context.getPermission().toString()));

        this.commandManager.registerMessage(BukkitMessageKey.PLAYER_ONLY, (sender, context) -> Messages.must_be_a_player.sendMessage(sender));

        this.commandManager.registerMessage(BukkitMessageKey.CONSOLE_ONLY, (sender, context) -> Messages.must_be_console_sender.sendMessage(sender));
    }
}
