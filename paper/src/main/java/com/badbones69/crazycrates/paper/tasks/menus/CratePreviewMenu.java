package com.badbones69.crazycrates.paper.tasks.menus;

import com.badbones69.crazycrates.paper.api.builders.gui.DynamicInventoryBuilder;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.Tier;
import com.ryderbelserion.fusion.paper.api.builders.gui.interfaces.GuiFiller;
import com.ryderbelserion.fusion.paper.api.builders.gui.interfaces.GuiItem;
import com.ryderbelserion.fusion.paper.api.builders.gui.types.PaginatedGui;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;

public class CratePreviewMenu extends DynamicInventoryBuilder {

    private final Tier tier;

    public CratePreviewMenu(@NotNull final Player player, @NotNull final Crate crate, @NotNull final Tier tier) {
        super(player, crate, crate.getPreviewName(), crate.getPreviewRows());

        this.tier = tier;
    }

    private final Player player = getPlayer();
    private final PaginatedGui gui = getGui();

    @Override
    public void open() {
        final Crate crate = getCrate();

        if (crate == null) return;

        final GuiFiller guiFiller = this.gui.getFiller();

        if (crate.isBorderToggle()) {
            final GuiItem guiItem = new GuiItem(crate.getBorderItem().asItemStack());

            guiFiller.fillTop(guiItem);
            guiFiller.fillBottom(guiItem);
        } else {
            guiFiller.fillBottom(new GuiItem(ItemType.AIR.createItemStack()));
        }

        final UUID uuid = this.player.getUniqueId();

        crate.getPreviewItems(this.player, this.tier).forEach(itemStack -> this.gui.addItem(new GuiItem(itemStack)));

        this.gui.setOpenGuiAction(event -> this.inventoryManager.addPreviewViewer(uuid));

        this.gui.setCloseGuiAction(event -> this.inventoryManager.removePreviewViewer(uuid));

        this.gui.open(this.player, gui -> {
            addBackButton(true);
            addNextButton(true);

            addMenuButton(this.player, crate, this.gui);
        });
    }
}