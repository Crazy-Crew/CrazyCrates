package com.badbones69.crazycrates.paper.api.managers;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.Methods;
import com.badbones69.crazycrates.paper.api.CrazyManager;
import com.badbones69.crazycrates.paper.api.enums.settings.Messages;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.api.quadcrates.CrateParticles;
import com.badbones69.crazycrates.paper.support.structures.QuadCrateSpiralHandler;
import com.badbones69.crazycrates.paper.support.structures.StructureHandler;
import com.badbones69.crazycrates.paper.support.structures.blocks.ChestStateHandler;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class QuadCrateManager {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final ChestStateHandler chestStateHandler = plugin.getStarter().getChestStateHandler();

    private final CrazyManager crazyManager = plugin.getStarter().getCrazyManager();

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
    private final ArrayList<Location> crateLocations = new ArrayList<>();

    // Stores if the crate is open or not.
    private final HashMap<Location, Boolean> cratesOpened = new HashMap<>();

    // Saves all the chests spawned by the QuadCrate task.
    private final HashMap<Location, BlockState> quadCrateChests = new HashMap<>();

    // Saves all the old blocks to restore after.
    private final HashMap<Location, BlockState> oldBlocks = new HashMap<>();

    // Get the particles that will be used to display above the crates.
    private final Color particleColor;
    private final CrateParticles particle;

    // Get the structure handler.
    private final StructureHandler handler;

    /**
     * A constructor to build the quad crate session
     *
     * @param player opening the crate
     * @param crate the player is opening
     * @param keyType the player has
     * @param spawnLocation of the schematic
     * @param lastLocation the player was at
     * @param inHand check the hand of the player
     * @param handler the structure handler instance
     */
    public QuadCrateManager(Player player, Crate crate, KeyType keyType, Location spawnLocation, Location lastLocation, boolean inHand, StructureHandler handler) {
        this.instance = this;
        this.player = player;
        this.crate = crate;
        this.keyType = keyType;
        this.checkHand = inHand;

        this.spawnLocation = spawnLocation;
        this.lastLocation = lastLocation;

        this.handler = handler;

        List<CrateParticles> particles = Arrays.asList(CrateParticles.values());
        this.particle = particles.get(new Random().nextInt(particles.size()));
        this.particleColor = getColors().get(new Random().nextInt(getColors().size()));

        crateSessions.add(instance);
    }

    /**
     * Start the crate session
     */
    public void startCrate() {

        // Check if it is on a block.
        if (spawnLocation.clone().subtract(0, 1, 0).getBlock().getType() == Material.AIR) {
            player.sendMessage(Messages.NOT_ON_BLOCK.getMessage());
            crazyManager.removePlayerFromOpeningList(player);
            crateSessions.remove(instance);
            return;
        }

        // Check if schematic folder is empty.
        if (crazyManager.getCrateSchematics().isEmpty()) {
            player.sendMessage(Messages.NO_SCHEMATICS_FOUND.getMessage());
            crazyManager.removePlayerFromOpeningList(player);
            crateSessions.remove(instance);
            return;
        }

        // Check if the blocks are able to be changed.
        List<Location> structureLocations;

        structureLocations = handler.getBlocks(spawnLocation.clone());

        // Loop through the blocks and check if the blacklist contains the block type.
        // Do not open the crate if the block is not able to be changed.
        assert structureLocations != null;

        for (Location loc : structureLocations) {
            if (handler.getBlockBlackList().contains(loc.getBlock().getType())) {
                player.sendMessage(Messages.NEEDS_MORE_ROOM.getMessage());
                crazyManager.removePlayerFromOpeningList(player);
                crateSessions.remove(instance);
                return;
            } else {
                if (!loc.getBlock().getType().equals(Material.AIR)) oldBlocks.put(loc.getBlock().getLocation(), loc.getBlock().getState());
            }
        }

        List<Entity> shovePlayers = new ArrayList<>();

        for (Entity entity : player.getNearbyEntities(3, 3, 3)) {
            if (entity instanceof Player) {
                for (QuadCrateManager ongoingCrate : crateSessions) {
                    if (entity.getUniqueId() == ongoingCrate.player.getUniqueId()) {
                        player.sendMessage(Messages.TO_CLOSE_TO_ANOTHER_PLAYER.getMessage("%Player%", entity.getName()));
                        crazyManager.removePlayerFromOpeningList(player);
                        crateSessions.remove(instance);
                        return;
                    }
                }

                shovePlayers.add(entity);
            }
        }

        if (!crazyManager.takeKeys(1, player, crate, keyType, checkHand)) {
            Methods.failedToTakeKey(player, crate);

            crazyManager.removePlayerFromOpeningList(player);
            crateSessions.remove(instance);
            return;
        }

        if (crazyManager.getHologramController() != null) crazyManager.getHologramController().removeHologram(spawnLocation.getBlock());

        // Shove other players away from the player opening the crate.
        shovePlayers.forEach(entity -> entity.getLocation().toVector().subtract(spawnLocation.clone().toVector()).normalize().setY(1));

        // Store the spawned Crates ( Chest Block ) in the ArrayList.
        addCrateLocations(2, 1, 0);
        addCrateLocations(0, 1, 2);

        addCrateLocations(-2, 1, 0);
        addCrateLocations(0, 1, -2);

        // Throws unopened crates in a HashMap.
        crateLocations.forEach(loc -> cratesOpened.put(loc, false));

        // This holds the quad crate's spawned chests.
        for (Location loc : crateLocations) {
            if (crateLocations.contains(loc)) quadCrateChests.put(loc.clone(), loc.getBlock().getState());
        }

        // Paste the structure in.
        handler.pasteStructure(spawnLocation.clone());

        player.teleport(spawnLocation.toCenterLocation().add(0, 1.0, 0));

        crazyManager.addQuadCrateTask(player, new BukkitRunnable() {

            private final QuadCrateSpiralHandler spiralHandler = new QuadCrateSpiralHandler();

            double radius = 0.0; // Radius of the particle spiral.
            int crateNumber = 0; // The crate number that spawns next.
            int tickTillSpawn = 0; // At tick 60 the crate will spawn and then reset the tick.
            Location particleLocation = crateLocations.get(crateNumber).clone().add(.5, 3, .5);
            List<Location> spiralLocationsClockwise = spiralHandler.getSpiralLocationClockwise(particleLocation);
            List<Location> spiralLocationsCounterClockwise = spiralHandler.getSpiralLocationCounterClockwise(particleLocation);

            @Override
            public void run() {
                if (tickTillSpawn < 60) {
                    spawnParticles(particle, particleColor, spiralLocationsClockwise.get(tickTillSpawn), spiralLocationsCounterClockwise.get(tickTillSpawn));
                    tickTillSpawn++;
                } else {
                    player.playSound(player.getLocation(), Sound.BLOCK_STONE_STEP, 1, 1);
                    Block chest = crateLocations.get(crateNumber).getBlock();

                    chest.setType(Material.CHEST);
                    chestStateHandler.rotateChest(chest, crateNumber);

                    if (crateNumber == 3) { // Last crate has spawned.
                        crazyManager.endQuadCrate(player); // Cancelled when method is called.
                    } else {
                        tickTillSpawn = 0;
                        crateNumber++;
                        radius = 0;
                        particleLocation = crateLocations.get(crateNumber).clone().add(.5, 3, .5); // Set the new particle location for the new crate
                        spiralLocationsClockwise = spiralHandler.getSpiralLocationClockwise(particleLocation);
                        spiralLocationsCounterClockwise = spiralHandler.getSpiralLocationCounterClockwise(particleLocation);
                    }
                }
            }
        }.runTaskTimer(plugin, 0,1));

        crazyManager.addCrateTask(player, new BukkitRunnable() {
            @Override
            public void run() {
                // End the crate by force.
                endCrateForce(true);
                player.sendMessage(Messages.OUT_OF_TIME.getMessage());
            }
        }.runTaskLater(plugin, crazyManager.getQuadCrateTimer()));
    }

    /**
     * End the crate gracefully.
     */
    public void endCrate() {
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

                if (crate.getHologram().isEnabled() && crazyManager.getHologramController() != null) crazyManager.getHologramController().createHologram(spawnLocation.getBlock(), crate);

                // End the crate.
                crazyManager.endCrate(player);

                // Remove the player from the list saying they are opening a crate.
                crazyManager.removePlayerFromOpeningList(player);

                // Remove the "instance" from the crate sessions.
                crateSessions.remove(instance);
            }
        }.runTaskLater(plugin, 5);
    }

    /**
     * End the crate by force which cleans everything up.
     *
     * @param removeForce whether to stop the crate session or not
     */
    public void endCrateForce(boolean removeForce) {
        oldBlocks.keySet().forEach(location -> oldBlocks.get(location).update(true, false));
        crateLocations.forEach(location -> quadCrateChests.get(location).update(true, false));
        displayedRewards.forEach(Entity::remove);
        player.teleport(lastLocation);

        if (removeForce) {
            crazyManager.removePlayerFromOpeningList(player);
            crateSessions.remove(instance);
        }

        handler.removeStructure();
    }

    /**
     * Add a crate location
     *
     * @param x coordinate
     * @param y coordinate
     * @param z coordinate
     */
    public void addCrateLocations(int x, int y, int z) {
        crateLocations.add(spawnLocation.clone().add(x, y, z));
    }

    /**
     * Get an arraylist of colors
     *
     * @return list of colors
     */
    private List<Color> getColors() {
        return Arrays.asList(
                Color.AQUA, Color.BLACK, Color.BLUE, Color.FUCHSIA, Color.GRAY,
                Color.GREEN, Color.LIME, Color.MAROON, Color.NAVY, Color.OLIVE,
                Color.ORANGE, Color.PURPLE, Color.RED, Color.SILVER, Color.TEAL,
                Color.WHITE, Color.YELLOW);
    }

    /**
     * Spawn particles at 2 specific locations with a customizable color.
     *
     * @param quadCrateParticle the particle to spawn
     * @param particleColor the color of the particle
     * @param location1 the first location of the particle
     * @param location2 the second location of the particle
     */
    private void spawnParticles(CrateParticles quadCrateParticle, Color particleColor, Location location1, Location location2) {
        Particle particle = switch (quadCrateParticle) {
            case FLAME -> Particle.FLAME;
            case VILLAGER_HAPPY -> Particle.VILLAGER_HAPPY;
            case SPELL_WITCH -> Particle.SPELL_WITCH;
            default -> Particle.REDSTONE;
        };

        if (particle == Particle.REDSTONE) {
            location1.getWorld().spawnParticle(particle, location1, 0, new Particle.DustOptions(particleColor, 1));
            location2.getWorld().spawnParticle(particle, location2, 0, new Particle.DustOptions(particleColor, 1));
        } else {
            location1.getWorld().spawnParticle(particle, location1, 0);
            location2.getWorld().spawnParticle(particle, location2, 0);
        }
    }

    /**
     * Get the crate sessions
     *
     * @return list of crate sessions
     */
    public static List<QuadCrateManager> getCrateSessions() {
        return crateSessions;
    }

    /**
     * Get the player opening the crate
     *
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get a list of crate locations.
     *
     * @return list of crate locations
     */
    public List<Location> getCrateLocations() {
        return crateLocations;
    }

    /**
     * Get the hashmap of opened crates.
     *
     * @return map of opened crates
     */
    public HashMap<Location, Boolean> getCratesOpened() {
        return cratesOpened;
    }

    /**
     * Get the crate object.
     *
     * @return the crate object
     */
    public Crate getCrate() {
        return crate;
    }

    /**
     * Get the list of display rewards.
     *
     * @return list of display rewards
     */
    public List<Entity> getDisplayedRewards() {
        return displayedRewards;
    }

    /**
     * Check if all crates have opened.
     *
     * @return true if yes otherwise false
     */
    public Boolean allCratesOpened() {
        for (Map.Entry<Location, Boolean> location : cratesOpened.entrySet()) {
            if (!location.getValue()) return false;
        }

        return true;
    }
}