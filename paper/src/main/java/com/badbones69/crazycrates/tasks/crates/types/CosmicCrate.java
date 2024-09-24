package com.badbones69.crazycrates.tasks.crates.types;

import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.managers.events.enums.EventType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.api.builders.CrateBuilder;

public class CosmicCrate extends CrateBuilder {

    public CosmicCrate(@NotNull final Crate crate, @NotNull final Player player, final int size) {
        super(crate, player, size, crate.getCrateName() + " - Choose");
    }

    @Override
    public void open(@NotNull final KeyType type, final boolean checkHand, final EventType eventType) {
        // If the crate event failed.
        if (isCrateEventValid(type, checkHand, eventType)) {
            return;
        }

        final Player player = getPlayer();

        populateTiers();

        this.crateManager.addPlayerKeyType(player, type);
        this.crateManager.addHands(player, checkHand);

        player.openInventory(getInventory());
    }
}