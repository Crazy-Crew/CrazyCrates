package us.crazycrew.crazycrates.paper.crates.object;

import org.bukkit.inventory.ItemStack;

public class Prize {

    private final ItemStack display;

    public Prize(ItemStack display) {
        this.display = display;
    }

    public ItemStack getDisplay() {
        return this.display;
    }
}