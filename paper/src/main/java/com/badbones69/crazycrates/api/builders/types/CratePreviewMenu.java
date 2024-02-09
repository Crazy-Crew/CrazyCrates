package com.badbones69.crazycrates.api.builders.types;

import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Tier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.common.config.types.ConfigKeys;
import com.badbones69.crazycrates.CrazyHandler;
import com.badbones69.crazycrates.api.builders.InventoryBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CratePreviewMenu extends InventoryBuilder {

    @NotNull
    private final CrazyHandler crazyHandler = this.plugin.getCrazyHandler();

    private final boolean isTier;
    private final Tier tier;

    public CratePreviewMenu(Crate crate, Player player, int size, int page, String title, boolean isTier, Tier tier) {
        super(crate, player, size, page, title);

        this.isTier = isTier;
        this.tier = tier;
    }

    @Override
    public InventoryBuilder build() {
        Inventory inventory = getInventory();

        setDefaultItems(inventory);

        for (ItemStack item : getPageItems(getPage())) {
            int nextSlot = inventory.firstEmpty();

            if (nextSlot >= 0) {
                inventory.setItem(nextSlot, item);
            } else {
                break;
            }
        }

        return this;
    }

    private void setDefaultItems(Inventory inventory) {
        if (getCrate().isBorderToggle()) {
            List<Integer> borderItems = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8);

            for (int i : borderItems) { // Top Border slots
                inventory.setItem(i, getCrate().getBorderItem().build());
            }

            borderItems.replaceAll(getCrate()::getAbsoluteItemPosition);

            for (int i : borderItems) { // Bottom Border slots
                inventory.setItem(i, getCrate().getBorderItem().build());
            }
        }

        int page = this.crazyHandler.getInventoryManager().getPage(getPlayer());

        if (this.crazyHandler.getInventoryManager().inCratePreview(getPlayer()) && this.crazyHandler.getConfigManager().getConfig().getProperty(ConfigKeys.enable_crate_menu)) {
            inventory.setItem(getCrate().getAbsoluteItemPosition(4), this.crazyHandler.getInventoryManager().getMenuButton());
        }

        if (page == 1) {
            if (getCrate().isBorderToggle()) {
                inventory.setItem(getCrate().getAbsoluteItemPosition(3), getCrate().getBorderItem().build());
            }
        } else {
            inventory.setItem(getCrate().getAbsoluteItemPosition(3), this.crazyHandler.getInventoryManager().getBackButton(getPlayer()));
        }

        if (page == getCrate().getMaxPage()) {
            if (getCrate().isBorderToggle()) {
                inventory.setItem(getCrate().getAbsoluteItemPosition(5), getCrate().getBorderItem().build());
            }
        } else {
            inventory.setItem(getCrate().getAbsoluteItemPosition(5), this.crazyHandler.getInventoryManager().getNextButton(getPlayer()));
        }
    }

    private List<ItemStack> getPageItems(int page) {
        List<ItemStack> list = !this.isTier ? getCrate().getPreviewItems() : getCrate().getPreviewItems(this.tier);
        List<ItemStack> items = new ArrayList<>();

        if (page <= 0) page = 1;

        int max = getCrate().getMaxSlots() - (getCrate().isBorderToggle() ? 18 : getCrate().getMaxSlots() >= list.size() ? 0 : getCrate().getMaxSlots() != 9 ? 9 : 0);
        int index = page * max - max;
        int endIndex = index >= list.size() ? list.size() - 1 : index + max;

        for (; index < endIndex; index++) {
            if (index < list.size()) items.add(list.get(index));
        }

        for (; items.isEmpty(); page--) {
            if (page <= 0) break;
            index = page * max - max;
            endIndex = index >= list.size() ? list.size() - 1 : index + max;

            for (; index < endIndex; index++) {
                if (index < list.size()) items.add(list.get(index));
            }
        }

        return items;
    }
}