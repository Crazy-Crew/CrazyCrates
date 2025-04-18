package com.badbones69.crazycrates.paper.tasks.crates.types;

import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.managers.events.enums.EventType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.paper.api.builders.CrateBuilder;

public class CosmicCrate extends CrateBuilder {

    public CosmicCrate(@NotNull final Crate crate, @NotNull final Player player, final int size) {
        super(crate, player, size, crate.getCrateName() + " - Choose");
    }

    @Override
    public void open(@NotNull final KeyType type, final boolean checkHand, final boolean isSilent, @NotNull final EventType eventType) {
        // Crate event failed, so we return.
        if (isCrateEventValid(type, checkHand, isSilent, eventType)) {
            return;
        }

        final Player player = getPlayer();

        populateTiers();

        this.crateManager.addPlayerKeyType(player, type);
        this.crateManager.addHands(player, checkHand);

        player.openInventory(getInventory());
    }
}