package com.badbones69.crazycrates.paper.api.builders.gui;

import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.ryderbelserion.fusion.paper.builder.gui.interfaces.Gui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public abstract class StaticInventoryBuilder extends InventoryBuilder {

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
    public StaticInventoryBuilder(final Player player, final Crate crate, final String title, final int rows) {
        super(player);

        this.gui = Gui.gui().setTitle(parse(player, title)).setRows(rows).disableInteractions().create();

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
    public StaticInventoryBuilder(final Player player, final String title, final int rows) {
        super(player);

        this.gui = Gui.gui().setTitle(parse(player, title)).setRows(rows).disableInteractions().create();

        this.player = player;
        this.crate = null;
    }

    /**
     * Builds an inventory with a set title/rows
     *
     * @param player {@link Player}
     * @param crate {@link String}
     */
    public StaticInventoryBuilder(final Player player, final Crate crate) {
        super(player);

        this.gui = Gui.gui().setTitle(parse(player, crate.getPreviewName())).setRows(crate.getPreviewTierCrateRows()).disableInteractions().create();

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
     * Gets the title of the gui.
     *
     * @return the title of the gui
     */
    public final String getTitle() {
        return this.gui.getTitle();
    }

    /**
     * Checks if the title contains a message.
     *
     * @param message the message to check
     * @return true or false
     */
    public final boolean contains(final String message) {
        return getTitle().contains(message);
    }

    /**
     * Gets the {@link Gui}.
     *
     * @return {@link Gui}
     */
    public final Gui getGui() {
        return this.gui;
    }
}