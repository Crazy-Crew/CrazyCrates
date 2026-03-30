package com.badbones69.crazycrates.paper.api.objects.items;

import com.badbones69.crazycrates.paper.utils.ItemUtils;
import com.ryderbelserion.fusion.paper.builders.gui.interfaces.GuiAction;
import com.ryderbelserion.fusion.paper.builders.gui.types.simple.SimpleGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;

public class DisplayItem {

    private final CommentedConfigurationNode configuration;
    private final boolean isEnabled;
    private final int slot;

    public DisplayItem(@NotNull final CommentedConfigurationNode configuration) {
        this.isEnabled = configuration.node("enabled").getBoolean(true);
        this.slot = configuration.node("slot").getInt(-1);

        this.configuration = configuration;
    }

    public void addItem(@NotNull final Player player, @NotNull final SimpleGui gui, @NotNull final GuiAction<InventoryClickEvent> action) {
        if (!this.isEnabled) {
            return;
        }

        if (this.slot == -1) {
            return;
        }

        gui.addSlotAction(this.slot, ItemUtils.convertNode(this.configuration.node("item")).asItemStack(player), action);
    }
}