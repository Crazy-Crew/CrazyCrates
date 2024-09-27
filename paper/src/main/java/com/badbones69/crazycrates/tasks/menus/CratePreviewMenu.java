package com.badbones69.crazycrates.tasks.menus;

import com.badbones69.crazycrates.api.builders.gui.DynamicInventoryBuilder;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Tier;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.GuiFiller;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.GuiItem;
import com.ryderbelserion.vital.paper.api.builders.gui.types.PaginatedGui;
import org.bukkit.entity.Player;

public class CratePreviewMenu extends DynamicInventoryBuilder {

    private final Tier tier;

    public CratePreviewMenu(final Player player, final Crate crate, final Tier tier) {
        super(player, crate, crate.getPreviewName(), crate.getPreviewChestLines());

        this.tier = tier;
    }

    private final Player player = getPlayer();
    private final PaginatedGui gui = getGui();

    @Override
    public void open() {
        final Crate crate = getCrate();

        if (crate == null) return;

        if (crate.isBorderToggle()) {
            final GuiFiller guiFiller = this.gui.getFiller();

            final GuiItem guiItem = new GuiItem(crate.getBorderItem().asItemStack());

            guiFiller.fillTop(guiItem);
            guiFiller.fillBottom(guiItem);
        }

        crate.getPreviewItems(this.player, this.tier).forEach(itemStack -> this.gui.addItem(new GuiItem(itemStack)));

        this.gui.setOpenGuiAction(event -> this.inventoryManager.addPreviewViewer(event.getPlayer().getUniqueId()));

        this.gui.setCloseGuiAction(event -> this.inventoryManager.removePreviewViewer(event.getPlayer().getUniqueId()));

        this.gui.open(this.player, gui -> {
            final int rows = gui.getRows();

            setBackButton(rows, 4, true);
            setNextButton(rows, 6, true);

            addMenuButton(this.player, crate, this.gui, rows, 5);
        });
    }
}