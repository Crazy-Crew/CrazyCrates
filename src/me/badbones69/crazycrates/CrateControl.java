package me.badbones69.crazycrates;

import java.util.HashMap;

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
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import me.badbones69.crazycrates.api.Crate;
import me.badbones69.crazycrates.api.KeyType;
import me.badbones69.crazycrates.cratetypes.CrateOnTheGo;

public class CrateControl implements Listener{ //Crate Control
	
	/**
	 * The keys for the /CE Admin menu.
	 */
	public static HashMap<Player, HashMap<ItemStack, ItemStack>> previewKeys = new HashMap<Player, HashMap<ItemStack, ItemStack>>();
	/**
	 * The crates that players are in.
	 */
	public static HashMap<Player, Crate> Crate = new HashMap<Player, Crate>();
	/**
	 * The key itemstack for the crate to remove 1 when used.
	 */
	public static HashMap<Player, ItemStack> Key = new HashMap<Player, ItemStack>();
	/**
	 * The last location for the quad crate to teleport players back.
	 */
	public static HashMap<Player, Location> LastLoc = new HashMap<Player, Location>();
	/**
	 * A list of crate locations that are in use.
	 */
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
				String crate = Main.settings.getLocations().getString("Locations."+location+".Crate");
				World w = Bukkit.getWorld(Main.settings.getLocations().getString("Locations."+location+".World"));
				int x = Main.settings.getLocations().getInt("Locations." + location + ".X");
				int y = Main.settings.getLocations().getInt("Locations." + location + ".Y");
				int z = Main.settings.getLocations().getInt("Locations." + location + ".Z");
				Location loc = new Location(w, x, y, z);
				if(block.getLocation().equals(loc)){
					e.setCancelled(true);
					if(crate.equalsIgnoreCase("Menu")){
						return;
					}else{
						if(config.getBoolean("Settings.Show-Preview")){
							for(Crate c : Main.CC.getCrates()){
								if(c.getName().equalsIgnoreCase(crate)){
									GUI.openPreview(player, c);
								}
							}
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
			if(Main.settings.getLocations().contains("Locations")){
				for(String location : Main.settings.getLocations().getConfigurationSection("Locations").getKeys(false)){
					Block block = e.getClickedBlock();
					String crate = Main.settings.getLocations().getString("Locations."+location+".Crate");
					World w = Bukkit.getWorld(Main.settings.getLocations().getString("Locations."+location+".World"));
					int x = Main.settings.getLocations().getInt("Locations."+location+".X");
					int y = Main.settings.getLocations().getInt("Locations."+location+".Y");
					int z = Main.settings.getLocations().getInt("Locations."+location+".Z");
					Location loc = new Location(w, x, y, z);
					if(block.getLocation().equals(loc)){
						e.setCancelled(true);
						if(crate.equalsIgnoreCase("Menu")){
							player.performCommand("crazycrates");
							return;
						}
						String KeyName = Methods.color(Main.settings.getFile(crate).getString("Crate.PhysicalKey.Name"));
						ItemStack key = null;
						Boolean hasKey = false;
						Boolean isPhysical = false;
						if(Methods.getItemInHand(player) != null){
							key = Methods.getItemInHand(player);
							for(Crate c : Main.CC.getCrates()){
								if(c.getName().equalsIgnoreCase(crate)){
									if(Methods.isSimilar(key, Methods.getKey(c))){
										hasKey = true;
										isPhysical = true;
									}
								}
							}
							
						}
						if(config.getBoolean("Settings.Physical-Accepts-Virtual-Keys")){
							for(Crate c : Main.CC.getCrates()){
								if(c.getName().equalsIgnoreCase(crate)){
									if(Methods.getKeys(player, c) >= 1){
										hasKey = true;
									}
								}
							}
						}
						if(hasKey){
							if(GUI.crates.containsKey(player)){
								player.sendMessage(Methods.color(Methods.getPrefix() + config.getString("Settings.AlreadyOpeningCrateMsg")
									.replaceAll("%Key%", KeyName).replaceAll("%key%", KeyName)));
								return;
							}
							if(InUse.containsValue(loc)){
								player.sendMessage(Methods.color(Methods.getPrefix() + config.getString("Settings.QuickCrateInUse")));
								return;
							}
							if(Methods.isInvFull(player)){
								if(config.contains("Settings.Inventory-Full")){
									player.sendMessage(Methods.color(Methods.getPrefix() + config.getString("Settings.Inventory-Full")));
								}else{
									player.sendMessage(Methods.color(Methods.getPrefix() + "&cYour inventory is full, please make room before opening a crate."));
								}
								return;
							}
							for(Crate c : Main.CC.getCrates()){
								if(c.getName().equalsIgnoreCase(crate)){
									GUI.crates.put(player, c);
								}
							}
							if(isPhysical){
								Key.put(player, key);
								Methods.Key.put(player, KeyType.PHYSICAL_KEY);
							}else{
								Methods.Key.put(player, KeyType.VIRTUAL_KEY);
								LastLoc.put(player, player.getLocation());
							}
							for(Crate c : Main.CC.getCrates()){
								if(c.getName().equalsIgnoreCase(crate)){
									Crate.put(player, c);
									Main.CC.openCrate(player, c.getCrateType(), loc);
								}
							}
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
	
	@EventHandler
	public void onInvClick(InventoryClickEvent e){
		Inventory inv = e.getInventory();
		Player player = (Player)e.getWhoClicked();
		if(inv!=null){
			if(inv.getName().equals(Methods.color("&4&lAdmin Keys"))){
				e.setCancelled(true);
				if(!Methods.permCheck(player, "Admin")){
					player.closeInventory();
					return;
				}
				int slot = e.getRawSlot();
				if(slot<inv.getSize()){
					if(e.getAction() == InventoryAction.PICKUP_ALL){
						ItemStack item = inv.getItem(slot);
						if(item != null){
							if(item.getType()!=Material.AIR){
								for(Crate crate : Main.CC.getCrates()){
									if(crate.getFile().getString("Crate.CrateType").equalsIgnoreCase("CrateOnTheGo")){
										CrateOnTheGo.giveCrate(player, 1, crate);
										return;
									}
								}
								player.getInventory().addItem(previewKeys.get(player).get(item));
							}
						}
					}
					if(e.getAction() == InventoryAction.PICKUP_HALF){
						ItemStack item = inv.getItem(slot);
						for(Crate crate : Main.CC.getCrates()){
							String name = crate.getFile().getString("Crate.PhysicalKey.Name");
							if(item.getItemMeta().getDisplayName().equals(Methods.color(name))){
								Methods.addKeys(1, player, crate, KeyType.VIRTUAL_KEY);
								player.sendMessage(Methods.getPrefix()+Methods.color("&a&l+1 " + name));
								return;
							}
						}
					}
				}
			}
		}
	}
	
	private void knockBack(Player player, Location loc){
		Vector v = player.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(1).setY(.1);
		if(player.isInsideVehicle()){
			player.getVehicle().setVelocity(v);
			return;
		}
		player.setVelocity(v);
	}
	
}