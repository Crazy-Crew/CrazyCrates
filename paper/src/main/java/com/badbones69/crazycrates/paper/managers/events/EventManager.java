package com.badbones69.crazycrates.paper.managers.events;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.CrazyCratesPaper;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.managers.events.enums.EventType;
import com.ryderbelserion.fusion.paper.FusionPaper;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.crazycrew.crazycrates.api.config.impl.ConfigManager;
import us.crazycrew.crazycrates.api.config.impl.types.config.crate.CrateKeys;
import us.crazycrew.crazycrates.api.config.properties.PropertyManager;
import us.crazycrew.crazycrates.api.enums.Files;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventManager {

    private final static CrazyCrates plugin = CrazyCrates.getPlugin();

    private final static CrazyCratesPaper platform = plugin.getPlatform();

    private final static ComponentLogger logger = plugin.getComponentLogger();

    private final static ConfigManager configManager = platform.getConfigManager();

    private final static PropertyManager pluginConfig = configManager.getConfig();

    private final static FusionPaper fusion = platform.getFusion();

    public static void logEvent(@NotNull final EventType type, @NotNull final String name, @NotNull final CommandSender sender, @NotNull final Crate crate, @NotNull final KeyType keyType, final int amount) {
        handle(type, name, sender, crate, keyType, amount);
    }

    private static void handle(@NotNull final EventType type, @NotNull final String name, @NotNull final CommandSender sender, @NotNull final Crate crate, @NotNull final KeyType keyType, final int amount) {
        String message = "";
        Files key = null;

        switch (type) {
            case event_key_given, event_key_removed, event_key_received, event_key_sent, event_key_taken, event_key_taken_multiple,
                 event_key_transferred -> {
                message = "Player: %player% | Sender: %sender% | Key Name: %key_name%<reset> | Key Type: %key_type%"
                        .replace("%key_type%", keyType.getFriendlyName())
                        .replace("%player%", name)
                        .replace("%sender%", sender.getName())
                        .replace("%key_name%", crate.getKeyName());

                if (amount >= 1) {
                    message = message + " | Amount: %amount%".replace("%amount%", String.valueOf(amount));
                }

                key = Files.key_log;
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

                key = Files.crate_log;
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

                key = Files.crate_log;
            }
        }

        log(message, key, type);
    }

    private static void log(@NotNull final String message, @Nullable final Files key, @NotNull final EventType type) {
        final String time = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date(System.currentTimeMillis()));

        if (key != null && pluginConfig.getProperty(CrateKeys.log_to_file)) {
            key.getLogCustomFile().save("[" + time + " " + type.getEvent() + "]: " + PlainTextComponentSerializer.plainText().serialize(fusion.asComponent(message)));
        }

        if (pluginConfig.getProperty(CrateKeys.log_to_console)) {
            logger.info("[{} {}]: {}", time, type.getEvent(), fusion.asComponent(message));
        }
    }
}