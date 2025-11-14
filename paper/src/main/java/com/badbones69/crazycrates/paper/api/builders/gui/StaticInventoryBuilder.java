package com.badbones69.crazycrates.paper.api.builders.gui;

import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.Tier;
import com.ryderbelserion.fusion.paper.builders.gui.interfaces.Gui;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class StaticInventoryBuilder extends InventoryBuilder {

    private final List<Tier> tiers;
    private final Player player;
    private final Crate crate;
    private final Gui gui;

    /**
     * Builds an inventory with a set title/rows
     *
     * @param player {@link Player}
     * @param title {@link String}
     * @param rows {@link Integer}
     */
    public StaticInventoryBuilder(@NotNull final Player player, @NotNull final Crate crate, @NotNull final List<Tier> tiers, @NotNull final String title, final int rows) {
        super(player);

        this.gui = Gui.gui(this.plugin).setTitle(this.fusion.papi(player, title)).setRows(rows).disableInteractions().create();

        this.player = player;
        this.crate = crate;
        this.tiers = tiers;
    }

    /**
     * Builds an inventory with a set title/rows
     *
     * @param player {@link Player}
     * @param title {@link String}
     * @param rows {@link Integer}
     */
    public StaticInventoryBuilder(@NotNull final Player player, @NotNull final Crate crate, @NotNull final String title, final int rows) {
        super(player);

        this.gui = Gui.gui(this.plugin).setTitle(this.fusion.papi(player, title)).setRows(rows).disableInteractions().create();

        this.player = player;
        this.crate = crate;
        this.tiers = new ArrayList<>();
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

        this.gui = Gui.gui(this.plugin).setTitle(this.fusion.papi(player, title)).setRows(rows).disableInteractions().create();

        this.player = player;
        this.crate = null;
        this.tiers = new ArrayList<>();
    }

    /**
     * Builds an inventory with a set title/rows
     *
     * @param player {@link Player}
     * @param crate {@link String}
     */
    public StaticInventoryBuilder(@NotNull final Player player, @NotNull final Crate crate) {
        super(player);

        this.gui = Gui.gui(this.plugin).setTitle(this.fusion.papi(player, crate.getPreviewName())).setRows(crate.getPreviewTierCrateRows()).disableInteractions().create();

        this.player = player;
        this.crate = crate;
        this.tiers = new ArrayList<>();
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
    public @NotNull String getTitle() {
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
     * Updates the gui title.
     *
     * @param message the new title
     */
    public void updateTitle(@NotNull final String message) {
        this.gui.setTitle(message);
        this.gui.updateTitle(this.player);
    }

    /**
     * Get a list of tiers.
     *
     * @return list of tiers
     */
    public @NotNull final List<Tier> getTiers() {
        return this.tiers;
    }

    /**
     * Fetches the inventory view.
     *
     * @return the inventory view
     */
    public @NotNull final InventoryView getView() {
        return getPlayer().getOpenInventory();
    }

    /**
     * Fetches the gui inventory.
     *
     * @return the inventory
     */
    public @NotNull final Inventory getInventory() {
        return getGui().getInventory();
    }

    /**
     * Gets the {@link Gui}.
     *
     * @return {@link Gui}
     */
    public @NotNull final Gui getGui() {
        return this.gui;
    }
}