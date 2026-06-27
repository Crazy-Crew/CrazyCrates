package com.ryderbelserion.crazycrates.paper.api.objects.items;

import com.ryderbelserion.crazycrates.paper.api.enums.DisplayType;
import com.ryderbelserion.crazycrates.paper.utils.ItemUtils;
import com.ryderbelserion.fusion.paper.builders.gui.interfaces.GuiAction;
import com.ryderbelserion.fusion.paper.builders.gui.objects.GuiItem;
import com.ryderbelserion.fusion.paper.builders.gui.types.paginated.PaginatedGui;
import com.ryderbelserion.fusion.paper.builders.gui.types.simple.SimpleGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;

public final class DisplayItem {

    private final CommentedConfigurationNode configuration;
    private final DisplayType displayType;
    private final boolean isEnabled;
    private final int slot;

    public DisplayItem(@NotNull final CommentedConfigurationNode configuration, @NotNull final DisplayType displayType) {
        this.isEnabled = configuration.node("enabled").getBoolean(true);
        this.slot = configuration.node("slot").getInt(-1);

        this.displayType = displayType;
        this.configuration = configuration;
    }

    public void addItem(@NotNull final Player player, @NotNull final SimpleGui gui, final int slot, @NotNull final GuiAction<InventoryClickEvent> action) {
        if (!this.isEnabled) {
            return;
        }

        if (this.displayType == DisplayType.PRIZE) {
            return;
        }

        gui.addSlotAction(slot, ItemUtils.convertNode(this.configuration.node("item")).asItemStack(player), action);
    }

    public void addItem(@NotNull final Player player, @NotNull final SimpleGui gui, final int slot) {
        addItem(player, gui, slot, _ -> {});
    }

    public void addItem(@NotNull final Player player, @NotNull final PaginatedGui gui, final int slot, @NotNull final GuiAction<InventoryClickEvent> action) {
        if (!this.isEnabled) {
            return;
        }

        if (this.displayType == DisplayType.PRIZE) {
            gui.addPageItem(new GuiItem(ItemUtils.convertNode(this.configuration.node("item")).asItemStack(player), action));

            return;
        }

        gui.addSlotAction(slot, ItemUtils.convertNode(this.configuration.node("item")).asItemStack(player), action);
    }

    public void addItem(@NotNull final Player player, @NotNull final PaginatedGui gui, final int slot) {
        addItem(player, gui, slot, _ -> {});
    }

    public int getSlot() {
        return this.slot;
    }
}