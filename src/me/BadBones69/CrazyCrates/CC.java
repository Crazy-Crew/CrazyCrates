package me.BadBones69.CrazyCrates;

import java.util.HashMap;

import me.BadBones69.CrazyCrates.CrateTypes.CSGO;
import me.BadBones69.CrazyCrates.CrateTypes.QCC;

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
	@EventHandler
	public void onCrateOpen(PlayerInteractEvent e){
		Player player = e.getPlayer();
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK||e.getAction() == Action.LEFT_CLICK_BLOCK){
			Block block = e.getClickedBlock();
			for(String crate : Main.settings.getAllCratesNames()){
				String KeyName = Api.color(Main.settings.getFile(crate).getString("Crate.PhysicalKey.Name"));
				if(e.hasItem()){
					ItemStack item = player.getItemInHand();
					if(item.hasItemMeta()){
						if(item.getItemMeta().hasDisplayName()){
							if(item.getItemMeta().getDisplayName().equals(KeyName)){
								e.setCancelled(true);
							}
						}
					}
				}
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
						ItemStack item = player.getItemInHand();
						if(item.hasItemMeta()){
							if(item.getItemMeta().hasDisplayName()){
								if(item.getItemMeta().getDisplayName().equals(KeyName)){
									if(GUI.Crate.containsKey(player)){
										player.sendMessage(Api.color(Api.getPrefix()+"&cYou are already opening a crate."));
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
					player.sendMessage(Api.color(Api.getPrefix()+"&cYou must have a "+KeyName+" &cin your hand to use that Crate."));
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
		if(C.equalsIgnoreCase("QuadCrate")){
			LastLoc.put(player, player.getLocation());
			player.teleport(loc.add(.5,0,.5));
			QCC.startBuild(player, loc, Material.CHEST);
		}
	}
	void knockBack(Player player, Location loc){
		Vector v = player.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(1).setY(.1);
		player.setVelocity(v);
	}
}