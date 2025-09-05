package com.badbones69.crazycrates.paper.api.builders.gui;

import com.badbones69.crazycrates.core.config.beans.inventories.ItemPlacement;
import com.badbones69.crazycrates.core.config.impl.ConfigKeys;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.ryderbelserion.fusion.paper.api.builders.gui.interfaces.Gui;
import com.ryderbelserion.fusion.paper.api.builders.gui.interfaces.GuiItem;
import com.ryderbelserion.fusion.paper.api.builders.gui.types.PaginatedGui;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.function.Consumer;

public abstract class DynamicInventoryBuilder extends InventoryBuilder {

    private final PaginatedGui gui;
    private final Crate crate;

    public DynamicInventoryBuilder(@NotNull final Player player, @Nullable final Crate crate, @NotNull final String title, final int rows) {
        super(player);

        this.gui = Gui.paginated(this.plugin).setTitle(title).setRows(rows).disableInteractions().create();

        this.crate = crate;
    }

    public DynamicInventoryBuilder(@NotNull final Player player, @NotNull final String title, final int rows) {
        this(player, null, title, rows);
    }

    /**
     * Opens the {@link PaginatedGui}.
     */
    public void open() {
        open(null);
    }

    /**
     * Opens the {@link PaginatedGui}.
     *
     * @param consumer the consumer
     */
    public void open(@Nullable final Consumer<DynamicInventoryBuilder> consumer) {
        if (consumer != null) {
            consumer.accept(this);
        }
    }

    /**
     * Gets the {@link Player}.
     *
     * @return {@link Player}
     */
    public @NotNull final Player getPlayer() {
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
    public @NotNull final PaginatedGui getGui() {
        return this.gui;
    }

    public void addBackButton(final boolean isPreview) {
        final ItemPlacement backButtonPlacement = this.config.getProperty(ConfigKeys.back_button_placement);
        final int column = backButtonPlacement.getColumn();
        final int row = backButtonPlacement.getRow();
        final int rows = this.gui.getRows();

        final int safeRow = Math.min(row == -1 ? rows : row, rows);

        if (this.gui.getCurrentPageNumber() <= 1) {
            setFillerItem(safeRow, column, isPreview);

            return;
        }

        this.gui.setItem(safeRow, column, new GuiItem(this.inventoryManager.getBackButton(this.player, this.gui), event -> {
            event.setCancelled(true);

            this.gui.previous();

            final int page = this.gui.getCurrentPageNumber();

            if (page <= 1) {
                setFillerItem(safeRow, column, isPreview);
            } else {
                addBackButton(isPreview);
            }

            if (page < this.gui.getMaxPages()) {
                addNextButton(isPreview);
            }
        }));
    }

    public void addNextButton(final boolean isPreview) {
        if (this.gui.getCurrentPageNumber() >= this.gui.getMaxPages()) {
            return;
        }

        final ItemPlacement nextButtonPlacement = this.config.getProperty(ConfigKeys.next_button_placement);
        final int column = nextButtonPlacement.getColumn();
        final int row = nextButtonPlacement.getRow();
        final int rows = this.gui.getRows();

        final int safeRow = Math.min(row == -1 ? rows : row, rows);

        this.gui.setItem(safeRow, column, new GuiItem(this.inventoryManager.getNextButton(this.player, this.gui), event -> {
            event.setCancelled(true);

            this.gui.next();

            final int page = this.gui.getCurrentPageNumber();

            if (page >= this.gui.getMaxPages()) {
                setFillerItem(safeRow, column, isPreview);
            } else {
                addNextButton(isPreview);
            }

            if (page <= 1) {
                if (this.crate != null && this.crate.isBorderToggle()) {
                    this.gui.setItem(safeRow, column, this.crate.getBorderItem().asGuiItem());
                } else {
                    if (!isPreview) {
                        this.gui.removeItem(safeRow, column);

                        this.gui.setItem(safeRow, column, new GuiItem(ItemType.BLACK_STAINED_GLASS_PANE));
                    }
                }
            } else {
                addBackButton(isPreview);
            }
        }));
    }

    private void setFillerItem(int row, int column, boolean isPreview) {
        if (this.crate != null && this.crate.isBorderToggle()) {
            this.gui.setItem(row, column, this.crate.getBorderItem().asGuiItem());
        } else {
            if (!isPreview) {
                this.gui.removeItem(row, column);

                this.gui.setItem(row, column, new GuiItem(ItemType.BLACK_STAINED_GLASS_PANE));
            }
        }
    }
}