package com.badbones69.crazycrates.paper.api.objects.crates;

import com.badbones69.crazycrates.paper.api.objects.Crate;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import us.crazycrew.crazycrates.api.enums.messages.Message;
import us.crazycrew.crazycrates.api.enums.types.KeyType;

import java.util.Map;

public final class CrateRequirement {

    private final int requiredKeys;
    private final KeyType keyType;
    private final Crate crate;
    private final int keys;

    public CrateRequirement(final int requiredKeys, final KeyType keyType, final Crate crate, final int keys) {
        this.requiredKeys = requiredKeys;
        this.keyType = keyType;
        this.crate = crate;
        this.keys = keys;
    }

    public boolean validate(final Player player) {
        if (hasKeys()) {
            return true;
        }

        if (this.crate.isPlaySound()) {
            player.playSound(Sound.sound(Key.key(this.crate.getSound()), Sound.Source.MASTER, 1f, 1f));
        }

        Message.not_enough_keys.sendMessage(player, Map.of(
                "{required_amount}", String.valueOf(this.requiredKeys),
                "{key_amount}", String.valueOf(this.requiredKeys),
                "{amount}", String.valueOf(this.keys),
                "{crate}", this.crate.getCrateName(),
                "{key}", this.crate.getKeyName()
        ));

        return false;
    }

    public int getRequiredKeys() {
        return this.requiredKeys;
    }

    public KeyType getKeyType() {
        return this.keyType;
    }

    public boolean hasKeys() {
        return this.keys >= this.requiredKeys;
    }

    public int getKeys() {
        return this.keys;
    }
}