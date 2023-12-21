package com.badbones69.crazycrates.commands.handlers;

import com.badbones69.crazycrates.commands.handlers.interfaces.MessageHandler;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.message.MessageKey;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.other.MsgUtils;

public class MiscRelations extends MessageHandler {

    @Override
    public void build() {
        getBukkitCommandManager().registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) -> send(sender, Messages.correct_usage.getMessage("%usage%", context.getTypedArgument()).toString()));
        getBukkitCommandManager().registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) -> send(sender, Messages.no_permission.getString()));
        getBukkitCommandManager().registerMessage(BukkitMessageKey.PLAYER_ONLY, (sender, context) -> send(sender, Messages.must_be_a_player.getString()));
        getBukkitCommandManager().registerMessage(BukkitMessageKey.CONSOLE_ONLY, (sender, context) -> send(sender, Messages.must_be_console_sender.getString()));
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