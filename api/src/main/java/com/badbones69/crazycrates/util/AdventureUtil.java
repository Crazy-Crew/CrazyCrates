package com.badbones69.crazycrates.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import com.badbones69.crazycrates.CrazyCrates;

public class AdventureUtil {

    /**
     * Parse a message.
     *
     * @param message message to parse
     * @param placeholders message placeholders
     */
    public static Component parse(String message, TagResolver.Single... placeholders) {
        return CrazyCrates.api().getMessage().deserialize(message, placeholders);
    }
}