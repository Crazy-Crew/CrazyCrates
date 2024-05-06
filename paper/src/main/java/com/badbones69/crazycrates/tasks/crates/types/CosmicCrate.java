package com.badbones69.crazycrates.tasks.crates.types;

import com.badbones69.crazycrates.api.PrizeManager;
import com.badbones69.crazycrates.api.enums.PersistentKeys;
import com.badbones69.crazycrates.api.objects.Tier;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.badbones69.crazycrates.tasks.crates.other.CosmicCrateManager;
import com.badbones69.crazycrates.api.objects.Crate;
import com.ryderbelserion.vital.items.ItemBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.api.builders.CrateBuilder;

public class CosmicCrate extends CrateBuilder {

    private @NotNull final CrateManager crateManager = this.plugin.getCrateManager();

    public CosmicCrate(@NotNull final Crate crate, @NotNull final Player player, final int size) {
        super(crate, player, size, crate.getCrateInventoryName() + " - Choose");
    }

    @Override
    public void open(@NotNull final KeyType type, final boolean checkHand) {
        // If the crate event failed.
        if (isCrateEventValid(type, checkHand)) {
            return;
        }

        final Player player = getPlayer();
        final Crate crate = getCrate();

        final CosmicCrateManager manager = (CosmicCrateManager) crate.getManager();
        int slot = 1;

        for (int index = 0; index < getSize(); index++) {
            final ItemBuilder stack = manager.getMysteryCrate().setPlayer(player).addPlaceholder("%Slot%", String.valueOf(slot), false).addPlaceholder("%Slot%", String.valueOf(slot), true);

            stack.setAmount(slot);

            final Tier tier = PrizeManager.getTier(crate);

            if (tier != null) {
                stack.setPersistentString(PersistentKeys.crate_tier.getNamespacedKey(), tier.getName());

                this.plugin.getLogger().warning("Debug: " + stack.hasKey(PersistentKeys.crate_tier.getNamespacedKey()));

                setItem(index, stack.build());

                slot++;
            }
        }

        this.crateManager.addPlayerKeyType(player, type);
        this.crateManager.addHands(player, checkHand);

        player.openInventory(getInventory());
    }

    @Override
    public void run() {

    }
}