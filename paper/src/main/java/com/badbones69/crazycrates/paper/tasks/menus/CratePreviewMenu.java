package com.badbones69.crazycrates.paper.tasks.menus;

import com.badbones69.crazycrates.paper.api.builders.gui.DynamicInventoryBuilder;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.Tier;
import com.ryderbelserion.fusion.paper.api.builder.gui.objects.GuiFiller;
import com.ryderbelserion.fusion.paper.api.builder.gui.objects.GuiItem;
import com.ryderbelserion.fusion.paper.api.builder.gui.types.PaginatedGui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CratePreviewMenu extends DynamicInventoryBuilder {

    private final Tier tier;

    public CratePreviewMenu(@NotNull final Player player, @NotNull final Crate crate, @NotNull final Tier tier) {
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

        final UUID uuid = this.player.getUniqueId();

        crate.getPreviewItems(this.player, this.tier).forEach(itemStack -> this.gui.addItem(new GuiItem(itemStack)));

        this.gui.setOpenGuiAction(event -> this.inventoryManager.addPreviewViewer(uuid));

        this.gui.setCloseGuiAction(event -> this.inventoryManager.removePreviewViewer(uuid));

        this.gui.open(this.player, gui -> {
            final int rows = gui.getRows();

            setBackButton(rows, 4, true);
            setNextButton(rows, 6, true);

            addMenuButton(this.player, crate, this.gui, rows, 5);
        });
    }
}