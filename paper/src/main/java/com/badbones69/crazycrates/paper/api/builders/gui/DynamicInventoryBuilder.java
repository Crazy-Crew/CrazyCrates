package com.badbones69.crazycrates.paper.api.builders.gui;

import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.ryderbelserion.fusion.paper.api.builder.gui.objects.Gui;
import com.ryderbelserion.fusion.paper.api.builder.gui.objects.GuiItem;
import com.ryderbelserion.fusion.paper.api.builder.gui.types.PaginatedGui;
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

        this.gui = Gui.paginated().setTitle(title).setRows(rows).disableInteractions().create();

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

    // Adds the back button
    public void setBackButton(final int row, final int column, final boolean isPreview) {
        if (this.gui.getCurrentPageNumber() <= 1) {
            setFillerItem(row, column, isPreview);

            return;
        }

        this.gui.setItem(this.gui.getSlotFromRowColumn(row, column), new GuiItem(this.inventoryManager.getBackButton(this.player, this.gui), event -> {
            event.setCancelled(true);

            this.gui.previous();

            final int page = this.gui.getCurrentPageNumber();

            if (page <= 1) {
                setFillerItem(row, column, isPreview);
            } else {
                setBackButton(row, column, isPreview);
            }

            if (page < this.gui.getMaxPages()) {
                setNextButton(this.gui.getRows(), 6, isPreview);
            }
        }));

        addMenuButton(this.player, this.crate, this.gui, this.gui.getRows(), 5);
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

    // Adds the next button
    public void setNextButton(final int row, final int column, final boolean isPreview) {
        if (this.gui.getCurrentPageNumber() >= this.gui.getMaxPages()) {
            return;
        }

        this.gui.setItem(this.gui.getSlotFromRowColumn(row, column), new GuiItem(this.inventoryManager.getNextButton(this.player, this.gui), event -> {
            event.setCancelled(true);

            this.gui.next();

            final int page = this.gui.getCurrentPageNumber();

            if (page >= this.gui.getMaxPages()) {
                setFillerItem(row, column, isPreview);
            } else {
                setNextButton(row, column, isPreview);
            }

            final int rows = this.gui.getRows();

            if (page <= 1) {
                if (this.crate != null && this.crate.isBorderToggle()) {
                    this.gui.setItem(rows, 4, this.crate.getBorderItem().asGuiItem());
                } else {
                    if (!isPreview) {
                        this.gui.removeItem(rows, 4);

                        this.gui.setItem(rows, 4, new GuiItem(ItemType.BLACK_STAINED_GLASS_PANE));
                    }
                }
            } else {
                setBackButton(rows, 4, isPreview);
            }
        }));

        addMenuButton(this.player, this.crate, this.gui, this.gui.getRows(), 5);
    }
}