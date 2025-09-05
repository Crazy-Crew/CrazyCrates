package com.badbones69.crazycrates.paper.tasks.menus;

import com.badbones69.crazycrates.paper.api.builders.gui.StaticInventoryBuilder;
import com.badbones69.crazycrates.paper.api.enums.other.keys.ItemKeys;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.Tier;
import com.ryderbelserion.fusion.paper.api.builders.gui.interfaces.Gui;
import com.ryderbelserion.fusion.paper.api.builders.gui.interfaces.GuiFiller;
import com.ryderbelserion.fusion.paper.api.builders.gui.interfaces.GuiItem;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import java.util.List;
import java.util.UUID;

public class CrateTierMenu extends StaticInventoryBuilder {

    public CrateTierMenu(@NotNull final Player player, @NotNull final Crate crate) {
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

        final UUID uuid = this.player.getUniqueId();

        final List<Tier> tiers = this.crate.getTiers();

        tiers.forEach(tier -> {
            final ItemStack item = tier.getTierItem(this.player, this.crate);
            final int slot = tier.getSlot();

            this.gui.setItem(slot, new GuiItem(item, action -> {
                final ItemStack itemStack = action.getCurrentItem();

                if (itemStack == null || itemStack.getType().isAir()) return;

                final PersistentDataContainerView tags = itemStack.getPersistentDataContainer();

                if (tags.has(ItemKeys.crate_tier.getNamespacedKey())) {
                    this.crate.playSound(this.player, this.player.getLocation(), "click-sound", "ui.button.click", Sound.Source.MASTER);

                    this.crate.getPreview(this.player, tier).open();
                }
            }));
        });

        addMenuButton(this.player, this.crate, this.gui);

        this.gui.setOpenGuiAction(event -> this.inventoryManager.addPreviewViewer(uuid));

        this.gui.setCloseGuiAction(event -> this.inventoryManager.removePreviewViewer(uuid));

        this.gui.open(this.player);
    }
}