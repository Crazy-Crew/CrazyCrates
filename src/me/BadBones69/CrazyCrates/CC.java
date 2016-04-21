package me.BadBones69.CrazyCrates;

import java.util.HashMap;

import me.BadBones69.CrazyCrates.CrateTypes.CSGO;
import me.BadBones69.CrazyCrates.CrateTypes.FireCracker;
import me.BadBones69.CrazyCrates.CrateTypes.QCC;
import me.BadBones69.CrazyCrates.CrateTypes.QuickCrate;
import me.BadBones69.CrazyCrates.CrateTypes.Roulette;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class CC implements Listener{ //Crate Control
	public static HashMap<Player, ItemStack> Key = new HashMap<Player, ItemStack>();
	public static HashMap<Player, Location> LastLoc = new HashMap<Player, Location>();
	public static HashMap<Player, Location> InUse = new HashMap<Player, Location>();
	@EventHandler
	public void onCrateOpen(PlayerInteractEvent e){
		Player player = e.getPlayer();
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK||e.getAction() == Action.LEFT_CLICK_BLOCK){
			Block block = e.getClickedBlock();
			for(String crate : Main.settings.getAllCratesNames()){
				String KeyName = Api.color(Main.settings.getFile(crate).getString("Crate.PhysicalKey.Name"));
				if(e.hasItem()){
					ItemStack item = new ItemStack(Material.AIR);
					if(Api.getVersion()==183){
						item = OnePointEight.getInHand(player);
					}
					if(Api.getVersion()==191){
						item = OnePointNine.getInHand(player);
					}
					if(item.hasItemMeta()){
						if(item.getItemMeta().hasDisplayName()){
							if(item.getItemMeta().getDisplayName().equals(KeyName)){
								e.setCancelled(true);
							}
						}
					}
				}
			}
			if(Main.settings.getLocations().getConfigurationSection("Locations")==null){
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
					String KeyName = Api.color(Main.settings.getFile(Crate).getString("Crate.PhysicalKey.Name"));
					if(e.hasItem()){
						ItemStack item = new ItemStack(Material.AIR);
						String ver = Bukkit.getServer().getClass().getPackage().getName();
						ver = ver.substring(ver.lastIndexOf('.')+1);
						if(Api.getVersion()==183){
							item = OnePointEight.getInHand(player);
						}
						if(Api.getVersion()==191){
							item = OnePointNine.getInHand(player);
						}
						if(item.hasItemMeta()){
							if(item.getItemMeta().hasDisplayName()){
								if(item.getItemMeta().getDisplayName().equals(KeyName)){
									if(GUI.Crate.containsKey(player)){
										String msg = Main.settings.getConfig().getString("Settings.AlreadyOpeningCrateMsg");
										msg = msg.replaceAll("%Key%", KeyName);
										msg = msg.replaceAll("%key%", KeyName);
										player.sendMessage(Api.color(Api.getPrefix()+msg));
										return;
									}
									GUI.Crate.put(player, Crate);
									Key.put(player, item);
									Api.Key.put(player, "PhysicalKey");
									openCrate(player, Crate, loc);
									return;
								}
							}
						}
					}
					knockBack(player, block.getLocation());
					String msg = Main.settings.getConfig().getString("Settings.NoKeyMsg");
					msg = msg.replaceAll("%Key%", KeyName);
					msg = msg.replaceAll("%key%", KeyName);
					player.sendMessage(Api.color(Api.getPrefix()+msg));
					return;
				}
			}
		}
	}
	void openCrate(Player player, String Crate, Location loc){
		String C = Main.settings.getFile(Crate).getString("Crate.CrateType");
		if(C.equalsIgnoreCase("CSGO")){
			CSGO.openCSGO(player);
		}
		if(C.equalsIgnoreCase("Roulette")){
			Roulette.openRoulette(player);
		}
		if(C.equalsIgnoreCase("QuadCrate")){
			LastLoc.put(player, player.getLocation());
			player.teleport(loc.add(.5,0,.5));
			QCC.startBuild(player, loc, Material.CHEST);
		}
		if(C.equalsIgnoreCase("QuickCrate")){
			if(InUse.containsValue(loc)){
				String msg = Main.settings.getConfig().getString("Settings.QuickCrateInUse");
				player.sendMessage(Api.color(Api.getPrefix()+msg));
				return;
			}
			if(!InUse.containsValue(loc)){
				QuickCrate.openCrate(player, loc, Crate, true);
				InUse.put(player, loc);
				return;
			}
		}
		if(C.equalsIgnoreCase("FireCracker")){
			FireCracker.startFireCracker(player, Crate, loc);
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
}