package com.badbones69.crazycrates.utilities.logger;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.utilities.AdventureUtils;
import com.google.inject.Inject;
import io.papermc.paper.console.HexFormattingConverter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class CrazyLogger {

    @Inject CrazyCrates plugin;

    @Inject AdventureUtils adventureUtils;

    public void debug(String message) {
        adventureUtils.send(plugin.getServer().getConsoleSender(), parse(" " + message));
    }

    public String parse(String message) {
        Component component = MiniMessage.miniMessage().deserialize(message);

        return HexFormattingConverter.SERIALIZER.serialize(component);
    }
}