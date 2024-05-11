package com.badbones69.crazycrates.tasks.crates.effects;

import com.ryderbelserion.vital.util.ItemUtil;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.ConfigurationSection;

public class SoundEffect {

    private final SoundCategory category;
    private final boolean isEnabled;
    private final Sound sound;
    private final float volume;
    private final float pitch;

    /**
     * Builds a sound to play.
     *
     * @param section section in the crate config.
     * @param type type of sound i.e. cycle-sound or click-sound.
     * @param fallback the fallback sound if no sound is found.
     * @param category category of sound as to respect the client side sound settings.
     */
    public SoundEffect(@NotNull final ConfigurationSection section, @NotNull final String type, @NotNull final String fallback, @NotNull final SoundCategory category) {
        this.isEnabled = section.getBoolean(type + ".toggle", false);

        this.sound = ItemUtil.getSound(section.getString(type + ".value", fallback));
        this.volume = (float) section.getDouble(type + ".volume", 1.0);
        this.pitch = (float) section.getDouble(type + ".pitch", 1.0);

        this.category = category;
    }

    /**
     * Play a sound for a player at a location.
     *
     * @param player player to play sound to.
     * @param location location for sound to play at.
     */
    public void play(@NotNull final Player player, @NotNull final Location location) {
        if (!this.isEnabled) return;

        player.playSound(location, this.sound, this.category, this.volume, this.pitch);
    }
}