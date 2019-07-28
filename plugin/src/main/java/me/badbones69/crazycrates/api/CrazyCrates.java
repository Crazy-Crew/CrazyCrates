package me.badbones69.crazycrates.api;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.enums.BrokeLocation;
import me.badbones69.crazycrates.api.enums.CrateType;
import me.badbones69.crazycrates.api.enums.KeyType;
import me.badbones69.crazycrates.api.enums.Messages;
import me.badbones69.crazycrates.api.objects.*;
import me.badbones69.crazycrates.controllers.CrateControl;
import me.badbones69.crazycrates.controllers.FileManager;
import me.badbones69.crazycrates.controllers.FileManager.Files;
import me.badbones69.crazycrates.controllers.GUIMenu;
import me.badbones69.crazycrates.cratetypes.*;
import me.badbones69.crazycrates.multisupport.Version;
import me.badbones69.crazycrates.multisupport.itemnbtapi.NBTItem;
import me.badbones69.crazycrates.multisupport.nms.NMSSupport;
import me.badbones69.crazycrates.multisupport.nms.v1_10_R1.NMS_v1_10_R1;
import me.badbones69.crazycrates.multisupport.nms.v1_11_R1.NMS_v1_11_R1;
import me.badbones69.crazycrates.multisupport.nms.v1_12_R1.NMS_v1_12_R1;
import me.badbones69.crazycrates.multisupport.nms.v1_13_R2.NMS_v1_13_R2;
import me.badbones69.crazycrates.multisupport.nms.v1_14_R1.NMS_v1_14_R1;
import me.badbones69.crazycrates.multisupport.nms.v1_8_R3.NMS_v1_8_R3;
import me.badbones69.crazycrates.multisupport.nms.v1_9_R1.NMS_v1_9_R1;
import me.badbones69.crazycrates.multisupport.nms.v1_9_R2.NMS_v1_9_R2;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

public class CrazyCrates {
	
	private static FileManager fileManager = FileManager.getInstance();
	/**
	 * The instance of this class.
	 */
	private static CrazyCrates instance = new CrazyCrates();
	/**
	 * All the crates that have been loaded.
	 */
	private ArrayList<Crate> crates = new ArrayList<>();
	/**
	 * A list of all the physical crate locations.
	 */
	private ArrayList<CrateLocation> crateLocations = new ArrayList<>();
	/**
	 * List of all the broken crates.
	 */
	private ArrayList<String> brokecrates = new ArrayList<>();
	
	/**
	 * List of broken physical crate locations.
	 */
	private List<BrokeLocation> brokeLocations = new ArrayList<>();
	
	/**
	 * The crate that the player is opening.
	 */
	private HashMap<UUID, Crate> playerOpeningCrates = new HashMap<>();
	
	/**
	 * Keys that are being used in crates. Only needed in cosmic due to it taking the key after the player picks a prize and not in a start method.
	 */
	private HashMap<UUID, KeyType> playerKeys = new HashMap<>();
	
	/**
	 * A list of all current crate tasks that are running that a time. Used to force stop any crates it needs to.
	 */
	private HashMap<UUID, BukkitTask> currentTasks = new HashMap<>();
	
	/**
	 * A list of tasks being ran by the QuadCrate type.
	 */
	private HashMap<UUID, ArrayList<BukkitTask>> currentQuadTasks = new HashMap<>();
	
	/**
	 * The time in seconds a quadcrate can go until afk kicks them from it.
	 */
	private Integer quadCrateTimer;
	
	/**
	 * A list of current crate schematics for Quad Crate.
	 */
	private List<CrateSchematic> crateSchematics = new ArrayList<>();
	
	/**
	 * If the player's inventory is full when given a physical key it will instead give them virtual keys. If false it will drop the keys on the ground.
	 */
	private boolean giveVirtualKeysWhenInventoryFull;
	
	/**
	 * True if at least one crate gives new players keys and false if none give new players keys.
	 */
	private boolean giveNewPlayersKeys;
	
	/**
	 * True if using 1.13+ material names and false if using lower versions.
	 */
	private boolean useNewMaterial;
	
	/**
	 * The NMS version needed to be used.
	 */
	private NMSSupport nmsSupport;
	
	/**
	 * Schematic locations for 1.13+
	 */
	private HashMap<UUID, Location[]> schemLocations = new HashMap<>();
	
	/**
	 * The CrazyCrates plugin.
	 */
	private Plugin plugin;
	
	/**
	 * Gets the instance of the CrazyCrates class.
	 * @return Instance of this class.
	 */
	public static CrazyCrates getInstance() {
		return instance;
	}
	
	/**
	 * Get the file manager that controls all yml files.
	 * @return The FileManager that controls all yml files.
	 */
	public static FileManager getFileManager() {
		return fileManager;
	}
	
	/**
	 * Loads all the information the plugin needs to run.
	 */
	public void loadCrates() {
		giveNewPlayersKeys = false;
		crates.clear();
		brokecrates.clear();
		crateLocations.clear();
		crateSchematics.clear();
		Version version = Version.getCurrentVersion();
		useNewMaterial = version.isNewer(Version.v1_12_R1);
		switch(version) {
			case v1_8_R3:
				nmsSupport = new NMS_v1_8_R3();
				break;
			case v1_9_R1:
				nmsSupport = new NMS_v1_9_R1();
				break;
			case v1_9_R2:
				nmsSupport = new NMS_v1_9_R2();
				break;
			case v1_10_R1:
				nmsSupport = new NMS_v1_10_R1();
				break;
			case v1_11_R1:
				nmsSupport = new NMS_v1_11_R1();
				break;
			case v1_12_R1:
				nmsSupport = new NMS_v1_12_R1();
				break;
			case v1_13_R2:
				nmsSupport = new NMS_v1_13_R2();
				break;
			case v1_14_R1:
				nmsSupport = new NMS_v1_14_R1();
				break;
		}
		plugin = Bukkit.getPluginManager().getPlugin("CrazyCrates");
		quadCrateTimer = Files.CONFIG.getFile().getInt("Settings.QuadCrate.Timer") * 20;
		giveVirtualKeysWhenInventoryFull = Files.CONFIG.getFile().getBoolean("Settings.Give-Virtual-Keys-When-Inventory-Full");
		if(fileManager.isLogging()) System.out.println(fileManager.getPrefix() + "Loading all crate information...");
		for(String crateName : fileManager.getAllCratesNames()) {
			//			if(fileManager.isLogging()) System.out.println(fileManager.getPrefix() + "Loading " + crateName + ".yml information....");
			try {
				FileConfiguration file = fileManager.getFile(crateName).getFile();
				ArrayList<Prize> prizes = new ArrayList<>();
				String previewName = file.contains("Crate.Preview-Name") ? file.getString("Crate.Preview-Name") : file.getString("Crate.Name");
				ArrayList<Tier> tiers = new ArrayList<>();
				if(file.contains("Crate.Tiers") && file.getConfigurationSection("Crate.Tiers") != null) {
					for(String tier : file.getConfigurationSection("Crate.Tiers").getKeys(false)) {
						String path = "Crate.Tiers." + tier;
						tiers.add(new Tier(tier, file.getString(path + ".Name"), file.getString(path + ".Color"), file.getInt(path + ".Chance"), file.getInt(path + ".MaxRange")));
					}
				}
				for(String prize : file.getConfigurationSection("Crate.Prizes").getKeys(false)) {
					Prize altPrize = null;
					String path = "Crate.Prizes." + prize;
					ArrayList<Tier> prizeTiers = new ArrayList<>();
					for(String tier : file.getStringList(path + ".Tiers")) {
						for(Tier loadedTier : tiers) {
							if(loadedTier.getName().equalsIgnoreCase(tier)) {
								prizeTiers.add(loadedTier);
							}
						}
					}
					if(file.contains(path + ".Alternative-Prize")) {
						if(file.getBoolean(path + ".Alternative-Prize.Toggle")) {
							altPrize = new Prize("Alternative-Prize",
							file.getStringList(path + ".Alternative-Prize.Messages"),
							file.getStringList(path + ".Alternative-Prize.Commands"),
							getItems(file, prize + ".Alternative-Prize"));
						}
					}
					ArrayList<ItemStack> itemPrizes = new ArrayList<>(getItems(file, prize));
					if(file.contains(path + ".Editor-Items")) {
						for(Object list : file.getList(path + ".Editor-Items")) {
							itemPrizes.add((ItemStack) list);
						}
					}
					prizes.add(new Prize(prize, getDisplayItem(file, prize),
					file.getStringList(path + ".Messages"),
					file.getStringList(path + ".Commands"),
					itemPrizes,
					crateName,
					file.getInt(path + ".Chance", 100),
					file.getInt(path + ".MaxRange", 100),
					file.getBoolean(path + ".Firework"),
					file.getStringList(path + ".BlackListed-Permissions"),
					prizeTiers,
					altPrize));
				}
				Integer newPlayersKeys = file.getInt("Crate.StartingKeys");
				if(giveNewPlayersKeys = false) {
					if(newPlayersKeys > 0) {
						giveNewPlayersKeys = true;
					}
				}
				crates.add(new Crate(crateName, previewName, CrateType.getFromName(file.getString("Crate.CrateType")), getKey(file), prizes, file, newPlayersKeys, tiers));
				//				if(fileManager.isLogging()) System.out.println(fileManager.getPrefix() + "" + crateName + ".yml has been loaded.");
			}catch(Exception e) {
				brokecrates.add(crateName);
				Bukkit.getLogger().log(Level.WARNING, fileManager.getPrefix() + "There was an error while loading the " + crateName + ".yml file.");
				e.printStackTrace();
			}
		}
		crates.add(new Crate("Menu", "Menu", CrateType.MENU, new ItemStack(Material.AIR), new ArrayList<>(), null, 0, null));
		if(fileManager.isLogging()) System.out.println(fileManager.getPrefix() + "All crate information has been loaded.");
		if(fileManager.isLogging()) System.out.println(fileManager.getPrefix() + "Loading all the physical crate locations.");
		FileConfiguration locations = Files.LOCATIONS.getFile();
		int loadedAmount = 0;
		int brokeAmount = 0;
		if(locations.getConfigurationSection("Locations") != null) {
			for(String locationName : locations.getConfigurationSection("Locations").getKeys(false)) {
				try {
					String worldName = locations.getString("Locations." + locationName + ".World");
					World world = Bukkit.getWorld(worldName);
					int x = locations.getInt("Locations." + locationName + ".X");
					int y = locations.getInt("Locations." + locationName + ".Y");
					int z = locations.getInt("Locations." + locationName + ".Z");
					Location location = new Location(world, x, y, z);
					Crate crate = getCrateFromName(locations.getString("Locations." + locationName + ".Crate"));
					if(world != null) {
						crateLocations.add(new CrateLocation(locationName, crate, location));
						loadedAmount++;
					}else {
						brokeLocations.add(new BrokeLocation(locationName, crate, x, y, z, worldName));
						brokeAmount++;
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		//Checking if all physical locations loaded
		if(fileManager.isLogging()) {
			if(loadedAmount > 0 || brokeAmount > 0) {
				if(brokeAmount <= 0) {
					System.out.println(fileManager.getPrefix() + "All physical crate locations have been loaded.");
				}else {
					System.out.println(fileManager.getPrefix() + "Loaded " + loadedAmount + " physical crate locations.");
					System.out.println(fileManager.getPrefix() + "Failed to load " + brokeAmount + " physical crate locations.");
				}
			}
		}
		//Loading schematic files
		if(fileManager.isLogging()) System.out.println(fileManager.getPrefix() + "Searching for schematics to load.");
		String[] schems = new File(plugin.getDataFolder() + "/Schematics/").list();
		boolean isNewer = Version.getCurrentVersion().isNewer(Version.v1_12_R1);
		for(String schematicName : schems) {
			if(isNewer) {
				if(schematicName.endsWith(".nbt")) {
					crateSchematics.add(new CrateSchematic(schematicName.replace(".nbt", ""), new File(plugin.getDataFolder() + "/Schematics/" + schematicName)));
					if(fileManager.isLogging()) System.out.println(fileManager.getPrefix() + schematicName + " was successfully found and loaded.");
				}
			}else {
				if(schematicName.endsWith(".schematic")) {
					crateSchematics.add(new CrateSchematic(schematicName.replace(".schematic", ""), new File(plugin.getDataFolder() + "/Schematics/" + schematicName)));
					if(fileManager.isLogging()) System.out.println(fileManager.getPrefix() + schematicName + " was successfully found and loaded.");
				}
			}
		}
		if(fileManager.isLogging()) System.out.println(fileManager.getPrefix() + "All schematics were found and loaded.");
		cleanDataFile();
	}
	
	/**
	 * If the player's inventory is full when given a physical key it will instead give them virtual keys. If false it will drop the keys on the ground.
	 * @return True if the player will get a virtual key and false if it drops on the floor.
	 */
	public boolean getGiveVirtualKeysWhenInventoryFull() {
		return giveVirtualKeysWhenInventoryFull;
	}
	
	/**
	 * This method is deigned to help clean the data.yml file of any unless info that it may have.
	 */
	public void cleanDataFile() {
		FileConfiguration data = Files.DATA.getFile();
		if(data.contains("Players")) {
			boolean logging = fileManager.isLogging();
			if(logging) System.out.println(fileManager.getPrefix() + "Cleaning up the data.yml file.");
			List<String> removePlayers = new ArrayList<>();
			for(String uuid : data.getConfigurationSection("Players").getKeys(false)) {
				boolean hasKeys = false;
				List<String> noKeys = new ArrayList<>();
				for(Crate crate : getCrates()) {
					if(data.getInt("Players." + uuid + "." + crate.getName()) <= 0) {
						noKeys.add(crate.getName());
					}else {
						hasKeys = true;
					}
				}
				if(hasKeys) {
					for(String crate : noKeys) {
						data.set("Players." + uuid + "." + crate, null);
					}
				}else {
					removePlayers.add(uuid);
				}
			}
			if(removePlayers.size() > 0) {
				if(logging) System.out.println(fileManager.getPrefix() + removePlayers.size() + " player's data has been marked to be removed.");
				for(String uuid : removePlayers) {
					//				if(logging) System.out.println(fileManager.getPrefix() + "Removed " + data.getString("Players." + uuid + ".Name") + "'s empty data from the data.yml.");
					data.set("Players." + uuid, null);
				}
				if(logging) System.out.println(fileManager.getPrefix() + "All empty player data has been removed.");
			}
			if(logging) System.out.println(fileManager.getPrefix() + "The data.yml file has been cleaned.");
			Files.DATA.saveFile();
		}
	}
	
	/**
	 * Opens a crate for a player.
	 * @param player The player that is having the crate opened for them.
	 * @param crate The crate that is being used.
	 * @param location The location that may be needed for some crate types.
	 */
	public void openCrate(Player player, Crate crate, KeyType key, Location location, boolean virtualCrate) {
		addPlayerToOpeningList(player, crate);
		boolean broadcast = crate.getFile() != null && crate.getFile().getBoolean("Crate.OpeningBroadCast");
		if(broadcast && crate.getCrateType() != CrateType.QUAD_CRATE) {
			if(!crate.getFile().getString("Crate.BroadCast").isEmpty()) {
				Bukkit.broadcastMessage(Methods.color(crate.getFile().getString("Crate.BroadCast").replaceAll("%Prefix%", Methods.getPrefix()).replaceAll("%prefix%", Methods.getPrefix()).replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName())));
			}
			broadcast = false;
		}
		switch(crate.getCrateType()) {
			case MENU:
				GUIMenu.openGUI(player);
				break;
			case COSMIC:
				Cosmic.openCosmic(player, crate, key);
				break;
			case CSGO:
				CSGO.openCSGO(player, crate, key);
				break;
			case ROULETTE:
				Roulette.openRoulette(player, crate, key);
				break;
			case WHEEL:
				Wheel.startWheel(player, crate, key);
				break;
			case WONDER:
				Wonder.startWonder(player, crate, key);
				break;
			case WAR:
				War.openWarCrate(player, crate, key);
				break;
			case QUAD_CRATE:
				Location last = player.getLocation();
				last.setPitch(0F);
				QuadCrateSession session = new QuadCrateSession(player, crate, key, location, last);
				broadcast = session.startCrate();
				break;
			case FIRE_CRACKER:
				if(CrateControl.inUse.containsValue(location)) {
					player.sendMessage(Messages.QUICK_CRATE_IN_USE.getMessage());
					removePlayerFromOpeningList(player);
					return;
				}else {
					if(virtualCrate) {
						player.sendMessage(Messages.CANT_BE_A_VIRTUAL_CRATE.getMessage());
						removePlayerFromOpeningList(player);
						return;
					}else {
						CrateControl.inUse.put(player, location);
						FireCracker.startFireCracker(player, crate, key, location);
					}
				}
				break;
			case QUICK_CRATE:
				if(CrateControl.inUse.containsValue(location)) {
					player.sendMessage(Messages.QUICK_CRATE_IN_USE.getMessage());
					removePlayerFromOpeningList(player);
					return;
				}else {
					if(virtualCrate && location.equals(player.getLocation())) {
						player.sendMessage(Messages.CANT_BE_A_VIRTUAL_CRATE.getMessage());
						removePlayerFromOpeningList(player);
						return;
					}else {
						CrateControl.inUse.put(player, location);
						QuickCrate.openCrate(player, location, crate, key);
					}
				}
				break;
			case CRATE_ON_THE_GO:
				if(virtualCrate) {
					player.sendMessage(Messages.CANT_BE_A_VIRTUAL_CRATE.getMessage());
					removePlayerFromOpeningList(player);
					return;
				}else {
					takeKeys(1, player, crate, key);
					Prize prize = crate.pickPrize(player);
					givePrize(player, prize);
					if(prize.useFireworks()) {
						Methods.fireWork(player.getLocation().add(0, 1, 0));
					}
					removePlayerFromOpeningList(player);
				}
				break;
		}
		if(broadcast) {
			if(!crate.getFile().getString("Crate.BroadCast").isEmpty()) {
				Bukkit.broadcastMessage(Methods.color(crate.getFile().getString("Crate.BroadCast").replaceAll("%Prefix%", Methods.getPrefix()).replaceAll("%prefix%", Methods.getPrefix()).replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName())));
			}
		}
	}
	
	/**
	 * This forces a crate to end and will not give out a prize. This is meant for people who leave the server to stop any errors or lag from happening.
	 * @param player The player that the crate is being ended for.
	 */
	public void endCrate(Player player) {
		if(currentTasks.containsKey(player.getUniqueId())) {
			currentTasks.get(player.getUniqueId()).cancel();
			removeCrateTask(player);
		}
	}
	
	/**
	 * Ends the tasks running by a player.
	 * @param player The player using the crate.
	 */
	public void endQuadCrate(Player player) {
		if(currentQuadTasks.containsKey(player.getUniqueId())) {
			for(BukkitTask task : currentQuadTasks.get(player.getUniqueId())) {
				task.cancel();
			}
			currentQuadTasks.remove(player.getUniqueId());
		}
	}
	
	/**
	 * Add a quad crate task that is going on for a player.
	 * @param player The player opening the crate.
	 * @param task The task of the quad crate.
	 */
	public void addQuadCrateTask(Player player, BukkitTask task) {
		if(currentQuadTasks.containsKey(player.getUniqueId())) {
			currentQuadTasks.get(player.getUniqueId()).add(task);
		}else {
			currentQuadTasks.put(player.getUniqueId(), new ArrayList<>());
			currentQuadTasks.get(player.getUniqueId()).add(task);
		}
	}
	
	/**
	 * Checks to see if the player has a quad crate task going on.
	 * @param player The player that is being checked.
	 * @return True if they do have a task and false if not.
	 */
	public boolean hasQuadCrateTask(Player player) {
		return currentQuadTasks.containsKey(player.getUniqueId());
	}
	
	/**
	 * Add a crate task that is going on for a player.
	 * @param player The player opening the crate.
	 * @param task The task of the crate.
	 */
	public void addCrateTask(Player player, BukkitTask task) {
		currentTasks.put(player.getUniqueId(), task);
	}
	
	/**
	 * Remove a task from the list of current tasks.
	 * @param player The player using the crate.
	 */
	public void removeCrateTask(Player player) {
		currentTasks.remove(player.getUniqueId());
	}
	
	/**
	 * Checks to see if the player has a crate task going on.
	 * @param player The player that is being checked.
	 * @return True if they do have a task and false if not.
	 */
	public boolean hasCrateTask(Player player) {
		return currentTasks.containsKey(player.getUniqueId());
	}
	
	/**
	 * A list of all the physical crate locations.
	 * @return List of locations.
	 */
	public ArrayList<CrateLocation> getCrateLocations() {
		return crateLocations;
	}
	
	/**
	 * Checks to see if the a location is a physical crate.
	 * @param loc The location you are checking.
	 * @return True if it is a physical crate and false if not.
	 */
	public boolean isCrateLocation(Location loc) {
		for(CrateLocation crateLocation : getCrateLocations()) {
			if(crateLocation.getLocation().equals(loc)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Gets the physical crate of the location.
	 * @param loc The location you are checking.
	 * @return A CrateLocation if the location is a physical crate otherwise null if not.
	 */
	public CrateLocation getCrateLocation(Location loc) {
		for(CrateLocation crateLocation : getCrateLocations()) {
			if(crateLocation.getLocation().equals(loc)) {
				return crateLocation;
			}
		}
		return null;
	}
	
	/**
	 * Get a list of all the broke physical crate locations.
	 * @return List of broken crate locations.
	 */
	public List<BrokeLocation> getBrokeCrateLocations() {
		return brokeLocations;
	}
	
	public void addCrateLocation(Location loc, Crate crate) {
		FileConfiguration locations = Files.LOCATIONS.getFile();
		String id = "1"; //Location ID
		for(int i = 1; locations.contains("Locations." + i); i++) {
			id = (i + 1) + "";
		}
		for(CrateLocation crateLocation : getCrateLocations()) {
			if(crateLocation.getLocation().equals(loc)) {
				id = crateLocation.getID();
				break;
			}
		}
		locations.set("Locations." + id + ".Crate", crate.getName());
		locations.set("Locations." + id + ".World", loc.getWorld().getName());
		locations.set("Locations." + id + ".X", loc.getBlockX());
		locations.set("Locations." + id + ".Y", loc.getBlockY());
		locations.set("Locations." + id + ".Z", loc.getBlockZ());
		Files.LOCATIONS.saveFile();
		crateLocations.add(new CrateLocation(id, crate, loc));
	}
	
	public void removeCrateLocation(String id) {
		Files.LOCATIONS.getFile().set("Locations." + id, null);
		Files.LOCATIONS.saveFile();
		CrateLocation loc = null;
		for(CrateLocation crateLocation : getCrateLocations()) {
			if(crateLocation.getID().equalsIgnoreCase(id)) {
				loc = crateLocation;
				break;
			}
		}
		if(loc != null) {
			crateLocations.remove(loc);
		}
	}
	
	public ArrayList<String> getBrokeCrates() {
		return brokecrates;
	}
	
	public ArrayList<Crate> getCrates() {
		return crates;
	}
	
	public Crate getCrateFromName(String name) {
		for(Crate crate : getCrates()) {
			if(crate.getName().equalsIgnoreCase(name)) {
				return crate;
			}
		}
		return null;
	}
	
	/**
	 * The time in seconds a quadcrate will last before kicking the player.
	 * @return The time in seconds till kick.
	 */
	public Integer getQuadCrateTimer() {
		return quadCrateTimer;
	}
	
	public Inventory loadPreview(Crate crate) {
		FileConfiguration file = crate.getFile();
		int slots = 9;
		for(int size = file.getConfigurationSection("Crate.Prizes").getKeys(false).size(); size > 9 && slots < crate.getMaxSlots(); size -= 9) {
			slots += 9;
		}
		Inventory inv = Bukkit.createInventory(null, slots, Methods.color(file.getString("Crate.Name")));
		for(String reward : file.getConfigurationSection("Crate.Prizes").getKeys(false)) {
			String id = file.getString("Crate.Prizes." + reward + ".DisplayItem");
			String name = file.getString("Crate.Prizes." + reward + ".DisplayName");
			List<String> lore = file.getStringList("Crate.Prizes." + reward + ".Lore");
			HashMap<Enchantment, Integer> enchantments = new HashMap<>();
			boolean glowing = false;
			int amount = 1;
			String player = "";
			if(file.contains("Crate.Prizes." + reward + ".Glowing")) {
				glowing = file.getBoolean("Crate.Prizes." + reward + ".Glowing");
			}
			if(file.contains("Crate.Prizes." + reward + ".Player")) {
				player = file.getString("Crate.Prizes." + reward + ".Player");
			}
			if(file.contains("Crate.Prizes." + reward + ".DisplayAmount")) {
				amount = file.getInt("Crate.Prizes." + reward + ".DisplayAmount");
			}
			if(file.contains("Crate.Prizes." + reward + ".DisplayEnchantments")) {
				for(String enchant : file.getStringList("Crate.Prizes." + reward + ".DisplayEnchantments")) {
					String[] b = enchant.split(":");
					enchantments.put(Enchantment.getByName(b[0]), Integer.parseInt(b[1]));
				}
			}
			try {
				inv.setItem(inv.firstEmpty(), new ItemBuilder().setMaterial(id).setAmount(amount).setName(name).setLore(lore).setEnchantments(enchantments).setGlowing(glowing).setPlayer(player).build());
			}catch(Exception e) {
				inv.addItem(new ItemBuilder().setMaterial(useNewMaterial ? "RED_TERRACOTTA" : "STAINED_CLAY:14").setName("&c&lERROR").setLore(Arrays.asList("&cThere is an error", "&cFor the reward: &c" + reward)).build());
			}
		}
		return inv;
	}
	
	public void givePrize(Player player, Prize prize) {
		if(prize != null) {
			prize = prize.hasBlacklistPermission(player) ? prize.getAltPrize() : prize;
			for(ItemStack i : prize.getItems()) {
				if(!Methods.isInventoryFull(player)) {
					player.getInventory().addItem(i);
				}else {
					player.getWorld().dropItemNaturally(player.getLocation(), i);
				}
			}
			for(String command : prize.getCommands()) {// /give %player% iron %random%:1-64
				if(command.contains("%random%:")) {
					String cmd = command;
					command = "";
					for(String word : cmd.split(" ")) {
						if(word.startsWith("%random%:")) {
							word = word.replace("%random%:", "");
							try {
								int min = Integer.parseInt(word.split("-")[0]);
								int max = Integer.parseInt(word.split("-")[1]);
								command += pickNumber(min, max) + " ";
							}catch(Exception e) {
								command += "1 ";
								Bukkit.getLogger().log(Level.WARNING, "[CrazyCrates]>> The prize " + prize.getName() + " in the " + prize.getCrate() + " crate has errored when trying to run a command.");
								Bukkit.getLogger().log(Level.WARNING, "[CrazyCrates]>> Command: " + cmd);
							}
						}else {
							command += word + " ";
						}
					}
					command = command.substring(0, command.length() - 1);
				}
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Methods.color(command.replace("%Player%", player.getName()).replace("%player%", player.getName())));
			}
			for(String msg : prize.getMessages()) {
				player.sendMessage(Methods.color(msg).replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName())
				.replace("%displayname%", prize.getDisplayItemBuilder().getName()).replace("%DisplayName%", prize.getDisplayItemBuilder().getName()));
			}
		}else {
			Bukkit.getLogger().log(Level.WARNING, "[CrazyCrates]>> No prize was found when giving " + player.getName() + " a prize.");
		}
	}
	
	public boolean addOfflineKeys(String player, Crate crate, int keys) {
		try {
			FileConfiguration data = Files.DATA.getFile();
			player = player.toLowerCase();
			if(data.contains("Offline-Players." + player + "." + crate.getName())) {
				keys += data.getInt("Offline-Players." + player + "." + crate.getName());
			}
			data.set("Offline-Players." + player + "." + crate.getName(), keys);
			Files.DATA.saveFile();
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean takeOfflineKeys(String player, Crate crate, int keys) {
		try {
			FileConfiguration data = Files.DATA.getFile();
			player = player.toLowerCase();
			int playerKeys = 0;
			if(data.contains("Offline-Players." + player + "." + crate.getName())) {
				playerKeys = data.getInt("Offline-Players." + player + "." + crate.getName());
			}
			data.set("Offline-Players." + player + "." + crate.getName(), playerKeys - keys);
			Files.DATA.saveFile();
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void loadOfflinePlayersKeys(Player player) {
		FileConfiguration data = Files.DATA.getFile();
		String name = player.getName().toLowerCase();
		if(data.contains("Offline-Players." + name)) {
			for(Crate crate : getCrates()) {
				if(data.contains("Offline-Players." + name + "." + crate.getName())) {
					addKeys(data.getInt("Offline-Players." + name + "." + crate.getName()), player, crate, KeyType.VIRTUAL_KEY);
				}
			}
			data.set("Offline-Players." + name, null);
			Files.DATA.saveFile();
		}
	}
	
	public void addPlayerToOpeningList(Player player, Crate crate) {
		playerOpeningCrates.put(player.getUniqueId(), crate);
	}
	
	public void removePlayerFromOpeningList(Player player) {
		playerOpeningCrates.remove(player.getUniqueId());
	}
	
	public boolean isInOpeningList(Player player) {
		return playerOpeningCrates.containsKey(player.getUniqueId());
	}
	
	public Crate getOpeningCrate(Player player) {
		return playerOpeningCrates.get(player.getUniqueId());
	}
	
	public Boolean isKey(ItemStack item) {
		for(Crate crate : getCrates()) {
			if(crate.getCrateType() != CrateType.MENU) {
				if(Methods.isSimilar(item, crate.getKey())) {
					return true;
				}
			}
		}
		return false;
	}
	
	public Crate getCrateFromKey(ItemStack item) {
		for(Crate crate : getCrates()) {
			if(crate.getCrateType() != CrateType.MENU) {
				if(Methods.isSimilar(item, crate.getKey())) {
					return crate;
				}
			}
		}
		return null;
	}
	
	public void addPlayerKeyType(Player player, KeyType key) {
		playerKeys.put(player.getUniqueId(), key);
	}
	
	public void removePlayerKeyType(Player player) {
		playerKeys.remove(player.getUniqueId());
	}
	
	public boolean hasPlayerKeyType(Player player) {
		return playerKeys.containsKey(player.getUniqueId());
	}
	
	/**
	 * The key type the player's current crate is using.
	 * @param player The player that is using the crate.
	 * @return The key type of the crate the player is using.
	 */
	public KeyType getPlayerKeyType(Player player) {
		return playerKeys.get(player.getUniqueId());
	}
	
	/**
	 * Checks to see if the player has a physical key of the crate in their main hand.
	 * @param player The player being checked.
	 * @param crate The crate that has the key you are checking.
	 * @return True if they have the key and false if not.
	 */
	public boolean hasPhysicalKey(Player player, Crate crate) {
		return Methods.isSimilar(player, crate.getKey());
	}
	
	public ItemStack getPhysicalKey(Player player, Crate crate) {
		for(ItemStack item : player.getOpenInventory().getBottomInventory().getContents()) {
			if(Methods.isSimilar(item, crate.getKey())) {
				return item;
			}
		}
		return null;
	}
	
	public HashMap<Crate, Integer> getVirtualKeys(Player player) {
		HashMap<Crate, Integer> keys = new HashMap<>();
		for(Crate crate : getCrates()) {
			keys.put(crate, getVirtualKeys(player, crate));
		}
		return keys;
	}
	
	public HashMap<Crate, Integer> getVirtualKeys(String playerName) {
		HashMap<Crate, Integer> keys = new HashMap<>();
		FileConfiguration data = Files.DATA.getFile();
		for(String uuid : data.getConfigurationSection("Players").getKeys(false)) {
			if(playerName.equalsIgnoreCase(data.getString("Players." + uuid + ".Name"))) {
				for(Crate crate : getCrates()) {
					keys.put(crate, data.getInt("Players." + uuid + "." + crate.getName()));
				}
			}
		}
		return keys;
	}
	
	public HashMap<UUID, Location[]> getSchematicLocations() {
		return schemLocations;
	}
	
	/**
	 * Get the amount of virtual keys a player has.
	 */
	public int getVirtualKeys(Player player, Crate crate) {
		return Files.DATA.getFile().getInt("Players." + player.getUniqueId() + "." + crate.getName());
	}
	
	/**
	 * Get the amount of physical keys a player has.
	 */
	public int getPhysicalKeys(Player player, Crate crate) {
		int keys = 0;
		for(ItemStack item : player.getOpenInventory().getBottomInventory().getContents()) {
			if(Methods.isSimilar(item, crate.getKey())) {
				keys += item.getAmount();
			}
		}
		return keys;
	}
	
	/**
	 * Get the total amount of keys a player has.
	 */
	public Integer getTotalKeys(Player player, Crate crate) {
		return getVirtualKeys(player, crate) + getPhysicalKeys(player, crate);
	}
	
	public void takeKeys(int amount, Player player, Crate crate, KeyType key) {
		switch(key) {
			case PHYSICAL_KEY:
				Methods.removeItem(getPhysicalKey(player, crate), player, amount);
				break;
			case VIRTUAL_KEY:
				String uuid = player.getUniqueId().toString();
				int keys = getVirtualKeys(player, crate);
				Files.DATA.getFile().set("Players." + uuid + ".Name", player.getName());
				int newAmount = Math.max((keys - amount), 0);
				if(newAmount <= 0) {
					Files.DATA.getFile().set("Players." + uuid + "." + crate.getName(), null);
				}else {
					Files.DATA.getFile().set("Players." + uuid + "." + crate.getName(), newAmount);
				}
				Files.DATA.saveFile();
				break;
		}
	}
	
	public void addKeys(int amount, Player player, Crate crate, KeyType key) {
		switch(key) {
			case PHYSICAL_KEY:
				if(Methods.isInventoryFull(player)) {
					if(giveVirtualKeysWhenInventoryFull) {
						addKeys(amount, player, crate, KeyType.VIRTUAL_KEY);
					}else {
						player.getWorld().dropItem(player.getLocation(), crate.getKey(amount));
					}
				}else {
					player.getInventory().addItem(crate.getKey(amount));
				}
				break;
			case VIRTUAL_KEY:
				String uuid = player.getUniqueId().toString();
				int keys = getVirtualKeys(player, crate);
				Files.DATA.getFile().set("Players." + uuid + ".Name", player.getName());
				Files.DATA.getFile().set("Players." + uuid + "." + crate.getName(), (Math.max((keys + amount), 0)));
				Files.DATA.saveFile();
				break;
		}
	}
	
	public void setKeys(int amount, Player player, Crate crate) {
		String uuid = player.getUniqueId().toString();
		Files.DATA.getFile().set("Players." + uuid + ".Name", player.getName());
		Files.DATA.getFile().set("Players." + uuid + "." + crate.getName(), amount);
		Files.DATA.saveFile();
	}
	
	public void checkNewPlayer(Player player) {
		if(giveNewPlayersKeys) {// Checks if any crate gives new players keys and if not then no need to do all this stuff.
			String uuid = player.getUniqueId().toString();
			if(player.hasPlayedBefore()) {
				for(Crate crate : getCrates()) {
					if(crate.doNewPlayersGetKeys()) {
						Files.DATA.getFile().set("Players." + uuid + "." + crate, crate.getNewPlayerKeys());
					}
				}
				Files.DATA.saveFile();
			}
		}
	}
	
	/**
	 * Get the NMS version being used.
	 * @return Version of NMS, returns null if not found.
	 */
	public NMSSupport getNMSSupport() {
		return nmsSupport;
	}
	
	public Plugin getPlugin() {
		return plugin;
	}
	
	public boolean useNewMaterial() {
		return useNewMaterial;
	}
	
	public void loadSchematics() {
		crateSchematics.clear();
		String[] schems = new File(plugin.getDataFolder() + "/Schematics/").list();
		boolean isNewer = Version.getCurrentVersion().isNewer(Version.v1_12_R1);
		for(String schematicName : schems) {
			if(isNewer) {
				if(schematicName.endsWith(".nbt")) {
					crateSchematics.add(new CrateSchematic(schematicName.replace(".nbt", ""), new File(plugin.getDataFolder() + "/Schematics/" + schematicName)));
				}
			}else {
				if(schematicName.endsWith(".schematic")) {
					crateSchematics.add(new CrateSchematic(schematicName.replace(".schematic", ""), new File(plugin.getDataFolder() + "/Schematics/" + schematicName)));
				}
			}
		}
	}
	
	public List<CrateSchematic> getCrateSchematics() {
		return crateSchematics;
	}
	
	public CrateSchematic getCrateSchematic(String name) {
		for(CrateSchematic schematic : crateSchematics) {
			if(schematic.getSchematicName().equalsIgnoreCase(name)) {
				return schematic;
			}
		}
		return null;
	}
	
	/**
	 * Check if an entity is a display reward for a crate.
	 * @param entity Entity you wish to check.
	 * @return True if it is a display reward item and false if not.
	 */
	public boolean isDisplayReward(Entity entity) {
		if(entity instanceof Item) {
			NBTItem item = new NBTItem(((Item) entity).getItemStack());
			return item.hasKey("crazycrates-item");
		}
		return false;
	}
	
	private ItemStack getKey(FileConfiguration file) {
		String name = file.getString("Crate.PhysicalKey.Name");
		List<String> lore = file.getStringList("Crate.PhysicalKey.Lore");
		String id = file.getString("Crate.PhysicalKey.Item");
		boolean glowing = false;
		if(file.contains("Crate.PhysicalKey.Glowing")) {
			glowing = file.getBoolean("Crate.PhysicalKey.Glowing");
		}
		return new ItemBuilder().setMaterial(id).setName(name).setLore(lore).setGlowing(glowing).build();
	}
	
	private ItemBuilder getDisplayItem(FileConfiguration file, String prize) {
		String path = "Crate.Prizes." + prize + ".";
		ItemBuilder itemBuilder = new ItemBuilder();
		try {
			itemBuilder.setMaterial(file.getString(path + "DisplayItem"))
			.setAmount(file.getInt(path + "DisplayAmount", 1))
			.setName(file.getString(path + "DisplayName"))
			.setLore(file.getStringList(path + "Lore"))
			.setGlowing(file.getBoolean(path + "Glowing"))
			.setUnbreakable(file.getBoolean(path + "Unbreakable"))
			.hideItemFlags(file.getBoolean(path + "HideItemFlags"))
			.setPlayer(file.getString(path + "Player"));
			HashMap<Enchantment, Integer> enchants = new HashMap<>();
			if(file.contains(path + "DisplayEnchantments")) {
				for(String enchant : file.getStringList(path + "DisplayEnchantments")) {
					for(Enchantment enc : Enchantment.values()) {
						if(Methods.getEnchantments().contains(enc.getName())) {
							enchant = enchant.toLowerCase();
							if(enchant.startsWith(enc.getName().toLowerCase() + ":") || enchant.startsWith(Methods.getEnchantmentName(enc).toLowerCase() + ":")) {
								itemBuilder.addEnchantments(enc, Integer.parseInt(enchant.split(":")[1]));
							}
						}
					}
				}
			}
			return itemBuilder;
		}catch(Exception e) {
			return new ItemBuilder().setMaterial(useNewMaterial ? "RED_TERRACOTTA" : "STAINED_CLAY:14").setName("&c&lERROR").setLore(Arrays.asList("&cThere is an error", "&cFor the reward: &c" + prize));
		}
	}
	
	private ArrayList<ItemStack> getItems(FileConfiguration file, String prize) {
		ArrayList<ItemStack> items = new ArrayList<>();
		for(String l : file.getStringList("Crate.Prizes." + prize + ".Items")) {
			ArrayList<String> lore = new ArrayList<>();
			HashMap<Enchantment, Integer> enchantments = new HashMap<>();
			String name = "";
			int amount = 1;
			String id = "Stone";
			String player = "";
			boolean unbreaking = false;
			for(String i : l.split(", ")) {
				if(i.startsWith("Item:")) {
					id = i.replaceAll("Item:", "");
				}else if(i.startsWith("Name:")) {
					name = Methods.color(i.replaceAll("Name:", ""));
				}else if(i.startsWith("Amount:")) {
					amount = Integer.parseInt(i.replaceAll("Amount:", ""));
				}else if(i.startsWith("Lore:")) {
					for(String L : i.replaceAll("Lore:", "").split(",")) {
						L = Methods.color(L);
						lore.add(L);
					}
				}else if(i.startsWith("Player:")) {
					player = i.replaceAll("Player:", "");
				}else if(i.startsWith("Unbreakable-Item:")) {
					if(i.replaceAll("Unbreakable-Item:", "").equalsIgnoreCase("true")) {
						unbreaking = true;
					}
				}else {
					for(Enchantment enc : Enchantment.values()) {
						if(enc.getName() != null) {
							if(i.toLowerCase().startsWith(enc.getName().toLowerCase() + ":") || i.toLowerCase().startsWith(Methods.getEnchantmentName(enc).toLowerCase() + ":")) {
								String[] breakdown = i.split(":");
								int lvl = Integer.parseInt(breakdown[1]);
								enchantments.put(enc, lvl);
							}
						}
					}
				}
			}
			try {
				items.add(new ItemBuilder().setMaterial(id).setAmount(amount).setName(name).setLore(lore).setEnchantments(enchantments).setPlayer(player).setUnbreakable(unbreaking).build());
			}catch(Exception e) {
				items.add(new ItemBuilder().setMaterial(useNewMaterial ? "RED_TERRACOTTA" : "STAINED_CLAY:14").setName("&c&lERROR").setLore(Arrays.asList("&cThere is an error", "&cFor the reward: &c" + prize)).build());
			}
		}
		return items;
	}
	
	private Integer pickNumber(int min, int max) {
		max++;
		return min + new Random().nextInt(max - min);
	}
	
}