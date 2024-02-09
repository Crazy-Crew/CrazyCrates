package com.badbones69.crazycrates.api.builders.types;

import com.badbones69.crazycrates.CrazyHandler;
import com.badbones69.crazycrates.api.builders.InventoryBuilder;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Tier;
import com.badbones69.crazycrates.common.config.types.ConfigKeys;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.List;

public class CrateTierMenu extends InventoryBuilder {

    @NotNull
    private final CrazyHandler crazyHandler = this.plugin.getCrazyHandler();

    public CrateTierMenu(List<Tier> tiers, Crate crate, Player player, int size, String title) {
        super(tiers, crate, player, size, title);
    }

    @Override
    public InventoryBuilder build() {
        setDefaultItems();

        return this;
    }

    private void setDefaultItems() {
        getTiers().forEach(tier -> getInventory().setItem(tier.getSlot(), tier.getTierItem()));

        if (getCrate().isPreviewTierBorderToggle()) {
            List<Integer> borderItems = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8);

            for (int item : borderItems) { // Top border slots
                getInventory().setItem(item, getCrate().getPreviewTierBorderItem().build());
            }

            borderItems.replaceAll(getCrate()::getAbsolutePreviewItemPosition);

            for (int item : borderItems) { // Bottom border slots
                getInventory().setItem(item, getCrate().getPreviewTierBorderItem().build());
            }
        }

        if (this.crazyHandler.getInventoryManager().inCratePreview(getPlayer()) && this.crazyHandler.getConfigManager().getConfig().getProperty(ConfigKeys.enable_crate_menu)) getInventory().setItem(getCrate().getAbsolutePreviewItemPosition(4), this.crazyHandler.getInventoryManager().getMenuButton());
    }
}