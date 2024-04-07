package com.badbones69.crazycrates.commands.relations;

import com.badbones69.crazycrates.commands.MessageManager;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.message.MessageKey;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.utils.MsgUtils;

public class MiscRelations extends MessageManager {

    @Override
    public void build() {
        commandManager.registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) -> send(sender, Messages.correct_usage.getMessage("{usage}", context.getTypedArgument(), sender)));

        commandManager.registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) -> send(sender, Messages.no_permission.getMessage(sender)));

        commandManager.registerMessage(BukkitMessageKey.PLAYER_ONLY, (sender, context) -> send(sender, Messages.must_be_a_player.getMessage(sender)));

        commandManager.registerMessage(BukkitMessageKey.CONSOLE_ONLY, (sender, context) -> send(sender, Messages.must_be_console_sender.getMessage(sender)));
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