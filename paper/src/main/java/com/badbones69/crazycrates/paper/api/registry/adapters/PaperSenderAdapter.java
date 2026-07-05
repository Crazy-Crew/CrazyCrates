package com.badbones69.crazycrates.paper.api.registry.adapters;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.CrazyCratesPaper;
import com.ryderbelserion.fusion.core.api.FusionKey;
import com.ryderbelserion.fusion.core.api.registry.message.MessageRegistry;
import com.ryderbelserion.fusion.paper.FusionPaper;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.adapters.sender.ISenderAdapter;
import us.crazycrew.crazycrates.api.config.impl.ConfigManager;
import us.crazycrew.crazycrates.api.config.impl.types.plugin.PluginConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PaperSenderAdapter extends ISenderAdapter<Component, CommandSender> {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrazyCratesPaper platform = this.plugin.getPlatform();

    private final FusionPaper fusion = this.platform.getFusion();

    private final MessageRegistry messageRegistry = this.fusion.getMessageRegistry();

    private final ConfigManager configManager = this.platform.getConfigManager();

    private final PluginConfig pluginConfig = this.configManager.getPluginConfig();

    @Override
    public UUID getUniqueId(@NotNull final CommandSender sender) {
        if (sender instanceof Player player) {
            return player.getUniqueId();
        }

        return us.crazycrew.crazycrates.api.CrazyCrates.CONSOLE_UUID;
    }

    @Override
    public String getName(@NotNull final CommandSender sender) {
        if (sender instanceof Player player) {
            return player.getName();
        }

        return us.crazycrew.crazycrates.api.CrazyCrates.CONSOLE_NAME;
    }

    @Override
    public void sendActionBar(@NotNull final CommandSender sender, @NotNull final FusionKey id, @NotNull final Map<String, String> placeholders) {
        final Component component = getComponent(sender, id, placeholders);

        if (component.equals(Component.empty())) {
            return;
        }

        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(component);

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

        final String prefix = this.pluginConfig.getPrefix();

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

        final String prefix = this.pluginConfig.getPrefix();

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