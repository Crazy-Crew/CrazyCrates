package com.badbones69.crazycrates.api.builders.v2;

import com.badbones69.crazycrates.api.objects.Crate;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.Gui;
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
    public StaticInventoryBuilder(final Player player, final String title, final int rows) {
        this.gui = Gui.gui().setTitle(title).setRows(rows).disableInteractions().create();

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
        this.gui = Gui.gui().setTitle(crate.getPreviewName()).setRows(crate.getPreviewTierCrateRows()).disableInteractions().create();

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
     * Gets the {@link Gui}.
     *
     * @return {@link Gui}
     */
    public final Gui getGui() {
        return this.gui;
    }
}