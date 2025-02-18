package com.badbones69.crazycrates.paper.tasks.crates.types;

import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.crates.CrateLocation;
import com.badbones69.crazycrates.paper.managers.events.enums.EventType;
import com.badbones69.crazycrates.paper.support.holograms.HologramManager;
import com.ryderbelserion.fusion.paper.util.scheduler.FoliaScheduler;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.paper.api.builders.CrateBuilder;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class FireCrackerCrate extends CrateBuilder {

    public FireCrackerCrate(@NotNull final Crate crate, @NotNull final Player player, final int size, @NotNull final Location location) {
        super(crate, player, size, location);
    }

    private final Player player = getPlayer();
    private final Location location = getLocation();
    private final UUID uuid = this.player.getUniqueId();
    private final Crate crate = getCrate();

    @Override
    public void open(@NotNull final KeyType type, final boolean checkHand, final boolean isSilent, final EventType eventType) {
        // Crate event failed so we return.
        if (isCrateEventValid(type, checkHand, isSilent, eventType)) {
            return;
        }

        final String fileName = this.crate.getFileName();

        this.crateManager.addCrateInUse(this.player, this.location);

        final boolean keyCheck = this.userManager.takeKeys(this.uuid, fileName, type, this.crate.useRequiredKeys() ? this.crate.getRequiredKeys() : 1, checkHand);

        if (!keyCheck) {
            // Remove from opening list.
            this.crateManager.removePlayerFromOpeningList(this.player);

            return;
        }

        final HologramManager manager = this.crateManager.getHolograms();

        if (manager != null && this.crate.getHologram().isEnabled()) {
            CrateLocation crateLocation = this.crateManager.getCrateLocation(this.location);

            if (crateLocation != null) {
                manager.removeHologram(crateLocation.getID());
            }
        }

        final List<Color> colors = Arrays.asList(Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE, Color.BLACK, Color.AQUA, Color.MAROON, Color.PURPLE);

        addCrateTask(new FoliaScheduler(null, this.player) {
            final int random = ThreadLocalRandom.current().nextInt(colors.size());
            final Location clonedLocation = location.clone().add(0.5, 25, 0.5);

            int length = 0;

            @Override
            public void run() {
                this.clonedLocation.subtract(0, 1, 0);

                MiscUtils.spawnFirework(this.clonedLocation, colors.get(this.random));

                this.length++;

                if (this.length == 25) {
                    crateManager.endCrate(player);

                    QuickCrate quickCrate = new QuickCrate(crate, player, location);

                    quickCrate.open(KeyType.free_key, false, isSilent, eventType);
                }
            }
        }.runAtFixedRate(0, 2));
    }
}