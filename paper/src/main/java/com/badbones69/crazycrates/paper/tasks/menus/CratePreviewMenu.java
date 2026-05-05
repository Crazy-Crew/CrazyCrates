package com.badbones69.crazycrates.paper.tasks.menus;

import com.badbones69.crazycrates.paper.api.builders.gui.DynamicInventoryBuilder;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.Tier;
import com.ryderbelserion.fusion.paper.builders.gui.objects.GuiItem;
import com.ryderbelserion.fusion.paper.builders.gui.objects.border.GuiFiller;
import com.ryderbelserion.fusion.paper.builders.gui.types.paginated.PaginatedGui;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.UUID;

public class CratePreviewMenu extends DynamicInventoryBuilder {

    private final Tier tier;

    public CratePreviewMenu(@NotNull final Player player, @NotNull final Crate crate, @Nullable final Tier tier) {
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
            guiFiller.fillBoth(crate.getBorderItem().asItemStack());
        } else {
            guiFiller.fillBottom(ItemType.AIR.createItemStack());
        }

        final UUID uuid = this.player.getUniqueId();

        crate.getPreviewItems(this.player, this.tier).forEach(itemStack -> this.gui.addPageItem(new GuiItem(itemStack)));

        this.gui.setOpenAction(_ -> this.inventoryManager.addPreviewViewer(uuid));

        this.gui.setCloseAction(_ -> this.inventoryManager.removePreviewViewer(uuid));

        this.gui.open(this.player, gui -> {
            addBackButton(true);
            addNextButton(true);

            addMenuButton(this.player, crate, this.gui);
        });
    }
}