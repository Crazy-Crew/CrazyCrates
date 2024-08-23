package com.badbones69.crazycrates.tasks.crates.other.quadcrates;

import com.badbones69.crazycrates.api.objects.other.CrateLocation;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.badbones69.crazycrates.support.holograms.HologramManager;
import com.ryderbelserion.vital.paper.util.scheduler.FoliaRunnable;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import com.ryderbelserion.vital.paper.util.structures.StructureManager;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.plugin.java.JavaPlugin;
import com.badbones69.crazycrates.config.ConfigManager;
import com.badbones69.crazycrates.config.impl.ConfigKeys;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.SpiralManager;
import com.badbones69.crazycrates.api.ChestManager;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

public class QuadCrateManager {

    private @NotNull final CrazyCrates plugin = CrazyCrates.getPlugin();

    private @NotNull final CrateManager crateManager = this.plugin.getCrateManager();
    private @NotNull final BukkitUserManager userManager = this.plugin.getUserManager();

    private static final List<QuadCrateManager> crateSessions = new ArrayList<>();

    private final QuadCrateManager instance;

    // Get the player.
    private final Player player;

    // Check player hand.
    private final boolean checkHand;

    // The crate that is being used.
    private final Crate crate;

    // The key type.
    private final KeyType keyType;

    // Get display rewards.
    private final List<Entity> displayedRewards = new ArrayList<>();

    /**
     * The spawn location.
     * Used to define where the structure will load.
     * Also used to get the center of the structure to teleport the player to.
     */
    private final Location spawnLocation;

    // The last location the player was originally at.
    private final Location lastLocation;

    // Defines the locations of the Chests that will spawn in.
    private final List<Location> crateLocations = new ArrayList<>();

    // Stores if the crate is open or not.
    private final Map<Location, Boolean> cratesOpened = new HashMap<>();

    // Saves all the chests spawned by the QuadCrate task.
    private final Map<Location, BlockState> quadCrateChests = new HashMap<>();

    // Saves all the old blocks to restore after.
    private final Map<Location, BlockState> oldBlocks = new HashMap<>();

    // Get the particles that will be used to display above the crates.
    private final Color particleColor;
    private final Particle particle;

    // Get the structure handler.
    private final StructureManager handler;

    /**
     * A constructor to build the quad crate session.
     *
     * @param player player opening the crate.
     * @param crate crate the player is opening.
     * @param keyType key type the player has.
     * @param spawnLocation spawn location of the schematic.
     * @param inHand checks the hand of the player.
     * @param handler the structure handler instance.
     */
    public QuadCrateManager(@NotNull final Player player, @NotNull final Crate crate, @NotNull final KeyType keyType, @NotNull final Location spawnLocation, final boolean inHand, @NotNull final StructureManager handler) {
        this.instance = this;
        this.player = player;
        this.crate = crate;
        this.keyType = keyType;
        this.checkHand = inHand;

        this.spawnLocation = spawnLocation;

        this.lastLocation = player.getLocation();
        this.lastLocation.setPitch(0F);

        this.handler = handler;

        this.particle = crate.getParticle();
        this.particleColor = crate.getColor();

        crateSessions.add(this.instance);
    }

    /**
     * Start the crate session.
     */
    public void startCrate() {
        // Check if it is on a block.
        if (this.spawnLocation.clone().subtract(0, 1, 0).getBlock().getType() == Material.AIR) {
            Messages.not_on_block.sendMessage(player);

            this.crateManager.removePlayerFromOpeningList(player);

            crateSessions.remove(this.instance);

            return;
        }

        // Check if schematic folder is empty.
        if (this.crateManager.getCrateSchematics().isEmpty()) {
            Messages.no_schematics_found.sendMessage(player);

            this.crateManager.removePlayerFromOpeningList(this.player);

            crateSessions.remove(this.instance);

            return;
        }

        // Check if the blocks are able to be changed.
        final Set<Location> structureLocations = this.handler.getBlocks(this.spawnLocation.clone());

        // Loop through the blocks and check if the blacklist contains the block type.
        // Do not open the crate if the block is not able to be changed.
        for (Location loc : structureLocations) {
            if (this.handler.getBlockBlacklist().contains(loc.getBlock().getType())) {
                Messages.needs_more_room.sendMessage(player);

                this.crateManager.removePlayerFromOpeningList(this.player);

                crateSessions.remove(this.instance);

                return;
            } else {
                if (!loc.getBlock().getType().equals(Material.AIR)) this.oldBlocks.put(loc.getBlock().getLocation(), loc.getBlock().getState());
            }
        }

        final List<Entity> shovePlayers = new ArrayList<>();

        for (final Entity entity : player.getNearbyEntities(3, 3, 3)) {
            if (entity instanceof Player entityPlayer) {
                for (QuadCrateManager ongoingCrate : crateSessions) {
                    if (entityPlayer.getUniqueId() == ongoingCrate.player.getUniqueId()) {
                        Messages.too_close_to_another_player.sendMessage(player, "{player}", entityPlayer.getName());

                        this.crateManager.removePlayerFromOpeningList(this.player);

                        crateSessions.remove(this.instance);

                        return;
                    }
                }

                shovePlayers.add(entity);
            }
        }

        if (!this.userManager.takeKeys(this.player.getUniqueId(), this.crate.getFileName(), this.keyType, this.crate.useRequiredKeys() ? this.crate.getRequiredKeys() : 1, this.checkHand)) {
            this.crateManager.removePlayerFromOpeningList(this.player);

            crateSessions.remove(this.instance);

            return;
        }

        final HologramManager manager = this.crateManager.getHolograms();

        if (manager != null && this.crate.getHologram().isEnabled()) {
            CrateLocation crateLocation = this.crateManager.getCrateLocation(this.spawnLocation);

            if (crateLocation != null) {
                manager.removeHologram(crateLocation.getID());
            }
        }

        // Shove other players away from the player opening the crate.
        shovePlayers.forEach(entity -> entity.getLocation().toVector().subtract(this.spawnLocation.clone().toVector()).normalize().setY(1));

        // Store the spawned Crates ( Chest Block ) in the ArrayList.
        addCrateLocations(2, 1, 0);
        addCrateLocations(0, 1, 2);

        addCrateLocations(-2, 1, 0);
        addCrateLocations(0, 1, -2);

        // Throws unopened crates in a HashMap.
        this.crateLocations.forEach(loc -> this.cratesOpened.put(loc, false));

        // This holds the quad crate's spawned chests.
        for (final Location loc : this.crateLocations) {
            if (this.crateLocations.contains(loc)) this.quadCrateChests.put(loc.clone(), loc.getBlock().getState());
        }

        // Paste the structure in.
        this.handler.pasteStructure(this.spawnLocation, true);

        this.player.teleportAsync(this.spawnLocation.clone().toCenterLocation().add(0, 1.0, 0));

        this.crateManager.addQuadCrateTask(this.player, new FoliaRunnable(this.player.getScheduler(), null) {
            double radius = 0.0; // Radius of the particle spiral.
            int crateNumber = 0; // The crate number that spawns next.
            int tickTillSpawn = 0; // At tick 60 the crate will spawn and then reset the tick.
            Location particleLocation = crateLocations.get(this.crateNumber).clone().add(.5, 3, .5);
            List<Location> spiralLocationsClockwise = SpiralManager.getSpiralLocationClockwise(particleLocation);
            List<Location> spiralLocationsCounterClockwise = SpiralManager.getSpiralLocationCounterClockwise(particleLocation);

            @Override
            public void run() {
                if (this.tickTillSpawn < 60) {
                    spawnParticles(particleColor, this.spiralLocationsClockwise.get(this.tickTillSpawn), this.spiralLocationsCounterClockwise.get(this.tickTillSpawn));

                    this.tickTillSpawn++;
                } else {
                    crate.playSound(player, player.getLocation(), "cycle-sound", "block.stone.step", Sound.Source.PLAYER);

                    Block chest = crateLocations.get(crateNumber).getBlock();

                    chest.setType(Material.CHEST);

                    ChestManager.rotateChest(chest, crateNumber);

                    if (this.crateNumber == 3) { // Last crate has spawned.
                        crateManager.endQuadCrate(player); // Cancelled when method is called.
                    } else {
                        this.tickTillSpawn = 0;
                        this.crateNumber++;
                        this.radius = 0;
                        this.particleLocation = crateLocations.get(this.crateNumber).clone().add(.5, 3, .5); // Set the new particle location for the new crate
                        this.spiralLocationsClockwise = SpiralManager.getSpiralLocationClockwise(this.particleLocation);
                        this.spiralLocationsCounterClockwise = SpiralManager.getSpiralLocationCounterClockwise(this.particleLocation);
                    }
                }
            }
        }.runAtFixedRate(this.plugin, 0,1));

        this.crateManager.addCrateTask(this.player, new FoliaRunnable(getPlayer().getScheduler(), null) {
            @Override
            public void run() {
                endCrate(true);

                Messages.out_of_time.sendMessage(player, "{crate}", crate.getCrateName());

                crate.playSound(player, player.getLocation(), "stop-sound", "entity.player.levelup", Sound.Source.PLAYER);
            }
        }.runDelayed(this.plugin, ConfigManager.getConfig().getProperty(ConfigKeys.quad_crate_timer) * 20));
    }

    /**
     * End the crate gracefully.
     */
    public void endCrate(final boolean immediately) {
        new FoliaRunnable(this.plugin.getServer().getGlobalRegionScheduler()) {
            @Override
            public void run() {
                // Update spawned crate block states which removes them.
                crateLocations.forEach(location -> plugin.getServer().getRegionScheduler().run(plugin, location, schedulerTask -> quadCrateChests.get(location).update(true, false)));

                // Remove displayed rewards.
                for (Entity displayedReward : displayedRewards) {
                    displayedReward.getScheduler().run(plugin, scheduledTask -> displayedReward.remove(), null);
                }

                // Teleport player to last location.
                player.teleportAsync(lastLocation);

                // Remove the structure blocks.
                handler.removeStructure();

                // Restore the old blocks.
                oldBlocks.keySet().forEach(location -> plugin.getServer().getRegionScheduler().run(plugin, location, schedulerTask -> oldBlocks.get(location).update(true, false)));

                final HologramManager manager = crateManager.getHolograms();

                if (manager != null && crate.getHologram().isEnabled()) {
                    CrateLocation crateLocation = crateManager.getCrateLocation(spawnLocation);

                    if (crateLocation != null) {
                        manager.createHologram(spawnLocation, crate, crateLocation.getID());
                    }
                }

                // End the crate.
                crateManager.endCrate(player);

                // Remove the player from the list saying they are opening a crate.
                crateManager.removePlayerFromOpeningList(player);

                // Remove the "instance" from the crate sessions.
                crateSessions.remove(instance);
            }
        }.runDelayed(this.plugin, immediately ? 0 : 5);
    }

    /**
     * End the crate by force which cleans everything up.
     *
     * @param removeForce whether to stop the crate session or not.
     */
    public void endCrateForce(final boolean removeForce) {
        this.oldBlocks.keySet().forEach(location -> this.oldBlocks.get(location).update(true, false));
        this.crateLocations.forEach(location -> this.quadCrateChests.get(location).update(true, false));
        this.displayedRewards.forEach(Entity::remove);
        this.player.teleportAsync(this.lastLocation);

        if (removeForce) {
            this.crateManager.removePlayerFromOpeningList(this.player);

            crateSessions.remove(this.instance);
        }

        this.handler.removeStructure();
    }

    /**
     * Add a crate location.
     *
     * @param x x coordinate.
     * @param y y coordinate.
     * @param z z coordinate.
     */
    public void addCrateLocations(final int x, final int y, final int z) {
        this.crateLocations.add(this.spawnLocation.clone().add(x, y, z));
    }

    /**
     * Spawn particles at 2 specific locations with a customizable color.
     *
     * @param particleColor the color of the particle.
     * @param location1 the first location of the particle.
     * @param location2 the second location of the particle.
     */
    private void spawnParticles(@NotNull final Color particleColor, @NotNull final Location location1, @NotNull final Location location2) {
        if (this.particle == Particle.DUST) {
            location1.getWorld().spawnParticle(this.particle, location1, 0, new Particle.DustOptions(particleColor, 1));
            location2.getWorld().spawnParticle(this.particle, location2, 0, new Particle.DustOptions(particleColor, 1));

            return;
        }

        location1.getWorld().spawnParticle(this.particle, location1, 0);
        location2.getWorld().spawnParticle(this.particle, location2, 0);
    }

    /**
     * Get the crate sessions
     *
     * @return list of crate sessions.
     */
    public static List<QuadCrateManager> getCrateSessions() {
        return crateSessions;
    }

    /**
     * Get the player opening the crate
     *
     * @return the player.
     */
    public @NotNull final Player getPlayer() {
        return this.player;
    }

    /**
     * Get a list of crate locations.
     *
     * @return list of crate locations.
     */
    public @NotNull final List<Location> getCrateLocations() {
        return this.crateLocations;
    }

    /**
     * Get the hashmap of opened crates.
     *
     * @return map of opened crates.
     */
    public @NotNull final Map<Location, Boolean> getCratesOpened() {
        return this.cratesOpened;
    }

    /**
     * Get the crate object.
     *
     * @return the crate object.
     */
    public @NotNull final Crate getCrate() {
        return this.crate;
    }

    /**
     * Get the list of display rewards.
     *
     * @return list of display rewards.
     */
    public @NotNull final List<Entity> getDisplayedRewards() {
        return this.displayedRewards;
    }

    /**
     * Check if all crates have opened.
     *
     * @return true or false.
     */
    public final boolean allCratesOpened() {
        for (Map.Entry<Location, Boolean> location : this.cratesOpened.entrySet()) {
            if (!location.getValue()) return false;
        }

        return true;
    }
}