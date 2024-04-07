package com.badbones69.crazycrates.tasks.crates.other.quadcrates;

import org.bukkit.SoundCategory;
import us.crazycrew.crazycrates.platform.config.ConfigManager;
import us.crazycrew.crazycrates.platform.config.impl.ConfigKeys;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.SpiralManager;
import com.badbones69.crazycrates.support.StructureHandler;
import com.badbones69.crazycrates.api.ChestManager;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuadCrateManager {

    @NotNull
    private final CrazyCrates plugin = CrazyCrates.get();

    @NotNull
    private final CrateManager crateManager = this.plugin.getCrateManager();

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
    private final HashMap<Location, Boolean> cratesOpened = new HashMap<>();

    // Saves all the chests spawned by the QuadCrate task.
    private final HashMap<Location, BlockState> quadCrateChests = new HashMap<>();

    // Saves all the old blocks to restore after.
    private final HashMap<Location, BlockState> oldBlocks = new HashMap<>();

    // Get the particles that will be used to display above the crates.
    private final Color particleColor;
    private final Particle particle;

    // Get the structure handler.
    private final StructureHandler handler;

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
    public QuadCrateManager(Player player, Crate crate, KeyType keyType, Location spawnLocation, boolean inHand, StructureHandler handler) {
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
            this.player.sendMessage(Messages.not_on_block.getMessage(player));
            this.crateManager.removePlayerFromOpeningList(player);
            crateSessions.remove(this.instance);
            return;
        }

        // Check if schematic folder is empty.
        if (this.plugin.getCrateManager().getCrateSchematics().isEmpty()) {
            this.player.sendMessage(Messages.no_schematics_found.getMessage(player));
            this.crateManager.removePlayerFromOpeningList(this.player);
            crateSessions.remove(this.instance);
            return;
        }

        // Check if the blocks are able to be changed.
        List<Location> structureLocations;

        structureLocations = this.handler.getBlocks(this.spawnLocation.clone());

        // Loop through the blocks and check if the blacklist contains the block type.
        // Do not open the crate if the block is not able to be changed.
        assert structureLocations != null;

        for (Location loc : structureLocations) {
            if (this.handler.getBlockBlackList().contains(loc.getBlock().getType())) {
                this.player.sendMessage(Messages.needs_more_room.getMessage(player));
                this.crateManager.removePlayerFromOpeningList(this.player);

                crateSessions.remove(this.instance);
                return;
            } else {
                if (!loc.getBlock().getType().equals(Material.AIR)) this.oldBlocks.put(loc.getBlock().getLocation(), loc.getBlock().getState());
            }
        }

        List<Entity> shovePlayers = new ArrayList<>();

        for (Entity entity : player.getNearbyEntities(3, 3, 3)) {
            if (entity instanceof Player) {
                for (QuadCrateManager ongoingCrate : crateSessions) {
                    if (entity.getUniqueId() == ongoingCrate.player.getUniqueId()) {
                        this.player.sendMessage(Messages.too_close_to_another_player.getMessage("{player}", entity.getName(), player));
                        this.crateManager.removePlayerFromOpeningList(this.player);

                        crateSessions.remove(this.instance);
                        return;
                    }
                }

                shovePlayers.add(entity);
            }
        }

        if (!this.plugin.getUserManager().takeKeys(1, this.player.getUniqueId(), this.crate.getName(), this.keyType, this.checkHand)) {
            this.crateManager.removePlayerFromOpeningList(this.player);

            crateSessions.remove(this.instance);
            return;
        }

        if (this.crateManager.getHolograms() != null) this.crateManager.getHolograms().removeHologram(this.spawnLocation);

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
        for (Location loc : this.crateLocations) {
            if (this.crateLocations.contains(loc)) this.quadCrateChests.put(loc.clone(), loc.getBlock().getState());
        }

        // Paste the structure in.
        this.handler.pasteStructure(this.spawnLocation.clone());

        this.player.teleport(this.spawnLocation.toCenterLocation().add(0, 1.0, 0));

        this.crateManager.addQuadCrateTask(this.player, new BukkitRunnable() {

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
                    crate.playSound(player, player.getLocation(), "cycle-sound", "BLOCK_STONE_STEP", SoundCategory.PLAYERS);

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
        }.runTaskTimer(this.plugin, 0,1));

        this.crateManager.addCrateTask(this.player, new BukkitRunnable() {
            @Override
            public void run() {
                endCrate(true);
                player.sendMessage(Messages.out_of_time.getMessage("{crate}", crate.getName(), player));
                crate.playSound(player, player.getLocation(), "stop-sound", "ENTITY_PLAYER_LEVELUP", SoundCategory.PLAYERS);
            }
        }.runTaskLater(this.plugin, ConfigManager.getConfig().getProperty(ConfigKeys.quad_crate_timer) * 20));
    }

    /**
     * End the crate gracefully.
     */
    public void endCrate(boolean immediately) {
        new BukkitRunnable() {
            @Override
            public void run() {
                // Update spawned crate block states which removes them.
                crateLocations.forEach(location -> quadCrateChests.get(location).update(true, false));

                // Remove displayed rewards.
                displayedRewards.forEach(Entity::remove);

                // Teleport player to last location.
                player.teleport(lastLocation);

                // Remove the structure blocks.
                handler.removeStructure();

                // Restore the old blocks.
                oldBlocks.keySet().forEach(location -> oldBlocks.get(location).update(true, false));

                if (crate.getHologram().isEnabled() && crateManager.getHolograms() != null) crateManager.getHolograms().createHologram(spawnLocation, crate);

                // End the crate.
                crateManager.endCrate(player);

                // Remove the player from the list saying they are opening a crate.
                crateManager.removePlayerFromOpeningList(player);

                // Remove the "instance" from the crate sessions.
                crateSessions.remove(instance);
            }
        }.runTaskLater(this.plugin, immediately ? 0 : 5);
    }

    /**
     * Add a crate location.
     *
     * @param x x coordinate.
     * @param y y coordinate.
     * @param z z coordinate.
     */
    public void addCrateLocations(int x, int y, int z) {
        this.crateLocations.add(this.spawnLocation.clone().add(x, y, z));
    }

    /**
     * Spawn particles at 2 specific locations with a customizable color.
     *
     * @param particleColor the color of the particle.
     * @param location1 the first location of the particle.
     * @param location2 the second location of the particle.
     */
    private void spawnParticles(Color particleColor, Location location1, Location location2) {
        if (this.particle == Particle.REDSTONE) {
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
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Get a list of crate locations.
     *
     * @return list of crate locations.
     */
    public List<Location> getCrateLocations() {
        return this.crateLocations;
    }

    /**
     * Get the hashmap of opened crates.
     *
     * @return map of opened crates.
     */
    public HashMap<Location, Boolean> getCratesOpened() {
        return this.cratesOpened;
    }

    /**
     * Get the crate object.
     *
     * @return the crate object.
     */
    public Crate getCrate() {
        return this.crate;
    }

    /**
     * Get the list of display rewards.
     *
     * @return list of display rewards.
     */
    public List<Entity> getDisplayedRewards() {
        return this.displayedRewards;
    }

    /**
     * Check if all crates have opened.
     *
     * @return true or false.
     */
    public boolean allCratesOpened() {
        for (Map.Entry<Location, Boolean> location : this.cratesOpened.entrySet()) {
            if (!location.getValue()) return false;
        }

        return true;
    }
}