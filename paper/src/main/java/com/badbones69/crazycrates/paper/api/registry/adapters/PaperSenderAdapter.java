package com.badbones69.crazycrates.paper.api.registry.adapters;

import ch.jalu.configme.SettingsManager;
import com.badbones69.common.config.ConfigManager;
import com.badbones69.common.config.impl.ConfigKeys;
import com.badbones69.crazycrates.paper.api.CrazyCratesPaper;
import com.ryderbelserion.fusion.core.api.FusionKey;
import com.ryderbelserion.fusion.core.api.registry.message.MessageRegistry;
import com.ryderbelserion.fusion.paper.FusionPaper;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import us.crazycrew.crazycrates.api.CrazyCrates;
import us.crazycrew.crazycrates.api.adapters.sender.ISenderAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class PaperSenderAdapter extends ISenderAdapter<Component, CommandSender> {

    private final SettingsManager config = ConfigManager.getConfig();

    private final MessageRegistry messageRegistry;
    private final FusionPaper fusion;

    public PaperSenderAdapter(@NotNull final CrazyCratesPaper platform) {
        this.fusion = platform.getFusion();
        this.messageRegistry = this.fusion.getMessageRegistry();
    }

    @Override
    public UUID getUniqueId(@NotNull final CommandSender sender) {
        if (sender instanceof Player player) {
            return player.getUniqueId();
        }

        return CrazyCrates.CONSOLE_UUID;
    }

    @Override
    public String getName(@NotNull final CommandSender sender) {
        if (sender instanceof Player player) {
            return player.getName();
        }

        return CrazyCrates.CONSOLE_NAME;
    }

    @Override
    public void sendActionBar(@NotNull final CommandSender sender, @NotNull final FusionKey id, @NotNull final Map<String, String> placeholders) {
        final Component component = getComponent(sender, id, placeholders);

        if (component.equals(Component.empty())) {
            return;
        }

        sender.sendActionBar(component);
    }

    @Override
    public void sendMessage(@NotNull final CommandSender sender, @NotNull final FusionKey id, @NotNull final Map<String, String> placeholders) {
        final Component component = getComponent(sender, id, placeholders);

        if (component.equals(Component.empty())) {
            return;
        }

        sender.sendMessage(component);
    }

    @Override
    public Component getComponent(@NotNull final CommandSender sender, @NotNull final FusionKey id, @NotNull final Map<String, String> placeholders) {
        final List<String> values = new ArrayList<>();

        this.messageRegistry.getMessage(id).ifPresent(value -> values.add(value.getValue()));

        if (values.isEmpty()) {
            return Component.empty();
        }

        final Map<String, String> map = new HashMap<>(placeholders);

        final String prefix = this.config.getProperty(ConfigKeys.command_prefix);

        if (!prefix.isEmpty()) {
            map.putIfAbsent("{prefix}", prefix);
        }

        return this.fusion.asComponent(sender, values.getFirst(), map);
    }

    @Override
    public String getMessage(@NotNull final CommandSender sender, @NotNull final FusionKey id, @NotNull final Map<String, String> placeholders) {
        final List<String> values = new ArrayList<>();

        this.messageRegistry.getMessage(id).ifPresent(value -> values.add(value.getValue()));

        if (values.isEmpty()) {
            return "";
        }

        final Map<String, String> map = new HashMap<>(placeholders);

        final String prefix = this.config.getProperty(ConfigKeys.command_prefix);

        if (!prefix.isEmpty()) {
            map.putIfAbsent("{prefix}", prefix);
        }

        return this.fusion.replacePlaceholders(this.fusion.papi(sender, values.getFirst()), map);
    }

    @Override
    public boolean isConsole(@NotNull final CommandSender sender) {
        return sender instanceof ConsoleCommandSender;
    }
}