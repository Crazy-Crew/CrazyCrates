package com.badbones69.crazycrates.paper.commands;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.CrazyCratesPaper;
import com.badbones69.crazycrates.paper.api.registry.adapters.PaperSenderAdapter;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import org.bukkit.command.CommandSender;

public abstract class MessageManager {

    protected final CrazyCrates plugin = CrazyCrates.getPlugin();

    protected final CrazyCratesPaper platform = this.plugin.getPlatform();

    protected final PaperSenderAdapter senderAdapter = this.platform.getSenderAdapter();

    protected final BukkitCommandManager<CommandSender> commandManager = CommandManager.getCommandManager();

    public abstract void build();

}