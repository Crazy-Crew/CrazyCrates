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
     * @return The root directory of the plugin.
     */
    @NotNull Path getRootDirectory();

    /**
     * Used to manage players across platforms.
     *
     * @return The instance of PlayerRegistry
     */
    @NotNull PlayerRegistry getPlayerRegistry();

    /**
     * @return The instance of Mini-Message.
     */
    @NotNull MiniMessage getMiniMessage();
}