package com.badbones69.crazycrates.api.objects.users;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import us.crazycrew.crazycrates.api.users.objects.User;
import java.util.Locale;
import java.util.UUID;

public class BukkitUser implements User {

    private final Player player;

    public BukkitUser(final Player player) {
        this.player = player;
    }

    @Override
    public Component getDisplayName() {
        return this.player.displayName();
    }

    @Override
    public Locale getLocale() {
        return this.player.locale();
    }

    @Override
    public String getName() {
        return this.player.getName();
    }

    @Override
    public UUID getUUID() {
        return this.player.getUniqueId();
    }
}