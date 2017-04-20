package me.badbones69.crazycrates.cratetypes;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.badbones69.crazycrates.CrateControl;
import me.badbones69.crazycrates.GUI;
import me.badbones69.crazycrates.Main;
import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.Crate;
import me.badbones69.crazycrates.api.CrateType;
import me.badbones69.crazycrates.api.PlayerPrizeEvent;
import me.badbones69.crazycrates.api.Prize;

public class CrateOnTheGo implements Listener{
	
	public static void giveCrate(Player player, int amount, Crate crate){
		FileConfiguration file = crate.getFile();
		String path = "Crate.";
		String ma = file.getString(path+"Item");
		int type = 0;
		if(ma.contains(":")){
			String[] b = ma.split(":");
			ma = b[0];
			type = Integer.parseInt(b[1]);
		}
		String name = file.getString(path + "Name");
		ArrayList<String> lore = new ArrayList<String>();
		for(String i : file.getStringList(path+"Lore")){
			lore.add(i.replaceAll("%Keys%", Methods.getKeys(player, crate)+"").replaceAll("%keys%", Methods.getKeys(player, crate)+"")
					.replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName()));
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
						for(Crate crate : Main.CC.getCrates()){
							if(crate.getFile().getString("Crate.CrateType").equalsIgnoreCase("CrateOnTheGo")){
								if(item.getItemMeta().getDisplayName().equals(Methods.color(crate.getFile().getString("Crate.Name")))){
									e.setCancelled(true);
									GUI.crates.put(player, crate);
									Methods.removeItem(item, player);
									Prize prize = Main.CC.pickPrize(player);
									Main.CC.getReward(player, prize);
									Bukkit.getPluginManager().callEvent(new PlayerPrizeEvent(player, CrateType.CRATE_ON_THE_GO, CrateControl.Crate.get(player).getName(), prize));
									if(prize.toggleFirework()){
										Methods.fireWork(player.getLocation().add(0, 1, 0));
									}
									GUI.crates.remove(player);
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