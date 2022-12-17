package com.badbones69.crazycrates;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.util.types.Console;
import com.badbones69.crazycrates.registry.player.PlayerRegistry;
import java.nio.file.Path;

public interface CrazyCrates {

    final class Provider {
        static CrazyCrates api;

        @NotNull
        public static CrazyCrates api() {
            return Provider.api;
        }
    }

    @NotNull
    static CrazyCrates api() {
        return Provider.api();
    }

    void enable();

    void disable();

    /**
     * @return The plugin directory
     */
    @NotNull Path getDirectory();

    /**
     * A platform-agnostic implementation of Console Sender.
     *
     * @return The console sender
     */
    @NotNull Console getConsole();

    @NotNull PlayerRegistry getPlayerRegistry();

    @NotNull MiniMessage getMessage();

}