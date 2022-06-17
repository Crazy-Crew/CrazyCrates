package com.badbones69.crazycrates.api.managers;

import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.enums.KeyType;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.enums.QuadCrateParticles;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.structures.QuadCrateSpiralHandler;
import com.badbones69.crazycrates.structures.StructureHandler;
import com.badbones69.crazycrates.structures.blocks.ChestStateHandler;
import com.badbones69.crazycrates.utils.Schedulers;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.*;

public class QuadCrateManager {

    private final ChestStateHandler quadCrateHandler = new ChestStateHandler();

    private static final List<QuadCrateManager> crateSessions = new ArrayList<>();

    private final QuadCrateManager instance;
    private final Player player;

    /**
     * Check player hand.
     */
    private final boolean checkHand;

    /**
     * The crate that is being used.
     */
    private final Crate crate;

    /**
     * The key type.
     */
    private final KeyType keyType;

    /**
     * The current displayed rewards.
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
     * Saves all the chests spawned by the QuadCrate task.
     */
    private final HashMap<Location, BlockState> quadCrateChests = new HashMap<>();

    /**
     * Saves all the old blocks to restore after.
     */
    private final HashMap<Location, BlockState> oldBlocks = new HashMap<>();

    /**
     * Get the particles that will be used to display above the crates.
     */
    private final Color particleColor;
    private final QuadCrateParticles particle;

    /**
     * Get the structure handler.
     */
    private final StructureHandler handler;

    public QuadCrateManager(Player player, Crate crate, KeyType keyType, Location spawnLocation, Location lastLocation, boolean inHand, StructureHandler handler) {
        this.instance = this;
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

        crateSessions.add(instance);
    }

    public boolean startCrate(JavaPlugin plugin) {

        // Check if it is on a block.
        if (spawnLocation.clone().subtract(0, 1, 0).getBlock().getType() == Material.AIR) {
            player.sendMessage(Messages.NOT_ON_BLOCK.getMessage());
            CrazyManager.getInstance().removePlayerFromOpeningList(player);
            crateSessions.remove(instance);
            return false;
        }

        // Check if schematic folder is empty.
        if (CrazyManager.getInstance().getCrateSchematics().isEmpty()) {
            player.sendMessage(Messages.NO_SCHEMATICS_FOUND.getMessage());
            CrazyManager.getInstance().removePlayerFromOpeningList(player);
            crateSessions.remove(instance);
            return false;
        }

        // Check if the blocks are able to be changed.
        List<Block> structureLocations = handler.getNearbyBlocks(spawnLocation.clone());

        // Loop through the blocks and check if the blacklist contains the block type.
        // Do not open the crate if the block is not able to be changed.
        for (Block block : structureLocations) {
            if (handler.getBlackList().contains(block.getType())) {
                player.sendMessage(Messages.NEEDS_MORE_ROOM.getMessage());
                CrazyManager.getInstance().removePlayerFromOpeningList(player);
                crateSessions.remove(instance);
                return false;
            } else {
                oldBlocks.put(block.getLocation().clone(), block.getState());
            }
        }

        List<Entity> shovePlayers = new ArrayList<>();

        for (Entity entity : player.getNearbyEntities(3, 3, 3)) {
            if (entity instanceof Player) {
                for (QuadCrateManager ongoingCrate : crateSessions) {
                    if (entity.getUniqueId() == ongoingCrate.player.getUniqueId()) {
                        player.sendMessage(Messages.TO_CLOSE_TO_ANOTHER_PLAYER.getMessage("%Player%", entity.getName()));
                        CrazyManager.getInstance().removePlayerFromOpeningList(player);
                        crateSessions.remove(instance);
                        return false;
                    }
                }
                shovePlayers.add(entity);
            }
        }

        if (!CrazyManager.getInstance().takeKeys(1, player, crate, keyType, checkHand)) {
            Methods.failedToTakeKey(player, crate);
            CrazyManager.getInstance().removePlayerFromOpeningList(player);
            crateSessions.remove(instance);
            return false;
        }

        if (CrazyManager.getInstance().getHologramController() != null) CrazyManager.getInstance().getHologramController().removeHologram(spawnLocation.getBlock());

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

        CrazyManager.getInstance().addQuadCrateTask(player, new Schedulers(plugin).timer(0, 1, new BukkitRunnable() {

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
                    new ChestStateHandler().rotateChest(chest, crateNumber);
                    if (crateNumber == 3) { // Last crate has spawned.
                        CrazyManager.getInstance().endQuadCrate(player); // Cancelled when method is called.
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
        }));

        CrazyManager.getInstance().addCrateTask(player, new Schedulers(plugin).later(CrazyManager.getInstance().getQuadCrateTimer(), new BukkitRunnable() {
            @Override
            public void run() {
                // End the crate by force.
                endCrateForce(true);
                player.sendMessage(Messages.OUT_OF_TIME.getMessage());
            }
        }));
        return false;
    }

    public void endCrate(JavaPlugin plugin) {
        new Schedulers(plugin).later(3 * 20, new BukkitRunnable() {
            @Override
            public void run() {
                // Update spawned crate block states which removes them.
                crateLocations.forEach(location -> quadCrateChests.get(location).update(true, false));

                // Remove displayed rewards.
                displayedRewards.forEach(Entity::remove);

                // Teleport player to last location.
                player.teleport(lastLocation);

                // Remove the structure blocks.
                handler.removeStructure(spawnLocation.clone());

                // Restore the old blocks.
                oldBlocks.keySet().forEach(location -> oldBlocks.get(location).update(true, false));

                if (CrazyManager.getInstance().getHologramController() != null)
                    CrazyManager.getInstance().getHologramController().createHologram(spawnLocation.getBlock(), crate);

                // End the crate.
                CrazyManager.getInstance().endCrate(player);

                // Remove the player from the list saying they are opening a crate.
                CrazyManager.getInstance().removePlayerFromOpeningList(player);

                // Remove the "instance" from the crate sessions.
                crateSessions.remove(instance);
            }
        });
    }

    // End the crate & remove the hologram by force.
    public void endCrateForce(boolean removeForce) {
        crateLocations.forEach(location -> quadCrateChests.get(location).update(true, false));
        displayedRewards.forEach(Entity::remove);
        player.teleport(lastLocation);
        if (removeForce) CrazyManager.getInstance().removePlayerFromOpeningList(player);
        if (removeForce) crateSessions.remove(instance);

        handler.removeStructure(lastLocation);
    }

    // Add the crate locations.
    public void addCrateLocations(Integer x, Integer y, Integer z) {
        crateLocations.add(spawnLocation.clone().add(x, y, z));
    }

    // Particle management. - TODO() - Move to another class.
    private List<Color> getColors() {
        return Arrays.asList(
                Color.AQUA, Color.BLACK, Color.BLUE, Color.FUCHSIA, Color.GRAY,
                Color.GREEN, Color.LIME, Color.MAROON, Color.NAVY, Color.OLIVE,
                Color.ORANGE, Color.PURPLE, Color.RED, Color.SILVER, Color.TEAL,
                Color.WHITE, Color.YELLOW);
    }

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

    // Get the crate sessions.
    public static List<QuadCrateManager> getCrateSessions() {
        return crateSessions;
    }

    // Get Player.
    public Player getPlayer() {
        return player;
    }

    // Get the crate.
    public List<Location> getCrateLocations() {
        return crateLocations;
    }

    // Get open crates.
    public HashMap<Location, Boolean> getCratesOpened() {
        return cratesOpened;
    }

    // Get the crate.
    public Crate getCrate() {
        return crate;
    }

    // Get display rewards.
    public List<Entity> getDisplayedRewards() {
        return displayedRewards;
    }

    // Check if all crates are opened.
    public Boolean allCratesOpened() {
        for (Map.Entry<Location, Boolean> location : cratesOpened.entrySet()) {
            if (!location.getValue()) return false;
        }
        return true;
    }

    // Fetch the handler.
    public ChestStateHandler quadCrateHandler() {
        return quadCrateHandler;
    }
}
