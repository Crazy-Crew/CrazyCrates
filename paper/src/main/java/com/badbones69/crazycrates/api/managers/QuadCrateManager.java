package com.badbones69.crazycrates.api.managers;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.utilities.LoggerUtils;
import com.badbones69.crazycrates.api.utilities.handlers.tasks.CrateSessionHandler;
import com.badbones69.crazycrates.common.enums.crates.KeyType;
import com.badbones69.crazycrates.common.enums.particles.QuadCrateParticles;
import com.badbones69.crazycrates.api.utilities.handlers.objects.crates.Crate;
import com.badbones69.crazycrates.common.configuration.files.Config;
import com.badbones69.crazycrates.support.structures.QuadCrateSpiralHandler;
import com.badbones69.crazycrates.support.structures.StructureHandler;
import com.badbones69.crazycrates.support.structures.blocks.ChestStateHandler;
import com.badbones69.crazycrates.api.utilities.ScheduleUtils;
import com.badbones69.crazycrates.api.utilities.handlers.tasks.CrateTaskHandler;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class QuadCrateManager {

    // Global Methods.
    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final LoggerUtils loggerUtils = plugin.getStarter().getLoggerUtils();

    private final CrazyManager crazyManager = plugin.getStarter().getCrazyManager();

    private final ScheduleUtils scheduleUtils = plugin.getStarter().getScheduleUtils();

    private final Methods methods = plugin.getStarter().getMethods();

    private final ChestStateHandler chestStateHandler = plugin.getStarter().getChestStateHandler();

    private final CrateTaskHandler crateTaskHandler = plugin.getStarter().getCrateTaskHandler();
    
    private final CrateSessionHandler crateSessionHandler = plugin.getStarter().getSessionManager();

    // Class Internals.

    /**
     * Get the player object.
     */
    private final Player player;

    /**
     * Whether to check the player's hand.
     */
    private final boolean checkHand;

    /**
     * The crate that the player is using.
     */
    private final Crate crate;

    /**
     * The key type the player used.
     */
    private final KeyType keyType;

    /**
     * Where you save and fetch all display rewards.
     */
    private final List<Entity> displayedRewards = new ArrayList<>();

    /**
     * The spawn location.
     * Used to define where the structure will load.
     * Also used to get the center of the structure to teleport the player to.
     */
    private final Location spawnLocation;

    /**
     * The last location the player was originally at.
     */
    private final Location lastLocation;

    /**
     * Defines the locations of the Chests that will spawn in.
     */
    private final ArrayList<Location> crateLocations = new ArrayList<>();

    /**
     * Stores if the crate is open or not.
     */
    private final HashMap<Location, Boolean> cratesOpened = new HashMap<>();

    /**
     * Where you save all the chests spawned by the QuadCrate Task.
     */
    private final HashMap<Location, BlockState> quadCrateChests = new HashMap<>();

    /**
     * Where you save & fetch the old blocks prior to the structure spawning.
     */
    private final HashMap<Location, BlockState> oldBlocks = new HashMap<>();

    /**
     * Get the particles that will spawn above the crates.
     */
    private final Color particleColor;
    private final QuadCrateParticles particle;

    /**
     * Get the structure handler.
     */
    private final StructureHandler handler;

    /**
     * Builds everything that lets a QuadCrate function
     * @param player - The player who is opening the crate.
     * @param crate - The crate being opened by the player.
     * @param keyType - The key type the player is using.
     * @param spawnLocation - The spawn location where the crate spawned.
     * @param lastLocation - The last location the player was originally at.
     * @param inHand - Check if the item is in their hand.
     * @param handler - The structure handler that spawns the nbt files.
     */
    public QuadCrateManager(Player player, Crate crate, KeyType keyType, Location spawnLocation, Location lastLocation, boolean inHand, StructureHandler handler) {
        this.player = player;
        this.crate = crate;
        this.keyType = keyType;
        this.checkHand = inHand;

        this.spawnLocation = spawnLocation.getBlock().getLocation();
        this.lastLocation = lastLocation;

        this.handler = handler;

        List<QuadCrateParticles> particles = Arrays.asList(QuadCrateParticles.values());
        this.particle = particles.get(new Random().nextInt(particles.size()));
        this.particleColor = getColors().get(new Random().nextInt(getColors().size()));
        
        crateSessionHandler.addSession(this);
    }

    /**
     * Start the crate session.
     */
    public boolean startCrate() {

        // Check if it is on a block.
        if (spawnLocation.clone().subtract(0, 1, 0).getBlock().getType() == Material.AIR) {
            //player.sendMessage(Messages.NOT_ON_BLOCK.getMessage(methods));
            crazyManager.removePlayerFromOpeningList(player);
            crateSessionHandler.removeSession(this);
            return false;
        }

        // Check if schematic folder is empty.
        if (crazyManager.getCrateSchematics().isEmpty()) {
            //player.sendMessage(Messages.NO_SCHEMATICS_FOUND.getMessage(methods));
            crazyManager.removePlayerFromOpeningList(player);
            crateSessionHandler.removeSession(this);
            return false;
        }

        // Check if the blocks are able to be changed.
        List<Block> structureLocations = null;

        try {
            structureLocations = handler.getNearbyBlocks(spawnLocation.clone());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Loop through the blocks and check if the blacklist contains the block type.
        // Do not open the crate if the block is not able to be changed.
        assert structureLocations != null;

        for (Block block : structureLocations) {
            if (handler.getBlockBlackList().contains(block.getType())) {
                //player.sendMessage(Messages.NEEDS_MORE_ROOM.getMessage(methods));
                crazyManager.removePlayerFromOpeningList(player);
                crateSessionHandler.removeSession(this);
                return false;
            } else {
                oldBlocks.put(block.getLocation().clone(), block.getState());
            }
        }

        List<Entity> shovePlayers = new ArrayList<>();

        for (Entity entity : player.getNearbyEntities(3, 3, 3)) {
            if (entity instanceof Player) {
                for (QuadCrateManager ongoingCrate : crateSessionHandler.getCrateSessions()) {
                    if (entity.getUniqueId() == ongoingCrate.player.getUniqueId()) {
                        //player.sendMessage(Messages.TO_CLOSE_TO_ANOTHER_PLAYER.getMessage("%Player%", entity.getName(), methods));
                        crazyManager.removePlayerFromOpeningList(player);
                        crateSessionHandler.removeSession(this);
                        return false;
                    }
                }

                shovePlayers.add(entity);
            }
        }

        if (!crazyManager.takeKeys(1, player, crate, keyType, checkHand)) {
            methods.failedToTakeKey(player, crate);
            crazyManager.removePlayerFromOpeningList(player);
            crateSessionHandler.removeSession(this);

            return false;
        }

        if (crazyManager.getHologramController() != null) crazyManager.getHologramController().removeHologram(spawnLocation.getBlock());

        // Shove other players away from the player opening the crate.
        shovePlayers.forEach(entity -> entity.getLocation().toVector().subtract(spawnLocation.clone().toVector()).normalize().setY(1));

        // Store the spawned Crates ( Chest Block ) in the ArrayList.
        addCrateLocations(2, 1, 0);
        addCrateLocations(0, 1, 2);

        addCrateLocations(4, 1, 2);
        addCrateLocations(2, 1, 4);

        // Throws unopened crates in a HashMap.
        crateLocations.forEach(loc -> cratesOpened.put(loc, false));

        // This holds the quad crate's spawned chests.
        for (Location loc : crateLocations) {
            if (crateLocations.contains(loc)) quadCrateChests.put(loc.clone(), loc.getBlock().getState());
        }

        // Paste the structure in.
        handler.pasteStructure(spawnLocation.clone());

        // Teleport player to center.
        player.teleport(spawnLocation.clone().add(handler.getStructureX() / 2, 1.0, handler.getStructureZ() / 2));

        crazyManager.addQuadCrateTask(player, scheduleUtils.timer(0L, 1L, () -> {
            final QuadCrateSpiralHandler spiralHandler = new QuadCrateSpiralHandler();

            AtomicInteger tickTillSpawn = new AtomicInteger(0); // At tick 60 the crate will spawn and then reset the tick.
            AtomicInteger crateNumber = new AtomicInteger(0); // The crate number that spawns next.

            Location particleLocation = crateLocations.get(crateNumber.get()).clone().add(.5, 3, .5);

            List<Location> spiralLocationsClockwise = spiralHandler.getSpiralLocationClockwise(particleLocation);

            List<Location> spiralLocationsCounterClockwise = spiralHandler.getSpiralLocationCounterClockwise(particleLocation);

            if (tickTillSpawn.get() < 60) {
                spawnParticles(particle, particleColor, spiralLocationsClockwise.get(tickTillSpawn.get()), spiralLocationsCounterClockwise.get(tickTillSpawn.get()));
                tickTillSpawn.incrementAndGet();
            } else {
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_STEP, 1, 1);
                Block chest = crateLocations.get(crateNumber.get()).getBlock();
                chest.setType(Material.CHEST);
                chestStateHandler.rotateChest(chest, crateNumber.get());

                if (crateNumber.get() == 3) { // Last crate has spawned.
                    crazyManager.endQuadCrate(player); // Cancelled when method is called.
                } else {
                    tickTillSpawn.set(0);
                    crateNumber.incrementAndGet();
                }
            }
        }));

        crateTaskHandler.addTask(player, scheduleUtils.later(Config.QUAD_CRATE_TIMERS, () -> {
            endCrateForce(true);

            //player.sendMessage(Messages.OUT_OF_TIME.getMessage(methods));
        }));

        return false;
    }

    /**
     * End the crate session.
     */
    public void endCrate() {
        scheduleUtils.later(3 * 20L, () -> {
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

            if (crazyManager.getHologramController() != null) crazyManager.getHologramController().createHologram(spawnLocation.getBlock(), crate);

            crateTaskHandler.endCrate(player);

            // Remove the player from the list saying they are opening a crate.
            crazyManager.removePlayerFromOpeningList(player);

            // Remove the "instance" from the crate sessions.
            crateSessionHandler.removeSession(this);
        });
    }

    /**
     * End a crate session by force.
     * @param removeForce - The option that decides if the crate should be removed.
     */
    public void endCrateForce(boolean removeForce) {
        crateLocations.forEach(location -> quadCrateChests.get(location).update(true, false));
        displayedRewards.forEach(Entity::remove);
        player.teleport(lastLocation);

        if (removeForce) {
            crazyManager.removePlayerFromOpeningList(player);
            crateSessionHandler.removeSession(this);
        }

        handler.removeStructure();
    }

    /**
     * Adds a location to the arraylist!
     * @param x - The x coordinate
     * @param y - The y coordinate
     * @param z - The z coordinate
     */
    public void addCrateLocations(Integer x, Integer y, Integer z) {
        crateLocations.add(spawnLocation.clone().add(x, y, z));
    }

    /**
     * @return A list of colors.
     */
    private List<Color> getColors() {
        return Arrays.asList(
                Color.AQUA, Color.BLACK, Color.BLUE, Color.FUCHSIA, Color.GRAY,
                Color.GREEN, Color.LIME, Color.MAROON, Color.NAVY, Color.OLIVE,
                Color.ORANGE, Color.PURPLE, Color.RED, Color.SILVER, Color.TEAL,
                Color.WHITE, Color.YELLOW);
    }

    /**
     * Spawns a particle on 2 locations, Location 1 & Location 2.
     * @param quadCrateParticle - The particle from the enum.
     * @param particleColor - The color of the particle.
     * @param location1 - The first location of the particle.
     * @param location2 - The second location of the particle.
     */
    private void spawnParticles(QuadCrateParticles quadCrateParticle, Color particleColor, Location location1, Location location2) {
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
     * @return All active crate sessions.
     */
    public List<QuadCrateManager> getCrateSessions() {
        return crateSessionHandler.getCrateSessions();
    }

    /**
     * @return The player object.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return All crate locations.
     */
    public List<Location> getCrateLocations() {
        return crateLocations;
    }

    /**
     * @return All open crates.
     */
    public HashMap<Location, Boolean> getCratesOpened() {
        return cratesOpened;
    }

    /**
     * @return The crate object.
     */
    public Crate getCrate() {
        return crate;
    }

    /**
     * @return The display rewards.
     */
    public List<Entity> getDisplayedRewards() {
        return displayedRewards;
    }

    /**
     * Check if all crates are opened.
     * @return false if they are all not open & true if they are.
     */
    public boolean allCratesOpened() {
        for (Map.Entry<Location, Boolean> location : cratesOpened.entrySet()) {
            if (!location.getValue()) return false;
        }

        return true;
    }
}