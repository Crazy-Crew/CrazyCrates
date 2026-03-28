package com.badbones69.crazycrates.paper.api.objects.other;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;

public class CrateSound {

    private final boolean isEnabled;
    private final double volume;
    private final double pitch;
    private final String sound;

    public CrateSound(@NotNull final CommentedConfigurationNode configuration) {
        this.isEnabled = configuration.node("toggle").getBoolean(false);
        this.sound = configuration.node("value").getString("");
        this.volume = configuration.node("volume").getDouble(1.0);
        this.pitch = configuration.node("pitch").getDouble(1.0);
    }

    public void play(@NotNull final Audience audience) {
        if (!this.isEnabled) return;

        final Sound sound = Sound.sound(Key.key(this.sound), Sound.Source.MASTER, (float) this.volume, (float) this.pitch);

        audience.playSound(sound, Sound.Emitter.self());
    }
}