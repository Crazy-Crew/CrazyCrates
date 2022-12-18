package com.badbones69.crazycrates.util.types;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.badbones69.crazycrates.util.AdventureUtil;
import com.badbones69.crazycrates.util.keys.Key;
import com.badbones69.crazycrates.util.keys.Keyed;

/**
 * Represents a command sender.
 *
 * @author BillyGalbreath
 */
public abstract class Sender extends Keyed implements Audience {

    public Sender(@NotNull Key key) {
        super(key);
    }

    /**
     * Send a message.
     *
     * @param message message to send
     * @param placeholders message placeholders
     */
    public void send(@Nullable String message, @NotNull TagResolver.Single... placeholders) {
        send(true, message, placeholders);
    }

    /**
     * Send a message.
     *
     * @param prefix true to insert command prefix
     * @param message message to send
     * @param placeholders message placeholders
     */
    public void send(boolean prefix, String message, TagResolver.Single... placeholders) {
        if (message == null) return;

        for (String splitMessage : message.split("\n")) {
            send(prefix, AdventureUtil.parse(splitMessage, placeholders));
        }
    }

    /**
     * Send a message.
     *
     * @param message message to send
     */
    public void send(@NotNull ComponentLike message) {
        send(true, message);
    }

    /**
     * Send a message.
     *
     * @param prefix true to insert command prefix
     * @param message message to send
     */
    public abstract void send(boolean prefix, @NotNull ComponentLike message);
}