package me.badbones69.crazycrates.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

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
				CrateControl.LastLoc.put(player, player.getLocation());
				QCC.startBuild(player, loc, Material.CHEST);
				break;
			case FIRE_CRACKER:
				if(CrateControl.InUse.containsValue(loc)){
					player.sendMessage(Methods.color(Methods.getPrefix() + Main.settings.getConfig().getString("Settings.QuickCrateInUse")));
				}else{
					FireCracker.startFireCracker(player, crate.getName(), loc);
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
				Prize prize = pickPrize(player);
				getReward(player, prize);
				if(prize.toggleFirework()){
					Methods.fireWork(player.getLocation().add(0, 1, 0));
				}
				GUI.crates.remove(player);
				break;
		}
	}
	
	public void loadCrates(){
		crates.clear();
		for(String crateName : Main.settings.getAllCratesNames()){
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
						getItems(file, prize), file.getInt("Crate.Prizes." + prize + ".Chance"),
						file.getInt("Crate.Prizes." + prize + ".MaxRange"), file.getBoolean("Crate.Prizes." + prize + ".Firework")));
			}
			crates.add(new Crate(crateName, CrateType.getFromName(file.getString("Crate.CrateType")), getKey(file), prizes, file));
		}
	}
	
	public ArrayList<Crate> getCrates(){
		return crates;
	}
	
	public void getReward(Player player, Prize prize){
		for(ItemStack i : prize.getItems()){
			if(!Methods.isInvFull(player)){
				player.getInventory().addItem(i);
			}else{
				player.getWorld().dropItemNaturally(player.getLocation(), i);
			}
		}
		for(String command : prize.getCommands()){
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Methods.color(command
					.replace("%Player%", player.getName()).replace("%player%", player.getName())));
		}
		for(String msg : prize.getMessages()){
			player.sendMessage(Methods.color(msg)
					.replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName()));
		}
	}
	
	public Prize pickPrize(Player player){
		Crate crate = CrateControl.Crate.get(player);
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
		Crate crate = CrateControl.Crate.get(player);
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
					if(i.contains(enc.getName() + ":") || i.contains(Methods.getEnchantmentName(enc) + ":")){
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
	
}