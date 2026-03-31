package com.badbones69.crazycrates.paper.api.objects.prize;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.CratePlatform;
import com.badbones69.crazycrates.paper.api.objects.items.DisplayItem;
import com.badbones69.crazycrates.paper.utils.ItemUtils;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.builders.items.ItemBuilder;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Prize {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CratePlatform platform = this.plugin.getPlatform();

    private final FusionPaper fusion = this.platform.getFusion();

    private final Server server = this.plugin.getServer();

    private final List<ItemBuilder> items = new ArrayList<>();

    private final List<String> commands = new ArrayList<>();
    private final List<String> messages = new ArrayList<>();

    private final String broadcastPermission;
    private final boolean isBroadcasting;
    private final String broadcastLine;

    private final DisplayItem item;

    private final String prizeName;

    public Prize(@NotNull final CommentedConfigurationNode configuration, @NotNull final String prizeName) {
        final CommentedConfigurationNode crate = configuration.node("crate");

        this.item = new DisplayItem(crate.node("preview"));

        final CommentedConfigurationNode broadcast = crate.node("broadcast");

        this.broadcastPermission = broadcast.node("permission").getString("");
        this.isBroadcasting = broadcast.node("enabled").getBoolean(false);
        this.broadcastLine = broadcast.node("messages").getString("");

        this.items.addAll(ItemUtils.convertNodes(configuration.node("items")));

        this.commands.addAll(StringUtils.getStringList(configuration.node("commands")));
        this.messages.addAll(StringUtils.getStringList(configuration.node("messages")));

        this.prizeName = prizeName;
    }

    public void execute(@NotNull final Player player, @NotNull final Map<String, String> placeholders) {
        final ConsoleCommandSender sender = this.server.getConsoleSender();

        for (final String command : this.commands) {
            if (command.isBlank()) continue;

            this.server.dispatchCommand(sender, this.fusion.parse(player, command, placeholders));
        }

        for (final String message : this.messages) {
            if (message.isBlank()) continue;

            player.sendMessage(this.fusion.asComponent(player, message, placeholders));
        }
    }

    public void broadcast(@NotNull final Player player, @NotNull final Map<String, String> placeholders) {
        if (!this.isBroadcasting || this.broadcastLine.isBlank()) return;

        if (this.broadcastPermission.isBlank()) {
            this.server.broadcast(this.fusion.asComponent(player, this.broadcastLine, placeholders));

            return;
        }

        this.server.broadcast(this.fusion.asComponent(player, this.broadcastLine, placeholders), this.broadcastPermission);
    }

    public @NotNull final List<ItemBuilder> getItems() {
        return this.items;
    }

    public @NotNull final String getPrizeName() {
        return this.prizeName;
    }

    public @NotNull final DisplayItem getItem() {
        return this.item;
    }
}