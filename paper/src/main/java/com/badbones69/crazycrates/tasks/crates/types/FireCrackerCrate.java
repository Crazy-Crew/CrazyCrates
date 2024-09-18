package com.badbones69.crazycrates.tasks.crates.types;

import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.crates.CrateLocation;
import com.badbones69.crazycrates.support.holograms.HologramManager;
import com.ryderbelserion.vital.paper.util.scheduler.FoliaRunnable;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.api.builders.CrateBuilder;
import com.badbones69.crazycrates.utils.MiscUtils;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class FireCrackerCrate extends CrateBuilder {

    public FireCrackerCrate(@NotNull final Crate crate, @NotNull final Player player, final int size, @NotNull final Location location) {
        super(crate, player, size, location);
    }

    @Override
    public void open(@NotNull final KeyType type, final boolean checkHand) {
        // Crate event failed so we return.
        if (isCrateEventValid(type, checkHand)) {
            return;
        }

        final Player player = getPlayer();
        final UUID uuid = player.getUniqueId();
        final Crate crate = getCrate();
        final String fileName = crate.getFileName();

        this.crateManager.addCrateInUse(player, getLocation());

        final boolean keyCheck = this.userManager.takeKeys(uuid, fileName, type, crate.useRequiredKeys() ? crate.getRequiredKeys() : 1, checkHand);

        if (!keyCheck) {
            // Remove from opening list.
            this.crateManager.removePlayerFromOpeningList(player);

            return;
        }

        final HologramManager manager = this.crateManager.getHolograms();

        if (manager != null && crate.getHologram().isEnabled()) {
            CrateLocation crateLocation = this.crateManager.getCrateLocation(getLocation());

            if (crateLocation != null) {
                manager.removeHologram(crateLocation.getID());
            }
        }

        final List<Color> colors = Arrays.asList(Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE, Color.BLACK, Color.AQUA, Color.MAROON, Color.PURPLE);

        addCrateTask(new FoliaRunnable(player.getScheduler(), null) {
            final int random = ThreadLocalRandom.current().nextInt(colors.size());
            final Location location = getLocation().clone().add(.5, 25, .5);

            int length = 0;

            @Override
            public void run() {
                this.location.subtract(0, 1, 0);

                MiscUtils.spawnFirework(this.location, colors.get(this.random));

                this.length++;

                if (this.length == 25) {
                    crateManager.endCrate(player);

                    QuickCrate quickCrate = new QuickCrate(crate, player, getLocation());

                    quickCrate.open(KeyType.free_key, false);
                }
            }
        }.runAtFixedRate(this.plugin, 0, 2));
    }
}