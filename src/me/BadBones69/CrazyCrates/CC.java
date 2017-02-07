package me.BadBones69.CrazyCrates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import me.BadBones69.CrazyCrates.API.KeyType;
import me.BadBones69.CrazyCrates.CrateTypes.CSGO;
import me.BadBones69.CrazyCrates.CrateTypes.Cosmic;
import me.BadBones69.CrazyCrates.CrateTypes.FireCracker;
import me.BadBones69.CrazyCrates.CrateTypes.QCC;
import me.BadBones69.CrazyCrates.CrateTypes.QuickCrate;
import me.BadBones69.CrazyCrates.CrateTypes.Roulette;
import me.BadBones69.CrazyCrates.CrateTypes.Wheel;
import me.BadBones69.CrazyCrates.CrateTypes.Wonder;

public class CC implements Listener{ //Crate Control
	
	public static HashMap<Player, HashMap<ItemStack, String>> Rewards = new HashMap<Player, HashMap<ItemStack, String>>();
	public static HashMap<Player, String> Crate = new HashMap<Player, String>();
	public static HashMap<Player, ItemStack> Key = new HashMap<Player, ItemStack>();
	public static HashMap<Player, Location> LastLoc = new HashMap<Player, Location>();
	public static HashMap<Player, Location> InUse = new HashMap<Player, Location>();
	
	@EventHandler
	public void onCrateOpen(PlayerInteractEvent e){
		Player player = e.getPlayer();
		FileConfiguration config = Main.settings.getConfig();
		if(e.getAction() == Action.LEFT_CLICK_BLOCK){
			Block block = e.getClickedBlock();
			if(Main.settings.getLocations().getConfigurationSection("Locations") == null){
				Main.settings.getLocations().set("Locations.Clear", null);
				Main.settings.saveLocations();
			}
			for(String location : Main.settings.getLocations().getConfigurationSection("Locations").getKeys(false)){
				String Crate = Main.settings.getLocations().getString("Locations."+location+".Crate");
				World w = Bukkit.getWorld(Main.settings.getLocations().getString("Locations."+location+".World"));
				int x = Main.settings.getLocations().getInt("Locations."+location+".X");
				int y = Main.settings.getLocations().getInt("Locations."+location+".Y");
				int z = Main.settings.getLocations().getInt("Locations."+location+".Z");
				Location loc = new Location(w, x, y, z);
				if(block.getLocation().equals(loc)){
					e.setCancelled(true);
					if(Crate.equalsIgnoreCase("Menu")){
						return;
					}else{
						if(config.getBoolean("Settings.Show-Preview")){
							GUI.openGUI(player, Crate);
						}
					}
					return;
				}
			}
		}
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
			for(String crate : Main.settings.getAllCratesNames()){
				if(e.hasItem()){
					ItemStack key = Methods.getItemInHand(player);
					if(key.hasItemMeta()){
						if(key.getItemMeta().hasDisplayName()){
							if(key.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getFile(crate).getString("Crate.PhysicalKey.Name")))){
								e.setCancelled(true);
								player.updateInventory();
							}
						}
					}
				}
			}
			Block block = e.getClickedBlock();
			if(Main.settings.getLocations().getConfigurationSection("Locations")==null){
				Main.settings.getLocations().set("Locations.Clear", null);
				Main.settings.saveLocations();
			}
			if(Main.settings.getLocations().contains("Locations")){
				for(String location : Main.settings.getLocations().getConfigurationSection("Locations").getKeys(false)){
					String Crate = Main.settings.getLocations().getString("Locations."+location+".Crate");
					World w = Bukkit.getWorld(Main.settings.getLocations().getString("Locations."+location+".World"));
					int x = Main.settings.getLocations().getInt("Locations."+location+".X");
					int y = Main.settings.getLocations().getInt("Locations."+location+".Y");
					int z = Main.settings.getLocations().getInt("Locations."+location+".Z");
					Location loc = new Location(w, x, y, z);
					if(block.getLocation().equals(loc)){
						e.setCancelled(true);
						if(Crate.equalsIgnoreCase("Menu")){
							player.performCommand("crazycrates");
							return;
						}
						String KeyName = Methods.color(Main.settings.getFile(Crate).getString("Crate.PhysicalKey.Name"));
						ItemStack key = null;
						Boolean hasKey = false;
						Boolean isPhysical = false;
						if(e.hasItem()){
							key = Methods.getItemInHand(player);
							if(key.isSimilar(Methods.getKey(Crate))){
								hasKey = true;
								isPhysical = true;
							}
						}
						if(config.getBoolean("Settings.Physical-Accepts-Virtual-Keys")){
							if(Methods.getKeys(player, Crate) >= 1){
								hasKey = true;
							}
						}
						if(hasKey){
							if(GUI.Crate.containsKey(player)){
								String msg = config.getString("Settings.AlreadyOpeningCrateMsg")
										.replaceAll("%Key%", KeyName).replaceAll("%key%", KeyName);
								player.sendMessage(Methods.color(Methods.getPrefix()+msg));
								return;
							}
							if(InUse.containsValue(loc)){
								String msg = config.getString("Settings.QuickCrateInUse");
								player.sendMessage(Methods.color(Methods.getPrefix()+msg));
								return;
							}
							GUI.Crate.put(player, Crate);
							CC.Crate.put(player, Crate);
							if(isPhysical){
								Key.put(player, key);
								Methods.Key.put(player, KeyType.PHYSICAL_KEY);
							}else{
								Methods.Key.put(player, KeyType.VIRTUAL_KEY);
								LastLoc.put(player, player.getLocation());
							}
							openCrate(player, Crate, loc);
							return;
						}else{
							if(config.getBoolean("Settings.KnockBack")){
								knockBack(player, block.getLocation());
							}
							String msg = config.getString("Settings.NoKeyMsg");
							msg = msg.replaceAll("%Key%", KeyName);
							msg = msg.replaceAll("%key%", KeyName);
							player.sendMessage(Methods.color(Methods.getPrefix()+msg));
							return;
						}
					}
				}
			}
		}
	}
	
	void openCrate(Player player, String Crate, Location loc){
		String C = Main.settings.getFile(Crate).getString("Crate.CrateType");
		if(C.equalsIgnoreCase("Wheel")){
			Wheel.startWheel(player);
		}
		if(C.equalsIgnoreCase("Wonder")){
			Wonder.startWonder(player);
		}
		if(C.equalsIgnoreCase("Cosmic")){
			Cosmic.openCosmic(player);
		}
		if(C.equalsIgnoreCase("CSGO")){
			CSGO.openCSGO(player);
		}
		if(C.equalsIgnoreCase("CrateOnTheGo")){
			if(Methods.Key.get(player) == KeyType.PHYSICAL_KEY){
				Methods.removeItem(Key.get(player), player);
			}
			GUI.Crate.put(player, Crate);
			if(!CC.Rewards.containsKey(player)){
				CC.getItems(player);
			}
			ItemStack it = pickItem(player);
			String path = Rewards.get(player).get(it);
			getReward(player, path);
			if(Main.settings.getFile(GUI.Crate.get(player)).getBoolean(path + ".Firework")){
				Methods.fireWork(player.getLocation().add(0, 1, 0));
			}
			GUI.Crate.remove(player);
			return;
		}
		if(C.equalsIgnoreCase("Roulette")){
			Roulette.openRoulette(player);
		}
		if(C.equalsIgnoreCase("QuadCrate")){
			LastLoc.put(player, player.getLocation());
			QCC.startBuild(player, loc, Material.CHEST);
		}
		if(C.equalsIgnoreCase("QuickCrate")){
			if(InUse.containsValue(loc)){
				String msg = Main.settings.getConfig().getString("Settings.QuickCrateInUse");
				player.sendMessage(Methods.color(Methods.getPrefix()+msg));
				return;
			}else{
				QuickCrate.openCrate(player, loc, true);
				InUse.put(player, loc);
				return;
			}
		}
		if(C.equalsIgnoreCase("FireCracker")){
			if(InUse.containsValue(loc)){
				String msg = Main.settings.getConfig().getString("Settings.QuickCrateInUse");
				player.sendMessage(Methods.color(Methods.getPrefix()+msg));
				return;
			}else{
				FireCracker.startFireCracker(player, Crate, loc);
				InUse.put(player, loc);
				return;
			}
		}
	}
	
	void knockBack(Player player, Location loc){
		Vector v = player.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(1).setY(.1);
		if(player.isInsideVehicle()){
			player.getVehicle().setVelocity(v);
			return;
		}
		player.setVelocity(v);
	}
	
	public static void getItems(Player player){
		FileConfiguration file = Main.settings.getFile(CC.Crate.get(player));
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		HashMap<ItemStack, String> path = new HashMap<ItemStack, String>();
		for(String reward : file.getConfigurationSection("Crate.Prizes").getKeys(false)){
			String id = file.getString("Crate.Prizes." + reward + ".DisplayItem");
			String name = file.getString("Crate.Prizes." + reward + ".DisplayName");
			try{
				items.add(Methods.makeItem(id, 1, name));
				path.put(Methods.makeItem(id, 1, name), "Crate.Prizes."+reward);
			}catch(Exception e){
				continue;
			}
		}
		Rewards.put(player, path);
	}
	
	public static ItemStack pickItem(Player player){
		FileConfiguration file = Main.settings.getFile(CC.Crate.get(player));
		if(!Rewards.containsKey(player)){
			getItems(player);
		}
		Set<ItemStack> items = Rewards.get(player).keySet();
		ArrayList<ItemStack> Items = new ArrayList<ItemStack>();
		Random r = new Random();
		for(int stop=0;Items.size()==0;stop++){
			if(stop==100){
				break;
			}
			for(ItemStack i : items){
				String path = Rewards.get(player).get(i);
				ItemStack item = Methods.makeItem(file.getString(path+".DisplayItem"), 1, file.getString(path+".DisplayName"));
				if(file.contains(path + ".Glowing")){
					if(file.getBoolean(path + ".Glowing")){
						item = Methods.addGlow(item);
					}
				}
				int max = file.getInt(path+".MaxRange");
				int chance = file.getInt(path+".Chance");
				int num;
				for(int counter = 1; counter<=1; counter++){
					num = 1 + r.nextInt(max);
					if(num >= 1 && num <= chance){
						Items.add(item);
					}
				}
			}
		}
		return Items.get(r.nextInt(Items.size()));
	}
	
	public static ItemStack pickItem(Player player, Location loc){
		FileConfiguration file = Main.settings.getFile(GUI.Crate.get(player));
		if(!Rewards.containsKey(player)){
			getItems(player);
		}
		Set<ItemStack> items = Rewards.get(player).keySet();
		ArrayList<ItemStack> Items = new ArrayList<ItemStack>();
		Random r = new Random();
		for(int stop=0;Items.size()==0;stop++){
			if(stop==100){
				break;
			}
			for(ItemStack i : items){
				String path = Rewards.get(player).get(i);
				ItemStack item = Methods.makeItem(file.getString(path+".DisplayItem"), 1, file.getString(path+".DisplayName"));
				int max = file.getInt(path+".MaxRange");
				int chance = file.getInt(path+".Chance");
				int num;
				for(int counter = 1; counter<=1; counter++){
					num = 1 + r.nextInt(max);
					if(num >= 1 && num <= chance)Items.add(item);
				}
			}
		}
		ItemStack item = Items.get(r.nextInt(Items.size()));
		if(file.getBoolean("Crate.Prizes."+Rewards.get(player).get(item)+".Firework")){
			Methods.fireWork(loc);
		}
		return item;
	}
	
	public static void getReward(Player player, String path){
		FileConfiguration file = Main.settings.getFile(GUI.Crate.get(player));
		if(file.contains(path + ".Items")){
			for(ItemStack i : Methods.getFinalItems(path, player)){
				if(!Methods.isInvFull(player)){
					player.getInventory().addItem(i);
				}else{
					player.getWorld().dropItemNaturally(player.getLocation(), i);
				}
			}
		}
		if(file.contains(path + ".Commands")){
			for(String command : file.getStringList(path + ".Commands")){
				command = Methods.color(command);
				command = command.replace("%Player%", player.getName());
				command = command.replace("%player%", player.getName());
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
			}
		}
		if(file.contains(path + ".Messages")){
			for(String msg : file.getStringList(path + ".Messages")){
				msg = Methods.color(msg);
				msg = msg.replace("%Player%", player.getName());
				msg = msg.replace("%player%", player.getName());
				player.sendMessage(msg);
			}
		}
	}
	
}