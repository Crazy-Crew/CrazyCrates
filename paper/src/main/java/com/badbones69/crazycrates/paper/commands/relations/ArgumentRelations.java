package com.badbones69.crazycrates.paper.commands.relations;

import us.crazycrew.crazycrates.api.enums.messages.Message;
import com.badbones69.crazycrates.paper.commands.MessageManager;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.message.MessageKey;

public class ArgumentRelations extends MessageManager {

    @Override
    public void build() {
        this.commandManager.registerMessage(BukkitMessageKey.UNKNOWN_COMMAND, (sender, context) -> Message.command_unknown.sendMessage(sender, "{command}", context.getInvalidInput()));

        this.commandManager.registerMessage(MessageKey.TOO_MANY_ARGUMENTS, (sender, context) -> Message.command_usage.sendMessage(sender, "{usage}", context.getSyntax()));

        this.commandManager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> Message.command_usage.sendMessage(sender, "{usage}", context.getSyntax()));

        this.commandManager.registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) -> Message.command_usage.sendMessage(sender, "{usage}", context.getSyntax()));

        this.commandManager.registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) -> Message.lacking_permission.sendMessage(sender, "{permission}", context.getPermission().toString()));

        this.commandManager.registerMessage(BukkitMessageKey.PLAYER_ONLY, (sender, context) -> Message.must_be_player.sendMessage(sender));

        this.commandManager.registerMessage(BukkitMessageKey.CONSOLE_ONLY, (sender, context) -> Message.must_be_console_sender.sendMessage(sender));
    }
}