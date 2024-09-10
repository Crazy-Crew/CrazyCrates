package com.badbones69.crazycrates.api.builders.v2;

import com.badbones69.crazycrates.api.objects.Crate;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.Gui;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.GuiAction;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.GuiItem;
import com.ryderbelserion.vital.paper.api.builders.gui.types.PaginatedGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class DynamicInventoryBuilder extends InventoryBuilder {

    private final PaginatedGui gui;
    private final Player player;
    private final Crate crate;

    public DynamicInventoryBuilder(final Player player, final String title, final int rows) {
        this.gui = Gui.paginated().setTitle(title).setRows(rows).disableInteractions().create();

        this.player = player;
        this.crate = null;
    }

    public DynamicInventoryBuilder(final Player player, final Crate crate) {
        this.gui = Gui.paginated().setTitle(crate.getPreviewName()).setRows(crate.getPreviewTierCrateRows()).disableInteractions().create();

        this.player = player;
        this.crate = crate;
    }

    public abstract void open();

    /**
     * Gets the {@link Player}.
     *
     * @return {@link Player}
     */
    public final Player getPlayer() {
        return this.player;
    }

    /**
     * Gets the {@link Crate}.
     *
     * @return {@link Crate}
     */
    public @Nullable Crate getCrate() {
        return this.crate;
    }

    /**
     * Gets the {@link PaginatedGui}.
     *
     * @return {@link PaginatedGui}
     */
    public final PaginatedGui getGui() {
        return this.gui;
    }

    // Adds the back button
    public void setBackButton(final int row, final int column, final @Nullable GuiAction<@NotNull InventoryClickEvent> action) {
        this.gui.setItem(row, column, new GuiItem(this.inventoryManager.getBackButton(this.player), action));
    }

    // Adds the next button
    public void setNextButton(final int row, final int column, final @Nullable GuiAction<@NotNull InventoryClickEvent> action) {
        this.gui.setItem(row, column, new GuiItem(this.inventoryManager.getNextButton(this.player), action));
    }
}