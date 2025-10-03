package com.badbones69.crazycrates.paper.managers.events;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.enums.other.keys.FileKeys;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.core.config.ConfigManager;
import com.badbones69.crazycrates.core.config.impl.ConfigKeys;
import com.badbones69.crazycrates.paper.managers.events.enums.EventType;
import com.ryderbelserion.fusion.core.api.utils.AdvUtils;
import com.ryderbelserion.fusion.core.files.types.LogCustomFile;
import com.ryderbelserion.fusion.paper.files.FileManager;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EventManager {

    private final static CrazyCrates plugin = CrazyCrates.getPlugin();

    private final static ComponentLogger logger = plugin.getComponentLogger();

    private final static SettingsManager config = ConfigManager.getConfig();

    public static void logEvent(@NotNull final EventType type, @NotNull final String name, @NotNull final CommandSender sender, @NotNull final Crate crate, @NotNull final KeyType keyType, final int amount) {
        handle(type, name, sender, crate, keyType, amount);
    }

    private static void handle(@NotNull final EventType type, @NotNull final String name, @NotNull final CommandSender sender, @NotNull final Crate crate, @NotNull final KeyType keyType, final int amount) {
        String message = "";
        Path path = null;

        switch (type) {
            case event_key_given, event_key_removed, event_key_received, event_key_sent, event_key_taken, event_key_taken_multiple -> {
                message = "Player: %player% | Sender: %sender% | Key Name: %key_name%<reset> | Key Type: %key_type%"
                        .replace("%key_type%", keyType.getFriendlyName())
                        .replace("%player%", name)
                        .replace("%sender%", sender.getName())
                        .replace("%key_name%", crate.getKeyName());

                if (amount >= 1) {
                    message = message + " | Amount: %amount%".replace("%amount%", String.valueOf(amount));
                }

                path = FileKeys.key_log.getPath();
            }

            /*case event_command_sent -> {

            }

            case event_command_failed -> {

            }*/

            case event_crate_opened -> {
                message = "Player: %player% | Crate Name: %crate_name%<reset> | Force Opened: %force_opened% | Crate Type: %crate_type% | Key Name: %key_name%<reset> | Key Type: %key_type% | Key Item: %key_item%"
                        .replace("%player%", name)
                        .replace("%crate_name%", crate.getCrateName())
                        .replace("%crate_type%", crate.getCrateType().getName())
                        .replace("%key_name%", crate.getKeyName())
                        .replace("%key_type%", keyType.getFriendlyName())
                        .replace("%key_item%", crate.getKey().getType().getKey().getKey())
                        .replace("%force_opened%", "no");

                if (amount >= 1) {
                    message = message + " | Amount: %amount%".replace("%amount%", String.valueOf(amount));
                }

                path = FileKeys.crate_log.getPath();
            }

            case event_crate_force_opened -> {
                message = "Player: %player% | Crate Name: %crate_name%<reset> | Force Opened: %force_opened% | Crate Type: %crate_type% | Key Name: %key_name%<reset> | Key Type: %key_type% | Key Item: %key_item%"
                        .replace("%player%", name)
                        .replace("%crate_name%", crate.getCrateName())
                        .replace("%crate_type%", crate.getCrateType().getName())
                        .replace("%key_name%", crate.getKeyName())
                        .replace("%key_type%", keyType.getFriendlyName())
                        .replace("%key_item%", crate.getKey().getType().getKey().getKey())
                        .replace("%force_opened%", "yes");

                if (amount >= 1) {
                    message = message + " | Amount: %amount%".replace("%amount%", String.valueOf(amount));
                }

                path = FileKeys.crate_log.getPath();
            }
        }

        log(message, path, type);
    }

    private static final FileManager fileManager = plugin.getFileManager();

    private static void log(@NotNull final String message, @Nullable final Path path, @NotNull final EventType type) {
        final String time = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date(System.currentTimeMillis()));

        if (path != null && config.getProperty(ConfigKeys.log_to_file)) {
            final LogCustomFile customFile = (LogCustomFile) fileManager.getCustomFile(path);

            if (customFile != null) {
                customFile.save("[" + time + " " + type.getEvent() + "]: " + PlainTextComponentSerializer.plainText().serialize(AdvUtils.parse(message)), new ArrayList<>());
            }
        }

        final boolean log_to_console = config.getProperty(ConfigKeys.log_to_console);

        if (log_to_console) {
            logger.info("[{} {}]: {}", time, type.getEvent(), AdvUtils.parse(message));
        }
    }
}