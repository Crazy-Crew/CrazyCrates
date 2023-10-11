package us.crazycrew.crazycrates.paper.crates.object;

import org.bukkit.inventory.ItemStack;

public class Prize {

    private final ItemStack displayItem;

    public Prize(ItemStack displayItem) {
        this.displayItem = displayItem;
    }

    public ItemStack getDisplayItem() {
        return this.displayItem;
    }
}