package com.badbones69.crazycrates.util.types;

import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.util.AdventureUtil;
import com.badbones69.crazycrates.util.keys.Key;
import java.util.UUID;

/**
 * Represents the console command sender.
 *
 * @author BillyGalbreath
 */
public abstract class Console extends Sender {

    public Console() {
        super(Key.of(new UUID(0, 0)));
    }

    @Override
    public void send(boolean prefix, @NotNull ComponentLike message) {
        sendMessage(AdventureUtil.parse("<white>[<gradient:#FE5F55:#6b55b5>CrazyCrates</gradient>]</white> ").append(message));
    }
}