package me.badbones69.crazycrates.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.badbones69.crazycrates.CrateControl;
import me.badbones69.crazycrates.GUI;
import me.badbones69.crazycrates.Main;
import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.cratetypes.CSGO;
import me.badbones69.crazycrates.cratetypes.Cosmic;
import me.badbones69.crazycrates.cratetypes.FireCracker;
import me.badbones69.crazycrates.cratetypes.QCC;
import me.badbones69.crazycrates.cratetypes.QuickCrate;
import me.badbones69.crazycrates.cratetypes.Roulette;
import me.badbones69.crazycrates.cratetypes.War;
import me.badbones69.crazycrates.cratetypes.Wheel;
import me.badbones69.crazycrates.cratetypes.Wonder;

public class CrazyCrates {
	
	private static CrazyCrates instance = new CrazyCrates();
	private ArrayList<Crate> crates = new ArrayList<Crate>();
	
	public static CrazyCrates getInstance(){
		return instance;
	}
	
	public void loadCrates(){
		crates.clear();
		Bukkit.getLogger().log(Level.INFO, "[Crazy Crates]>> Loading all crate information...");
		for(String crateName : Main.settings.getAllCratesNames()){
			Bukkit.getLogger().log(Level.INFO, "[Crazy Crates]>> Loading " + crateName + ".yml information....");
			try{
				FileConfiguration file = Main.settings.getFile(crateName);
				ArrayList<Prize> prizes = new ArrayList<Prize>();
				for(String prize : file.getConfigurationSection("Crate.Prizes").getKeys(false)){
					ArrayList<String> msgs = new ArrayList<String>();
					ArrayList<String> commands = new ArrayList<String>();
					for(String msg : file.getStringList("Crate.Prizes." + prize + ".Messages")){
						msgs.add(msg);
					}
					for(String cmd : file.getStringList("Crate.Prizes." + prize + ".Commands")){
						commands.add(cmd);
					}
					prizes.add(new Prize(prize, getDisplayItem(file, prize), msgs, commands,
							getItems(file, prize), crateName, file.getInt("Crate.Prizes." + prize + ".Chance"),
							file.getInt("Crate.Prizes." + prize + ".MaxRange"), file.getBoolean("Crate.Prizes." + prize + ".Firework")));
				}
				crates.add(new Crate(crateName, CrateType.getFromName(file.getString("Crate.CrateType")), getKey(file), prizes, file));
				Bukkit.getLogger().log(Level.INFO, "[Crazy Crates]>> " + crateName + ".yml has been loaded.");
			}catch(Exception e){
				Bukkit.getLogger().log(Level.WARNING, "[Crazy Crates]>> There was an error while loading the " + crateName + ".yml file.");
				e.printStackTrace();
			}
		}
		Bukkit.getLogger().log(Level.INFO, "[Crazy Crates]>> All crate information has been loaded.");
	}
	
	public void openCrate(Player player, CrateType crate, Location loc){
		switch(crate){
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
			case WAR:
				War.openWarCrate(player);
				break;
			case QUAD_CRATE:
				CrateControl.lastLocation.put(player, player.getLocation());
				QCC.startBuild(player, loc, Material.CHEST);
				break;
			case FIRE_CRACKER:
				if(CrateControl.inUse.containsValue(loc)){
					player.sendMessage(Messages.QUICK_CRATE_IN_USE.getMessage());
				}else{
					FireCracker.startFireCracker(player, crate.getName(), loc);
					CrateControl.inUse.put(player, loc);
				}
				break;
			case QUICK_CRATE:
				if(CrateControl.inUse.containsValue(loc)){
					player.sendMessage(Messages.QUICK_CRATE_IN_USE.getMessage());
				}else{
					QuickCrate.openCrate(player, loc, true);
					CrateControl.inUse.put(player, loc);
				}
				break;
			case CRATE_ON_THE_GO:
				if(Methods.Key.get(player) == KeyType.PHYSICAL_KEY){
					Methods.removeItem(CrateControl.keys.get(player), player);
				}
				Prize prize = pickPrize(player);
				getReward(player, prize);
				if(prize.toggleFirework()){
					Methods.fireWork(player.getLocation().add(0, 1, 0));
				}
				GUI.crates.remove(player);
				break;
		}
		FileConfiguration file = CrateControl.crates.get(player).getFile();
		if(file.getBoolean("Crate.OpeningBroadCast")){
			Bukkit.broadcastMessage(Methods.color(file.getString("Crate.BroadCast")
					.replaceAll("%Prefix%", Methods.getPrefix()).replaceAll("%prefix%", Methods.getPrefix())
					.replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName())));
		}
	}
	
	public ArrayList<Crate> getCrates(){
		return crates;
	}
	
	public void getReward(Player player, Prize prize){
		if(prize != null){
			for(ItemStack i : prize.getItems()){
				if(!Methods.isInvFull(player)){
					player.getInventory().addItem(i);
				}else{
					player.getWorld().dropItemNaturally(player.getLocation(), i);
				}
			}
			for(String command : prize.getCommands()){// /give %player% iron %random%:1-64
				if(command.contains("%random%:")){
					String cmd = command;
					command = "";
					for(String word : cmd.split(" ")){
						if(word.startsWith("%random%:")){
							word = word.replace("%random%:", "");
							try{
								int min = Integer.parseInt(word.split("-")[0]);
								int max = Integer.parseInt(word.split("-")[1]);
								command += pickNumber(min, max) + " ";
							}catch(Exception e){
								command += "1 ";
								Bukkit.getLogger().log(Level.WARNING, "[CrazyCrates]>> The prize " + prize.getName() + " in the " + prize.getCrate() +
										" crate has errored when trying to run a command.");
								Bukkit.getLogger().log(Level.WARNING, "[CrazyCrates]>> Command: " + cmd);
							}
						}else{
							command += word + " ";
						}
					}
					command = command.substring(0, command.length() - 1);
				}
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Methods.color(command
						.replace("%Player%", player.getName()).replace("%player%", player.getName())));
			}
			for(String msg : prize.getMessages()){
				player.sendMessage(Methods.color(msg)
						.replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName()));
			}
		}else{
			Bukkit.getLogger().log(Level.WARNING, "[CrazyCrates]>> No prize was found when giving " + player.getName() + " a prize.");
		}
	}
	
	public Boolean addOfflineKeys(String player, Crate crate, int keys) {
		try {
			FileConfiguration data = Main.settings.getData();
			player = player.toLowerCase();
			for(String uuid : data.getConfigurationSection("Players").getKeys(false)) {
				if(data.getString("Players." + uuid + ".Name").equalsIgnoreCase(player)) {
					if(data.contains("Players." + uuid + "." + crate.getName())) {
						keys += data.getInt("Players." + uuid + "." + crate.getName());
					}
					data.set("Players." + uuid + "." + crate.getName(), keys);
					Main.settings.saveData();	
					return true;
				}
			}
			if(data.contains("Offline-Players." + player + "." + crate.getName())) {
				keys += data.getInt("Offline-Players." + player + "." + crate.getName());
			}
			data.set("Offline-Players." + player + "." + crate.getName(), keys);
			Main.settings.saveData();
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void loadOfflinePlayersKeys(Player player) {
		FileConfiguration data = Main.settings.getData();
		String name = player.getName().toLowerCase();
		if(data.contains("Offline-Players." + name)) {
			for(Crate crate : getCrates()) {
				if(data.contains("Offline-Players." + name + "." + crate.getName())) {
					if(data.getInt("Offline-Players." + name + "." + crate.getName()) > 0) {
						Methods.addKeys(data.getInt("Offline-Players." + name + "." + crate.getName()),
								player, crate, KeyType.VIRTUAL_KEY);
					}
				}
			}
			data.set("Offline-Players." + name, null);
			Main.settings.saveData();
		}
	}
	
	
	public Prize pickPrize(Player player){
		Crate crate = CrateControl.crates.get(player);
		ArrayList<Prize> prizes = new ArrayList<Prize>();
		for(int stop = 0; prizes.size() == 0 && stop <= 2000; stop++){
			for(Prize prize : crate.getPrizes()){
				int max = prize.getMaxRange();
				int chance = prize.getChance();
				int num;
				for(int counter = 1; counter <= 1; counter++){
					num = 1 + new Random().nextInt(max);
					if(num >= 1 && num <= chance){
						prizes.add(prize);
					}
				}
			}
		}
		return prizes.get(new Random().nextInt(prizes.size()));
	}
	
	public Prize pickPrize(Player player, Location loc){
		Crate crate = CrateControl.crates.get(player);
		ArrayList<Prize> prizes = new ArrayList<Prize>();
		for(int stop = 0; prizes.size() == 0 && stop <= 2000; stop++){
			for(Prize prize : crate.getPrizes()){
				int max = prize.getMaxRange();
				int chance = prize.getChance();
				int num;
				for(int counter = 1; counter <= 1; counter++){
					num = 1 + new Random().nextInt(max);
					if(num >= 1 && num <= chance){
						prizes.add(prize);
					}
				}
			}
		}
		Prize prize = prizes.get(new Random().nextInt(prizes.size()));
		if(prize.toggleFirework()){
			Methods.fireWork(loc);
		}
		return prize;
	}
	
	private ItemStack getKey(FileConfiguration file){
		String name = file.getString("Crate.PhysicalKey.Name");
		List<String> lore = file.getStringList("Crate.PhysicalKey.Lore");
		String id = file.getString("Crate.PhysicalKey.Item");
		Boolean enchanted = false;
		if(file.contains("Crate.PhysicalKey.Glowing")){
			enchanted = file.getBoolean("Crate.PhysicalKey.Glowing");
		}
		return Methods.makeItem(id, 1, name, lore, enchanted);
	}
	
	private ItemStack getDisplayItem(FileConfiguration file, String prize){
		String path = "Crate.Prizes." + prize + ".";
		String id = file.getString(path + "DisplayItem");
		String name = file.getString(path + "DisplayName");
		int amount = 1;
		if(file.contains(path + "DisplayAmount")){
			amount = file.getInt(path + "DisplayAmount");
		}
		ArrayList<String> lore = new ArrayList<String>();
		if(file.contains(path + "Lore")){
			for(String l : file.getStringList(path + "Lore")){
				lore.add(l);
			}
		}
		HashMap<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();
		if(file.contains(path + "DisplayEnchantments")){
			for(String enchant : file.getStringList(path + "DisplayEnchantments")){
				for(Enchantment enc : Enchantment.values()){
					if(Methods.getEnchantments().contains(enc.getName())){
						enchant = enchant.toLowerCase();
						if(enchant.contains(enc.getName().toLowerCase() + ":") || enchant.contains(Methods.getEnchantmentName(enc).toLowerCase() + ":")){
							String[] breakdown = enchant.split(":");
							int lvl = Integer.parseInt(breakdown[1]);
							enchants.put(enc, lvl);
						}
					}
				}
			}
		}
		String player = "";
		if(file.contains(path + "Player")){
			player = file.getString(path + "Player");
		}
		boolean glowing = false;
		if(file.contains(path + "Glowing")){
			glowing = file.getBoolean(path + "Glowing");
		}
		try{
			if(Methods.makeItem(id, amount, "").getType() == Material.SKULL_ITEM){
				return Methods.makePlayerHead(player, amount, name, lore, enchants, glowing);
			}else{
				return Methods.makeItem(id, amount, name, lore, enchants, glowing);
			}
		}catch(Exception e){
			return Methods.makeItem(Material.STAINED_CLAY, 1, 14, "&c&lERROR", Arrays.asList("&cThere is an error", "&cFor the reward: &c" + prize));
		}
	}
	
	private ArrayList<ItemStack> getItems(FileConfiguration file, String prize){
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		for(String l : file.getStringList("Crate.Prizes." + prize + ".Items")){
			ArrayList<String> lore = new ArrayList<String>();
			HashMap<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();
			String name = "";
			int amount = 1;
			String id = "Stone";
			String player = "";
			for(String i : l.split(", ")){
				if(i.contains("Item:")){
					i = i.replaceAll("Item:", "");
					id = i;
				}
				if(i.contains("Name:")){
					i = i.replaceAll("Name:", "");
					i = i.replaceAll("_", " ");
					name = Methods.color(i);
				}
				if(i.contains("Amount:")){
					i = i.replaceAll("Amount:", "");
					amount = Integer.parseInt(i);
				}
				if(i.contains("Lore:")){
					i = i.replaceAll("Lore:", "");
					for(String L : i.split(",")){
						L = Methods.color(L);
						lore.add(L);
					}
				}
				if(i.contains("Player:")){
					i = i.replaceAll("Player:", "");
					player = i;
				}
				for(Enchantment enc : Enchantment.values()){
					if(i.toLowerCase().contains(enc.getName().toLowerCase() + ":") || 
							i.toLowerCase().contains(Methods.getEnchantmentName(enc).toLowerCase() + ":")){
						String[] breakdown = i.split(":");
						int lvl = Integer.parseInt(breakdown[1]);
						enchants.put(enc, lvl);
					}
				}
			}
			try{
				if(Methods.makeItem(id, amount, name).getType() == Material.SKULL_ITEM){
					items.add(Methods.makePlayerHead(player, amount, name, lore, enchants, false));
				}else{
					items.add(Methods.makeItem(id, amount, name, lore, enchants));
				}
			}catch(Exception e){
				items.add(Methods.makeItem(Material.STAINED_CLAY, 1, 14, "&c&lERROR", Arrays.asList("&cThere is an error", "&cFor the reward: &c" + prize)));
			}
		}
		return items;
	}
	
	private Integer pickNumber(int min, int max){
		max++;
		return min + new Random().nextInt(max - min);
	}
	
}