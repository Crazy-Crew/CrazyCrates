package me.BadBones69.CrazyCrates.CrateTypes;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.BadBones69.CrazyCrates.Api;
import me.BadBones69.CrazyCrates.CC;
import me.BadBones69.CrazyCrates.GUI;
import me.BadBones69.CrazyCrates.Main;

public class CrateOnTheGo implements Listener{
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
			i=i.replaceAll("%Keys%", Api.getKeys(player, crate)+"");
			i=i.replaceAll("%keys%", Api.getKeys(player, crate)+"");
			i=i.replaceAll("%Player%", player.getName());
			i=i.replaceAll("%player%", player.getName());
			lore.add(i);
		}
		player.getInventory().addItem(Api.makeItem(Material.matchMaterial(ma), amount, type, name, lore));
	}
	@EventHandler
	public void onCrateOpen(PlayerInteractEvent e){
		Player player = e.getPlayer();
		if(e.getAction()==Action.RIGHT_CLICK_BLOCK){
			if(e.hasItem()){
				ItemStack item = e.getItem();
				if(item.hasItemMeta()){
					if(item.getItemMeta().hasDisplayName()){
						for(String crate : Main.settings.getAllCratesNames()){
							String name = Main.settings.getFile(crate).getString("Crate.Name");
							name = Api.color(name);
							if(item.getItemMeta().getDisplayName().equals(name)){
								e.setCancelled(true);
								GUI.Crate.put(player, crate);
								if(!CC.Rewards.containsKey(player)){
									CC.getItems(player);
								}
								Api.removeItem(item, player);
								ItemStack it = CC.pickItem(player);
								String path = CC.Rewards.get(player).get(it);
								CC.getReward(player, path);
								if(Main.settings.getFile(GUI.Crate.get(player)).getBoolean(path + ".Firework")){
									Api.fireWork(player.getLocation().add(0, 1, 0));
								}
								GUI.Crate.remove(player);
								CC.Rewards.remove(player);
								return;
							}
						}
					}
				}
			}
		}
	}
}