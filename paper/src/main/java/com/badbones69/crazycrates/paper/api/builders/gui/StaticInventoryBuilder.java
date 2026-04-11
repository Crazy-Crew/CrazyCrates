package com.badbones69.crazycrates.paper.api.builders.gui;

import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.ryderbelserion.fusion.paper.builders.gui.enums.GuiState;
import com.ryderbelserion.fusion.paper.builders.gui.types.simple.SimpleGui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class StaticInventoryBuilder extends InventoryBuilder {

    private final Player player;
    private final Crate crate;

    private final SimpleGui gui;

    /**
     * Builds an inventory with a set title/rows
     *
     * @param player {@link Player}
     * @param title {@link String}
     * @param rows {@link Integer}
     */
    public StaticInventoryBuilder(@NotNull final Player player, @NotNull final Crate crate, @NotNull final String title, final int rows) {
        super(player);

        this.gui = SimpleGui.gui(this.plugin, player, title, rows).addState(GuiState.block_all_interactions);

        this.player = player;
        this.crate = crate;
    }

    /**
     * Builds an inventory with a set title/rows
     *
     * @param player {@link Player}
     * @param title {@link String}
     * @param rows {@link Integer}
     */
    public StaticInventoryBuilder(@NotNull final Player player, @NotNull final String title, final int rows) {
        super(player);

        this.gui = SimpleGui.gui(this.plugin, player, title, rows).addState(GuiState.block_all_interactions);

        this.player = player;
        this.crate = null;
    }

    /**
     * Builds an inventory with a set title/rows
     *
     * @param player {@link Player}
     * @param crate {@link String}
     */
    public StaticInventoryBuilder(@NotNull final Player player, @NotNull final Crate crate) {
        super(player);

        this.gui = SimpleGui.gui(this.plugin, player, crate.getPreviewName(), crate.getPreviewTierCrateRows()).addState(GuiState.block_all_interactions);

        this.player = player;
        this.crate = crate;
    }

    public abstract void open();

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
     * Gets the title of the gui.
     *
     * @return the title of the gui
     */
    public @NotNull final String getTitle() {
        return this.gui.getTitle();
    }

    /**
     * Checks if the title contains a message.
     *
     * @param message the message to check
     * @return true or false
     */
    public final boolean contains(@NotNull final String message) {
        return getTitle().contains(message);
    }

    /**
     * Gets the {@link SimpleGui}.
     *
     * @return {@link SimpleGui}
     */
    public @NotNull final SimpleGui getGui() {
        return this.gui;
    }
}