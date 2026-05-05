package com.badbones69.crazycrates.paper.api.builders.gui;

import com.badbones69.common.config.beans.inventories.ItemPlacement;
import com.badbones69.common.config.impl.ConfigKeys;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.ryderbelserion.fusion.paper.builders.gui.enums.GuiState;
import com.ryderbelserion.fusion.paper.builders.gui.types.paginated.PaginatedGui;
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

        this.gui = PaginatedGui.gui(this.plugin, player, title, rows).addState(GuiState.block_all_interactions).build();

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

        if (this.gui.getPageNumber() <= 1) {
            setFillerItem(safeRow, column, isPreview);

            return;
        }

        this.gui.addSlotAction(safeRow, column, this.inventoryManager.getBackButton(this.player, this.gui), event -> {
            event.setCancelled(true);

            this.gui.nextPage();

            final int page = this.gui.getPageNumber();

            if (page <= 1) {
                setFillerItem(safeRow, column, isPreview);
            } else {
                addBackButton(isPreview);
            }

            if (page < this.gui.getMaxPages()) {
                addNextButton(isPreview);
            }
        });
    }

    public void addNextButton(final boolean isPreview) {
        final int number = this.gui.getPageNumber();

        if (number >= this.gui.getMaxPages()) {
            return;
        }

        final ItemPlacement nextButtonPlacement = this.config.getProperty(ConfigKeys.next_button_placement);
        final int column = nextButtonPlacement.getColumn();
        final int row = nextButtonPlacement.getRow();
        final int rows = this.gui.getRows();

        final int safeRow = Math.min(row == -1 ? rows : row, rows);

        this.gui.addSlotAction(safeRow, column, this.inventoryManager.getNextButton(this.player, this.gui), event -> {
            event.setCancelled(true);

            this.gui.nextPage();

            final int page = this.gui.getPageNumber();

            if (page >= this.gui.getMaxPages()) {
                setFillerItem(safeRow, column, isPreview);
            } else {
                addNextButton(isPreview);
            }

            if (page <= 1) {
                if (this.crate != null && this.crate.isBorderToggle()) {
                    this.gui.addSlotAction(safeRow, column, this.crate.getBorderItem().asGuiItem(this.player));
                } else {
                    if (!isPreview) {
                        this.gui.removeItem(safeRow, column);

                        this.gui.addSlotAction(safeRow, column, ItemType.BLACK_STAINED_GLASS_PANE.createItemStack(), _ -> {});
                    }
                }
            } else {
                addBackButton(isPreview);
            }
        });
    }

    private void setFillerItem(int row, int column, boolean isPreview) {
        if (this.crate != null && this.crate.isBorderToggle()) {
            this.gui.addSlotAction(row, column, this.crate.getBorderItem().asGuiItem(this.player));
        } else {
            if (!isPreview) {
                this.gui.removeItem(row, column);

                this.gui.addSlotAction(row, column, ItemType.BLACK_STAINED_GLASS_PANE.createItemStack(), _ -> {});
            }
        }
    }
}