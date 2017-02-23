package me.BadBones69.CrazyCrates.API;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.BadBones69.CrazyCrates.CrateControl;
import me.BadBones69.CrazyCrates.GUI;
import me.BadBones69.CrazyCrates.Main;
import me.BadBones69.CrazyCrates.Methods;
import me.BadBones69.CrazyCrates.CrateTypes.CSGO;
import me.BadBones69.CrazyCrates.CrateTypes.Cosmic;
import me.BadBones69.CrazyCrates.CrateTypes.FireCracker;
import me.BadBones69.CrazyCrates.CrateTypes.QCC;
import me.BadBones69.CrazyCrates.CrateTypes.QuickCrate;
import me.BadBones69.CrazyCrates.CrateTypes.Roulette;
import me.BadBones69.CrazyCrates.CrateTypes.Wheel;
import me.BadBones69.CrazyCrates.CrateTypes.Wonder;

public class CrazyCrates {
	
	private static CrazyCrates instance = new CrazyCrates();
	
	public static CrazyCrates getInstance(){
		return instance;
	}
	
	public void openCrate(Player player, String crate, Location loc){
		switch(CrateType.getFromName(Main.settings.getFile(crate).getString("Crate.CrateType"))){
			case MENU:
				GUI.openGUI(player);
				break;
			case COSMIC:
				Cosmic.openCosmic(player);
				break;
			case CSGO:
				CSGO.openCSGO(player);
				break;
			case ROULETTE:
				Roulette.openRoulette(player);
				break;
			case WHEEL:
				Wheel.startWheel(player);
				break;
			case WONDER:
				Wonder.startWonder(player);
				break;
			case QUAD_CRATE:
				CrateControl.LastLoc.put(player, player.getLocation());
				QCC.startBuild(player, loc, Material.CHEST);
				break;
			case FIRE_CRACKER:
				if(CrateControl.InUse.containsValue(loc)){
					player.sendMessage(Methods.color(Methods.getPrefix() + Main.settings.getConfig().getString("Settings.QuickCrateInUse")));
				}else{
					FireCracker.startFireCracker(player, crate, loc);
					CrateControl.InUse.put(player, loc);
				}
				break;
			case QUICK_CRATE:
				if(CrateControl.InUse.containsValue(loc)){
					player.sendMessage(Methods.color(Methods.getPrefix() + Main.settings.getConfig().getString("Settings.QuickCrateInUse")));
				}else{
					QuickCrate.openCrate(player, loc, true);
					CrateControl.InUse.put(player, loc);
				}
				break;
			case CRATE_ON_THE_GO:
				if(Methods.Key.get(player) == KeyType.PHYSICAL_KEY){
					Methods.removeItem(CrateControl.Key.get(player), player);
				}
				GUI.Crate.put(player, crate);
				if(!CrateControl.Rewards.containsKey(player)){
					getItems(player);
				}
				ItemStack it = pickItem(player);
				String path = CrateControl.Rewards.get(player).get(it);
				getReward(player, path);
				if(Main.settings.getFile(GUI.Crate.get(player)).getBoolean(path + ".Firework")){
					Methods.fireWork(player.getLocation().add(0, 1, 0));
				}
				GUI.Crate.remove(player);
				break;
		}
	}
	
	public void getReward(Player player, String path){
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
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Methods.color(command
						.replace("%Player%", player.getName()).replace("%player%", player.getName())));
			}
		}
		if(file.contains(path + ".Messages")){
			for(String msg : file.getStringList(path + ".Messages")){
				player.sendMessage(Methods.color(msg)
						.replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName()));
			}
		}
	}
	
	public ItemStack pickItem(Player player){
		FileConfiguration file = Main.settings.getFile(CrateControl.Crate.get(player));
		if(!CrateControl.Rewards.containsKey(player)){
			getItems(player);
		}
		Set<ItemStack> items = CrateControl.Rewards.get(player).keySet();
		ArrayList<ItemStack> Items = new ArrayList<ItemStack>();
		Random r = new Random();
		for(int stop = 0; Items.size() == 0; stop++){
			if(stop == 100){
				break;
			}
			for(ItemStack i : items){
				String path = CrateControl.Rewards.get(player).get(i);
				ItemStack item = Methods.makeItem(file.getString(path + ".DisplayItem"), 1, file.getString(path + ".DisplayName"));
				if(file.contains(path + ".Glowing")){
					if(file.getBoolean(path + ".Glowing")){
						item = Methods.addGlow(item);
					}
				}
				int max = file.getInt(path + ".MaxRange");
				int chance = file.getInt(path + ".Chance");
				int num;
				for(int counter = 1; counter <= 1; counter++){
					num = 1 + r.nextInt(max);
					if(num >= 1 && num <= chance){
						Items.add(item);
					}
				}
			}
		}
		return Items.get(r.nextInt(Items.size()));
	}
	
	public ItemStack pickItem(Player player, Location loc){
		FileConfiguration file = Main.settings.getFile(GUI.Crate.get(player));
		if(!CrateControl.Rewards.containsKey(player)){
			getItems(player);
		}
		Set<ItemStack> items = CrateControl.Rewards.get(player).keySet();
		ArrayList<ItemStack> Items = new ArrayList<ItemStack>();
		Random r = new Random();
		for(int stop = 0; Items.size() == 0; stop++){
			if(stop == 100){
				break;
			}
			for(ItemStack i : items){
				String path = CrateControl.Rewards.get(player).get(i);
				ItemStack item = Methods.makeItem(file.getString(path + ".DisplayItem"), 1, file.getString(path + ".DisplayName"));
				int max = file.getInt(path + ".MaxRange");
				int chance = file.getInt(path + ".Chance");
				int num;
				for(int counter = 1; counter <= 1; counter++){
					num = 1 + r.nextInt(max);
					if(num >= 1 && num <= chance)Items.add(item);
				}
			}
		}
		ItemStack item = Items.get(r.nextInt(Items.size()));
		if(file.getBoolean("Crate.Prizes." + CrateControl.Rewards.get(player).get(item) + ".Firework")){
			Methods.fireWork(loc);
		}
		return item;
	}
	
	public void getItems(Player player){
		FileConfiguration file = Main.settings.getFile(CrateControl.Crate.get(player));
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
		CrateControl.Rewards.put(player, path);
	}
	
}