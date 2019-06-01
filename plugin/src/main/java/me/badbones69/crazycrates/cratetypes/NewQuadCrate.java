package me.badbones69.crazycrates.cratetypes;

import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.enums.Messages;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.api.objects.CrateSchematic;
import me.badbones69.crazycrates.multisupport.nms.NMSSupport;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class NewQuadCrate implements Listener {
	
	private static CrazyCrates cc = CrazyCrates.getInstance();
	private static NMSSupport nms = cc.getNMSSupport();
	private static List<NewQuadCrate> ongoingCrates = new ArrayList<>();
	private static List<Material> blacklistBlocks = nms.getQuadCrateBlacklistBlocks();
	
	private Crate crate;
	private Player player;
	private Location lastLocation;
	private Location spawnLocation;
	private List<Location> chestLocations;
	private List<Entity> displayedRewards;
	private CrateSchematic crateSchematic;
	private List<Location> schematicLocations;
	private List<BukkitRunnable> ongoingTasks;
	private HashMap<Location, BlockState> oldBlocks;
	
	public NewQuadCrate(Player player, Crate crate, Location spawnLocation, Location lastLocation) {
		this.crate = crate;
		this.player = player;
		this.lastLocation = lastLocation;
		this.spawnLocation = spawnLocation;
		ongoingCrates.add(this);
	}
	
	//- Check if the spawnLocation is standing on a block
	//- Pick the schematic to be used
	//- Check if there is enough room for schematic
	//- Check if nearby players are to close and shoot them away
	//- Teleport player to spawn location
	//- Save the chestLocations
	//- Save all old block states
	//- Paste in the schematic
	//- Start the particle show and spawn the crates
	public void startCrate() {
		//Check if the spawnLocation is on a block
		if(spawnLocation.clone().subtract(0, 1, 0).getBlock().getType() == Material.AIR) {
			player.sendMessage(Messages.NOT_ON_BLOCK.getMessage());
			cc.removePlayerFromOpeningList(player);
			return;
		}
		crateSchematic = cc.getCrateSchematics().get(new Random().nextInt(cc.getCrateSchematics().size()));
		schematicLocations = nms.getLocations(crateSchematic.getSchematicFile(), spawnLocation);
		//Check if the locations are all able to be changed
		for(Location loc : schematicLocations) {
			if(blacklistBlocks.contains(loc.getBlock())) {
				player.sendMessage(Messages.NEEDS_MORE_ROOM.getMessage());
				cc.removePlayerFromOpeningList(player);
				return;
			}
		}
		//Checking if players nearby are opening a quadcrate.
		List<Entity> shovePlayers = new ArrayList<>();
		for(Entity entity : player.getNearbyEntities(3, 3, 3)) {
			if(entity instanceof Player) {
				for(NewQuadCrate ongoingCrate : ongoingCrates) {
					if(entity.getUniqueId() == ongoingCrate.getPlayer().getUniqueId()) {
						HashMap<String, String> placeholders = new HashMap<>();
						placeholders.put("%Player%", entity.getName());
						placeholders.put("%player%", entity.getName());
						player.sendMessage(Messages.TO_CLOSE_TO_ANOTHER_PLAYER.getMessage(placeholders));
						cc.removePlayerFromOpeningList(player);
						return;
					}
				}
				shovePlayers.add(entity);
			}
		}
		player.teleport(spawnLocation.clone().add(.5, 0, .5));
		//Shove other players away from the player
		for(Entity entity : shovePlayers) {
			entity.setVelocity(entity.getLocation().toVector().subtract(spawnLocation.toVector()).normalize().setY(1));
		}
		//Add the chestLocations
		chestLocations.add(spawnLocation.clone().add(2, 0, 0));
		chestLocations.add(spawnLocation.clone().add(0, 0, 2));
		chestLocations.add(spawnLocation.clone().add(-2, 0, 0));
		chestLocations.add(spawnLocation.clone().add(0, 0, -2));
		//Save the oldBlock states
		for(Location loc : schematicLocations) {
			if(!chestLocations.contains(loc)) {
				oldBlocks.put(loc.clone(), loc.getBlock().getState());
			}
		}
		nms.pasteSchematic(crateSchematic.getSchematicFile(), spawnLocation);
		cc.addQuadCrateTask(player, new BukkitRunnable() {
			
			@Override
			public void run() {
			
			}
		}.runTaskTimer(cc.getPlugin(), 0, 1));
	}
	
	public Crate getCrate() {
		return crate;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Location getLastLocation() {
		return lastLocation;
	}
	
	public List<Location> getChestLocations() {
		return chestLocations;
	}
	
	public List<Entity> getDisplayedRewards() {
		return displayedRewards;
	}
	
	public List<BukkitRunnable> getOngoingTasks() {
		return ongoingTasks;
	}
	
	public CrateSchematic getCrateSchematic() {
		return crateSchematic;
	}
	
	public List<Location> getSchematicLocations() {
		return schematicLocations;
	}
	
	public HashMap<Location, BlockState> getOldBlocks() {
		return oldBlocks;
	}
	
	public static List<NewQuadCrate> getOngoingCrates() {
		return ongoingCrates;
	}
	
}