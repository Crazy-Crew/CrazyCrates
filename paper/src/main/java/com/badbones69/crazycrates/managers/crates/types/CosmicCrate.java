package com.badbones69.crazycrates.managers.crates.types;

import com.badbones69.crazycrates.managers.crates.other.CosmicCrateManager;
import com.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.api.builders.CrateBuilder;

public class CosmicCrate extends CrateBuilder {

    public CosmicCrate(Crate crate, Player player, int size) {
        super(crate, player, size, crate.getFile().getString("Crate.CrateName") + " - Choose");
    }

    @Override
    public void open(KeyType type, boolean checkHand) {
        // If the crate event failed.
        if (isCrateEventValid(type, checkHand)) {
            return;
        }

        CosmicCrateManager manager = (CosmicCrateManager) getCrate().getManager();
        int slot = 1;

        for (int index = 0; index < getSize(); index++) {
            ItemStack stack = manager.getMysteryCrate().setAmount(slot).addNamePlaceholder("%Slot%", String.valueOf(slot)).addLorePlaceholder("%Slot%", String.valueOf(slot)).build();

            setItem(index, stack);
            slot++;
        }

        this.plugin.getCrateManager().addPlayerKeyType(getPlayer(), type);
        this.plugin.getCrateManager().addHands(getPlayer(), checkHand);

        getPlayer().openInventory(getInventory());
    }
}