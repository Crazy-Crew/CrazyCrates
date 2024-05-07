package com.badbones69.crazycrates.tasks.prizes;

import com.badbones69.crazycrates.CrazyCrates;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class PrizeBuilder {

    protected @NotNull final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    public abstract ItemStack getItemStack(@NotNull final Player player);

    public abstract void init(@NotNull final ConfigurationSection section);

    public abstract int maxRange();

    public abstract int chance();

}