package me.BadBones69.CrazyCrates;

import java.util.ArrayList;
import java.util.HashMap;

import me.BadBones69.CrazyCrates.CrateTypes.CSGO;
import me.BadBones69.CrazyCrates.CrateTypes.QCC;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GUI implements Listener{
	public static HashMap<Player, String> Crate = new HashMap<Player, String>();
	static void openGUI(Player player){
		Inventory inv = Bukkit.createInventory(null, Main.settings.getConfig().getInt("Settings.InventorySize"), Api.color(Main.settings.getConfig().getString("Settings.InventoryName")));
		for(String crate : Main.settings.getAllCratesNames()){
			String path = "Crate.";
			int slot = Main.settings.getFile(crate).getInt(path+"Slot")+1;
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
			inv.setItem(slot, Api.makeItem(Material.matchMaterial(ma), 1, type, name, lore));
		}
		player.openInventory(inv);
	}
	@EventHandler
	public void onInvClick(InventoryClickEvent e){
		Player player = (Player) e.getWhoClicked();
		Inventory inv = e.getInventory();
		if(inv!=null){
			if(inv.getName().equals(Api.color(Main.settings.getConfig().getString("Settings.InventoryName")))){
				e.setCancelled(true);
				if(e.getCurrentItem()!=null){
					ItemStack item = e.getCurrentItem();
					if(item.hasItemMeta()){
						if(item.getItemMeta().hasDisplayName()){
							for(String crate : Main.settings.getAllCratesNames()){
								String path = "Crate.";
								if(item.getItemMeta().getDisplayName().equals(Api.color(Main.settings.getFile(crate).getString(path+"Name")))){
									player.closeInventory();
									if(Crate.containsKey(player)){
										player.sendMessage(Api.color(Api.getPrefix()+"&cYou are already opening a crate."));
										return;
									}
									if(Api.getKeys(player, crate)<1){
										player.sendMessage(Api.color(Api.getPrefix()+"&cYou need a key to open that Crate."));
										return;
									}
									Crate.put(player, crate);
									if(Main.settings.getFile(crate).getString("Crate.CrateType").equalsIgnoreCase("QuadCrate")){
										QCC.startBuild(player, player.getLocation(), Material.CHEST);
									}
									if(Main.settings.getFile(crate).getString("Crate.CrateType").equalsIgnoreCase("CSGO")){
										CSGO.openCSGO(player);
										if(Main.settings.getFile(GUI.Crate.get(player)).getBoolean("Crate.OpeningBroadCast")){
											String msg = Api.color(Main.settings.getFile(GUI.Crate.get(player)).getString("Crate.BroadCast"));
											msg = msg.replaceAll("%Prefix%", Api.getPrefix());
											msg = msg.replaceAll("%prefix%", Api.getPrefix());
											msg = msg.replaceAll("%Player%", player.getName());
											msg = msg.replaceAll("%player%", player.getName());
											Bukkit.broadcastMessage(msg);
										}
										Api.takeKeys(1, player, crate);
									}
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