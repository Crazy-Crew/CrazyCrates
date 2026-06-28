package com.badbones69.crazycrates.paper.api.objects.buttons;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.CrazyCratesPaper;
import com.badbones69.crazycrates.paper.api.enums.DisplayType;
import com.badbones69.crazycrates.paper.api.objects.items.DisplayItem;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Button {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrazyCratesPaper platform = this.plugin.getPlatform();

    private final FusionPaper fusion = this.platform.getFusion();

    private final Server server = this.plugin.getServer();

    private final List<String> commands = new ArrayList<>();
    private final List<String> messages = new ArrayList<>();

    private final DisplayItem displayItem;

    public Button(@NotNull final CommentedConfigurationNode configuration) {
        final CommentedConfigurationNode display = configuration.node("display");

        this.displayItem = new DisplayItem(display, DisplayType.BUTTON);

        this.commands.addAll(StringUtils.getStringList(configuration.node("commands")));
        this.messages.addAll(StringUtils.getStringList(configuration.node("messages")));
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

    public @NotNull final DisplayItem getDisplayItem() {
        return this.displayItem;
    }
}