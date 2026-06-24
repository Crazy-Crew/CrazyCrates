package com.badbones69.crazycrates.paper.tasks.crates.effects;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import com.badbones69.crazycrates.paper.api.objects.crates.CrateLocation;
import com.badbones69.crazycrates.paper.tasks.crates.CrateManager;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.VaultDisplayItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles the Vault block's item display to show crate prizes.
 * When a Vault block is placed at a crate location, it will cycle through
 * displaying the prizes available in that crate.
 */
public class VaultEffect implements Listener {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();
    private final CrateManager crateManager = this.plugin.getCrateManager();
    private final Random random = new Random();

    // Track the current display index for each vault location to enable cycling
    private final Map<String, Integer> displayIndexes = new ConcurrentHashMap<>();

    @EventHandler
    public void onVaultDisplayItem(@NotNull final VaultDisplayItemEvent event) {
        final Location location = event.getBlock().getLocation();
        final CrateLocation crateLocation = this.crateManager.getCrateLocation(location);

        // If this vault is not at a crate location, let vanilla handle it
        if (crateLocation == null) return;

        final Crate crate = crateLocation.getCrate();
        final List<Prize> prizes = crate.getPrizes();

        // If the crate has no prizes, let vanilla handle it
        if (prizes.isEmpty()) return;

        // Get or initialize the display index for this location
        final String locationKey = getLocationKey(location);
        final int currentIndex = this.displayIndexes.getOrDefault(locationKey, 0);

        // Get the prize at the current index (cycling through)
        final Prize prize = prizes.get(currentIndex % prizes.size());

        // Get the display item for this prize
        final ItemStack displayItem = prize.getDisplayItem(crate);

        // Set the display item on the vault
        event.setDisplayItem(displayItem);

        // Increment the index for next time (cycle through prizes)
        this.displayIndexes.put(locationKey, (currentIndex + 1) % prizes.size());
    }

    /**
     * Creates a unique key for a location to track display cycling.
     *
     * @param location the location to create a key for
     * @return a string key representing the location
     */
    private String getLocationKey(@NotNull final Location location) {
        return location.getWorld().getName() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ();
    }

    /**
     * Clears the display index tracking for a specific location.
     * Call this when a crate location is removed.
     *
     * @param location the location to clear
     */
    public void clearLocation(@NotNull final Location location) {
        this.displayIndexes.remove(getLocationKey(location));
    }

    /**
     * Clears all tracked display indexes.
     * Call this on plugin disable or reload.
     */
    public void clearAll() {
        this.displayIndexes.clear();
    }
}

