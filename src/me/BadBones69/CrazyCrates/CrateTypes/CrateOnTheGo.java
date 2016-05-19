package me.BadBones69.CrazyCrates.CrateTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import me.BadBones69.CrazyCrates.Api;
import me.BadBones69.CrazyCrates.GUI;
import me.BadBones69.CrazyCrates.Main;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

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
								GUI.Crate.put(player, crate);
								e.setCancelled(true);
								Api.removeItem(item, player);
								name = pickItem(player).getItemMeta().getDisplayName();
								for(String reward : Main.settings.getFile(crate).getConfigurationSection("Crate.Prizes").getKeys(false)){
									if(name.equals(Api.color(Main.settings.getFile(crate).getString("Crate.Prizes."+reward+".DisplayName")))){
										getReward(player, reward);
										if(Main.settings.getFile(GUI.Crate.get(player)).getBoolean(Api.path.get(player) + ".Firework")){
											Api.fireWork(player.getLocation().add(0, 1, 0));
										}
										GUI.Crate.remove(player);
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
	public static ItemStack pickItem(Player player){
		HashMap<ItemStack, String> items = Api.getItems(player);
		int stop = 0;
		for(;items.size()==0;stop++){
			if(stop==100){
				break;
			}
			items=Api.getItems(player);
		}
		Random r = new Random();
		ArrayList<ItemStack> I = new ArrayList<ItemStack>();
		ArrayList<String> P = new ArrayList<String>();
		I.addAll(items.keySet());
		for(ItemStack it : I){
			P.add(items.get(it));
		}
		int pick = r.nextInt(I.size());
		String pa = P.get(pick);
		Api.path.put(player, pa);
		return I.get(pick);
	}
	public static void getReward(Player player, String reward){
		reward = "Crate.Prizes."+reward;
		Api.path.put(player, reward);
		if(Main.settings.getFile(GUI.Crate.get(player)).contains(Api.path.get(player) + ".Items")){
			for(ItemStack i : Api.getFinalItems(Api.path.get(player), player)){
				player.getInventory().addItem(i);
			}
		}
		if(Main.settings.getFile(GUI.Crate.get(player)).contains(Api.path.get(player) + ".Commands")){
			for(String command : Main.settings.getFile(GUI.Crate.get(player)).getStringList(Api.path.get(player) + ".Commands")){
				command = Api.color(command);
				command = command.replace("%Player%", player.getName());
				command = command.replace("%player%", player.getName());
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
			}
		}
	}
}