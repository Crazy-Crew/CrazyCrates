package com.badbones69.crazycrates.commands.relations;

import com.badbones69.crazycrates.commands.MessageManager;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.message.MessageKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.utils.MsgUtils;

public class MiscRelations extends MessageManager {

    @Override
    public void build() {
        getBukkitCommandManager().registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) -> {
            if (sender instanceof Player player) {
                send(sender, Messages.correct_usage.getMessage("%usage%", context.getTypedArgument(), player));
            } else {
                send(sender, Messages.correct_usage.getMessage("%usage%", context.getTypedArgument()));
            }
        });

        getBukkitCommandManager().registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) -> {
            if (sender instanceof Player player) {
                send(sender, Messages.no_permission.getMessage(player));
            } else {
                send(sender, Messages.no_permission.getMessage());
            }

            send(sender, Messages.no_permission.getMessage());
        });

        getBukkitCommandManager().registerMessage(BukkitMessageKey.PLAYER_ONLY, (sender, context) -> {
            if (sender instanceof Player player) {
                send(sender, Messages.must_be_a_player.getMessage(player));
            } else {
                send(sender, Messages.must_be_a_player.getMessage());
            }
        });

        getBukkitCommandManager().registerMessage(BukkitMessageKey.CONSOLE_ONLY, (sender, context) -> {
            if (sender instanceof Player player) {
                send(sender, Messages.must_be_console_sender.getMessage(player));
            } else {
                send(sender, Messages.must_be_console_sender.getMessage());
            }
        });
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