package com.badbones69.crazycrates.tasks.crates.effects;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.ConfigurationSection;

public class SoundEffect {

    private final boolean isEnabled;

    private Sound sound;

    /**
     * Builds a sound to play.
     *
     * @param section section in the crate config
     * @param type type of sound i.e. cycle-sound or click-sound
     * @param fallback the fallback sound if no sound is found
     * @param source category of sound as to respect the client side sound settings
     */
    public SoundEffect(@NotNull final ConfigurationSection section, @NotNull final String type, @NotNull final String fallback, @NotNull final Sound.Source source) {
        this.isEnabled = section.getBoolean(type + ".toggle", false);

        if (this.isEnabled) {
            this.sound = Sound.sound(Key.key(section.getString(type + ".value", fallback)), source, (float) section.getDouble(type + ".volume", 1.0), (float) section.getDouble(type + ".pitch", 1.0));
        }
    }

    /**
     * Play a sound for a player at a location.
     *
     * @param player player to play sound to.
     * @param location location for sound to play at.
     */
    public void play(@NotNull final Player player, @NotNull final Location location) {
        if (!this.isEnabled) return;
        if (this.sound == null) return;

        player.playSound(this.sound, location.x(), location.y(), location.z());
    }
}