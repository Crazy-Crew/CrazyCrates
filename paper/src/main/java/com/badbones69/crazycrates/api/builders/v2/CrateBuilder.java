package com.badbones69.crazycrates.api.builders.v2;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.events.crates.CrateStatusEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.Gui;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.GuiItem;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.GuiType;
import com.ryderbelserion.vital.paper.util.scheduler.FoliaRunnable;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.crazycrew.crazycrates.api.enums.types.KeyType;

@SuppressWarnings("UnusedReturnValue")
public abstract class CrateBuilder extends FoliaRunnable {

    protected CrazyCrates plugin = CrazyCrates.getPlugin();
    protected ComponentLogger logger = this.plugin.getComponentLogger();

    private final CrateStatusEvent event;

    private final Player player;
    private final Crate crate;

    private final Gui gui;

    private String title;
    private int seconds;

    public CrateBuilder(final Player player, final Crate crate, final int rows) {
        super(player.getScheduler(), null);

        this.gui = Gui.gui().setType(GuiType.CHEST).disableInteractions().setTitle(crate.getCrateName()).setRows(rows).create();
        this.event = new CrateStatusEvent(player, crate);

        this.player = player;
        this.crate = crate;
    }

    public abstract void open(final KeyType keyType, final boolean inspectInventory);

    public final CrateBuilder setSeconds(final int seconds) {
        this.seconds = seconds;

        return this;
    }

    public final CrateBuilder setTitle(final String title) {
        this.gui.setTitle(this.title = title);

        return this;
    }

    // Items
    public void setGuiItem(final ItemStack item, final int slot) {
        this.gui.setItem(slot, new GuiItem(item));
    }

    public void setRandomGlass(final int slot) {
        setGuiItem(getRandomGlass(), slot);
    }

    // Getters
    public final ItemStack getRandomGlass() {
        return MiscUtils.getRandomPaneColor().setDisplayName(" ").getStack();
    }

    public final CrateStatusEvent getEvent() {
        return this.event;
    }

    public final Player getPlayer() {
        return this.player;
    }

    public final String getTitle() {
        return this.title;
    }

    public final Crate getCrate() {
        return this.crate;
    }

    public final int getSeconds() {
        return this.seconds;
    }

    public final Gui getGui() {
        return this.gui;
    }
}