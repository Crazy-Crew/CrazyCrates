package com.badbones69.crazycrates.commands;

import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.CrazyCrates;

public abstract class MessageManager {

    @NotNull
    private final CrazyCrates plugin = CrazyCrates.get();

    @NotNull
    private final BukkitCommandManager<CommandSender> bukkitCommandManager = this.plugin.getCommandManager();

    @NotNull
    public BukkitCommandManager<CommandSender> getBukkitCommandManager() {
        return this.bukkitCommandManager;
    }

    public abstract void build();

    public abstract void send(@NotNull CommandSender sender, @NotNull String component);

    public abstract String parse(@NotNull String message);

}