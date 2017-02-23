package me.BadBones69.CrazyCrates.CrateTypes;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.BadBones69.CrazyCrates.Methods;
import me.BadBones69.CrazyCrates.CrateControl;
import me.BadBones69.CrazyCrates.GUI;
import me.BadBones69.CrazyCrates.Main;
import me.BadBones69.CrazyCrates.API.CrateType;
import me.BadBones69.CrazyCrates.API.CrazyCrates;
import me.BadBones69.CrazyCrates.API.PlayerPrizeEvent;

public class CrateOnTheGo implements Listener{
	
	private static CrazyCrates CC = CrazyCrates.getInstance();
	
	public static void giveCrate(Player player, int amount, String crate){
		String path = "Crate.";
		String ma = Main.settings.getFile(crate).getString(path+"Item");
		int type = 0;
		if(ma.contains(":")){
			String[] b = ma.split(":");
			ma = b[0];
			type = Integer.parseInt(b[1]);
		}
		String name = Main.settings.getFile(crate).getString(path+"Name");
		ArrayList<String> lore = new ArrayList<String>();
		for(String i : Main.settings.getFile(crate).getStringList(path+"Lore")){
			i=i.replaceAll("%Keys%", Methods.getKeys(player, crate)+"");
			i=i.replaceAll("%keys%", Methods.getKeys(player, crate)+"");
			i=i.replaceAll("%Player%", player.getName());
			i=i.replaceAll("%player%", player.getName());
			lore.add(i);
		}
		player.getInventory().addItem(Methods.makeItem(Material.matchMaterial(ma), amount, type, name, lore));
	}
	
	@EventHandler
	public void onCrateOpen(PlayerInteractEvent e){
		Player player = e.getPlayer();
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(e.hasItem()){
				ItemStack item = e.getItem();
				if(item.hasItemMeta()){
					if(item.getItemMeta().hasDisplayName()){
						for(String crate : Main.settings.getAllCratesNames()){
							if(Main.settings.getFile(crate).getString("Crate.CrateType").equalsIgnoreCase("CrateOnTheGo")){
								if(item.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getFile(crate).getString("Crate.Name")))){
									e.setCancelled(true);
									GUI.Crate.put(player, crate);
									if(!CrateControl.Rewards.containsKey(player)){
										CC.getItems(player);
									}
									Methods.removeItem(item, player);
									ItemStack it = CC.pickItem(player);
									String path = CrateControl.Rewards.get(player).get(it);
									CC.getReward(player, path);
									Bukkit.getPluginManager().callEvent(new PlayerPrizeEvent(player, CrateType.CRATE_ON_THE_GO, CrateControl.Crate.get(player), path.replace("Crate.Prizes.", "")));
									if(Main.settings.getFile(GUI.Crate.get(player)).getBoolean(path + ".Firework")){
										Methods.fireWork(player.getLocation().add(0, 1, 0));
									}
									GUI.Crate.remove(player);
									CrateControl.Rewards.remove(player);
									return;
								}
							}
						}
					}
				}
			}
		}
	}
	
}