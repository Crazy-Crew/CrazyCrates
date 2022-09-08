package com.badbones69.crazycrates.api.utilities.logger;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.utilities.AdventureUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.papermc.paper.console.HexFormattingConverter;
import net.kyori.adventure.text.Component;

@Singleton
public class CrazyLogger {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    @Inject private AdventureUtils adventureUtils;

    public void debug(String message) {
        adventureUtils.send(plugin.getServer().getConsoleSender(), parse(" " + message));
    }

    public String parse(String message) {
        Component component = adventureUtils.getMiniMessage().deserialize(message);

        return HexFormattingConverter.SERIALIZER.serialize(component);
    }
}