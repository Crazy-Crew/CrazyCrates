package com.badbones69.crazycrates.api.builders.types;

import com.badbones69.crazycrates.api.builders.gui.StaticInventoryBuilder;
import com.badbones69.crazycrates.api.enums.misc.Keys;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Tier;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.Gui;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.GuiFiller;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.GuiItem;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import java.util.List;

public class CrateTierMenu extends StaticInventoryBuilder {

    public CrateTierMenu(final Player player, final Crate crate) {
        super(player, crate);
    }

    private final Player player = getPlayer();
    private final Crate crate = getCrate();
    private final Gui gui = getGui();

    @Override
    public void open() {
        if (this.crate == null) return;

        final CrateType crateType = this.crate.getCrateType();

        if (crateType != CrateType.casino && crateType != CrateType.cosmic) return;

        final boolean isPreviewBorderEnabled = this.crate.isPreviewTierBorderToggle();

        if (isPreviewBorderEnabled) {
            final GuiItem guiItem = this.crate.getPreviewTierBorderItem().setPlayer(this.player).asGuiItem();

            final GuiFiller guiFiller = this.gui.getFiller();

            guiFiller.fillTop(guiItem);
            guiFiller.fillBottom(guiItem);
        }

        final List<Tier> tiers = this.crate.getTiers();

        tiers.forEach(tier -> {
            final ItemStack item = tier.getTierItem(this.player);
            final int slot = tier.getSlot();

            this.gui.setItem(slot, new GuiItem(item, action -> {
                final ItemStack itemStack = action.getCurrentItem();

                if (itemStack == null || itemStack.getType().isAir()) return;

                final PersistentDataContainerView tags = itemStack.getPersistentDataContainer();

                if (tags.has(Keys.crate_tier.getNamespacedKey())) {
                    this.crate.playSound(this.player, this.player.getLocation(), "click-sound", "ui.button.click", Sound.Source.PLAYER);

                    this.crate.getPreview(this.player, tier).open();
                }
            }));
        });

        addMenuButton(this.player, this.crate, this.gui, 5, 5);

        this.gui.setOpenGuiAction(event -> this.inventoryManager.addPreviewViewer(event.getPlayer().getUniqueId()));

        this.gui.setCloseGuiAction(event -> this.inventoryManager.removePreviewViewer(event.getPlayer().getUniqueId()));

        this.gui.open(this.player);
    }
}