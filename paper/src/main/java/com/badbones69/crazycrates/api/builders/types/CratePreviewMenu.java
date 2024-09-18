package com.badbones69.crazycrates.api.builders.types;

import com.badbones69.crazycrates.api.builders.gui.DynamicInventoryBuilder;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Tier;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.GuiFiller;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.GuiItem;
import com.ryderbelserion.vital.paper.api.builders.gui.types.PaginatedGui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CratePreviewMenu extends DynamicInventoryBuilder {

    private final Tier tier;

    public CratePreviewMenu(final Player player, final Crate crate, final Tier tier) {
        super(player, crate, crate.getPreviewName(), crate.getPreviewChestLines());

        this.tier = tier;
    }

    private final Player player = getPlayer();

    @Override
    public void open() {
        final Crate crate = getCrate();

        if (crate == null) return;

        final PaginatedGui gui = setGui().getGui();

        if (crate.isBorderToggle()) {
            final GuiFiller guiFiller = gui.getFiller();

            final GuiItem guiItem = new GuiItem(crate.getBorderItem().asItemStack());

            guiFiller.fillTop(guiItem);
            guiFiller.fillBottom(guiItem);
        }

        crate.getPreviewItems(this.player, this.tier).forEach(itemStack -> gui.addItem(new GuiItem(itemStack)));

        setBackButton(6, 4);
        setNextButton(6, 6);

        addMenuButton(this.player, crate, gui, 6, 5);

        gui.setOpenGuiAction(event -> this.inventoryManager.addPreviewViewer(event.getPlayer().getUniqueId()));

        gui.setCloseGuiAction(event -> this.inventoryManager.removePreviewViewer(event.getPlayer().getUniqueId()));

        gui.open(this.player);
    }
}