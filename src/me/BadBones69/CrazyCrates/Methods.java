package me.BadBones69.CrazyCrates;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import me.BadBones69.CrazyCrates.API.Crate;
import me.BadBones69.CrazyCrates.API.FireworkDamageAPI;
import me.BadBones69.CrazyCrates.API.KeyType;
import me.BadBones69.CrazyCrates.MultiSupport.NMS_v1_10_R1;
import me.BadBones69.CrazyCrates.MultiSupport.NMS_v1_11_R1;
import me.BadBones69.CrazyCrates.MultiSupport.NMS_v1_8_R1;
import me.BadBones69.CrazyCrates.MultiSupport.NMS_v1_8_R2;
import me.BadBones69.CrazyCrates.MultiSupport.NMS_v1_8_R3;
import me.BadBones69.CrazyCrates.MultiSupport.NMS_v1_9_R1;
import me.BadBones69.CrazyCrates.MultiSupport.NMS_v1_9_R2;
import me.BadBones69.CrazyCrates.MultiSupport.Version;

public class Methods{
	
	public static HashMap<Player, String> path = new HashMap<Player, String>();
	public static HashMap<Player, KeyType> Key = new HashMap<Player, KeyType>();
	public static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CrazyCrates");
	
	public static String color(String msg){
		msg = ChatColor.translateAlternateColorCodes('&', msg);
		return msg;
	}
	
	public static String removeColor(String msg){
		msg = ChatColor.stripColor(msg);
		return msg;
	}
	
	public static HashMap<ItemStack, String> getItems(Player player){
		HashMap<ItemStack, String> items = new HashMap<ItemStack, String>();
		FileConfiguration file = GUI.Crate.get(player).getFile();
		for(String reward : file.getConfigurationSection("Crate.Prizes").getKeys(false)){
			String id = file.getString("Crate.Prizes." + reward + ".DisplayItem");
			String name = file.getString("Crate.Prizes." + reward + ".DisplayName");
			int chance = file.getInt("Crate.Prizes." + reward + ".Chance");
			int max = 99;
			if(file.contains("Crate.Prizes." + reward + ".MaxRange")){
				max=file.getInt("Crate.Prizes." + reward + ".MaxRange")-1;
			}
			try{
				ItemStack item = makeItem(id, 1, name);
				Random number = new Random();
				int num;
				for(int counter = 1; counter<=1; counter++){
					num = 1 + number.nextInt(max);
					if(num >= 1 && num <= chance)items.put(item, "Crate.Prizes."+reward);
				}
			}catch(Exception e){
				continue;
			}
		}
		return items;
	}
	
	public static String getEnchantmentName(Enchantment en){
		HashMap<String, String> enchants = new HashMap<String, String>();
		enchants.put("ARROW_DAMAGE", "Power");
		enchants.put("ARROW_FIRE", "Flame");
		enchants.put("ARROW_INFINITE", "Infinity");
		enchants.put("ARROW_KNOCKBACK", "Punch");
		enchants.put("DAMAGE_ALL", "Sharpness");
		enchants.put("DAMAGE_ARTHROPODS", "Bane_Of_Arthropods");
		enchants.put("DAMAGE_UNDEAD", "Smite");
		enchants.put("DEPTH_STRIDER", "Depth_Strider");
		enchants.put("DIG_SPEED", "Efficiency");
		enchants.put("DURABILITY", "Unbreaking");
		enchants.put("FIRE_ASPECT", "Fire_Aspect");
		enchants.put("KNOCKBACK", "KnockBack");
		enchants.put("LOOT_BONUS_BLOCKS", "Fortune");
		enchants.put("LOOT_BONUS_MOBS", "Looting");
		enchants.put("LUCK", "Luck_Of_The_Sea");
		enchants.put("LURE", "Lure");
		enchants.put("OXYGEN", "Respiration");
		enchants.put("PROTECTION_ENVIRONMENTAL", "Protection");
		enchants.put("PROTECTION_EXPLOSIONS", "Blast_Protection");
		enchants.put("PROTECTION_FALL", "Feather_Falling");
		enchants.put("PROTECTION_FIRE", "Fire_Protection");
		enchants.put("PROTECTION_PROJECTILE", "Projectile_Protection");
		enchants.put("SILK_TOUCH", "Silk_Touch");
		enchants.put("THORNS", "Thorns");
		enchants.put("WATER_WORKER", "Aqua_Affinity");
		enchants.put("BINDING_CURSE", "Curse_Of_Binding");
		enchants.put("MENDING", "Mending");
		enchants.put("FROST_WALKER", "Frost_Walker");
		enchants.put("VANISHING_CURSE", "Curse_Of_Vanishing");
		if(enchants.get(en.getName()) == null){
			return "None Found";
		}
		return enchants.get(en.getName());
	}
	public static void fireWork(Location loc) {
		final Firework fw = loc.getWorld().spawn(loc, Firework.class);
		FireworkMeta fm = fw.getFireworkMeta();
		fm.addEffects(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE)
				.withColor(Color.RED)
				.withColor(Color.AQUA)
				.withColor(Color.ORANGE)
				.withColor(Color.YELLOW)
				.trail(false)
				.flicker(false)
				.build());
		fm.setPower(0);
		fw.setFireworkMeta(fm);
		FireworkDamageAPI.addFirework(fw);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				fw.detonate();
			}
		}, 2);
	}
	public static ItemStack makeItem(Material material, int amount, int type, String name){
		ItemStack item = new ItemStack(material, amount, (short) type);
		ItemMeta m = item.getItemMeta();
		m.setDisplayName(color(name));
		item.setItemMeta(m);
		return item;
	}
	@SuppressWarnings("deprecation")
	public static ItemStack makeItem(String type, int amount, String name, List<String> lore){
		ArrayList<String> l = new ArrayList<String>();
		int ty = 0;
		if(type.contains(":")){
			String[] b = type.split(":");
			type = b[0];
			ty = Integer.parseInt(b[1]);
		}
		Material m = Material.matchMaterial(type);
		ItemStack item = new ItemStack(m, amount, (short) ty);
		if(m == Material.MONSTER_EGG){
			switch(Version.getVersion()){
			case v1_11_R1:
				item = NMS_v1_11_R1.getSpawnEgg(EntityType.fromId(ty), amount);
				break;
			case v1_10_R1:
				item = NMS_v1_10_R1.getSpawnEgg(EntityType.fromId(ty), amount);
				break;
			case v1_9_R2:
				item = NMS_v1_9_R2.getSpawnEgg(EntityType.fromId(ty), amount);
				break;
			case v1_9_R1:
				item = NMS_v1_9_R1.getSpawnEgg(EntityType.fromId(ty), amount);
				break;
			default:
				break;
			}
		}
		ItemMeta me = item.getItemMeta();
		me.setDisplayName(color(name));
		for(String L:lore)l.add(color(L));
		me.setLore(l);
		item.setItemMeta(me);
		return item;
	}
	@SuppressWarnings("deprecation")
	public static ItemStack makeItem(String type, int amount, String name, List<String> lore, Boolean Enchanted){
		ArrayList<String> l = new ArrayList<String>();
		int ty = 0;
		if(type.contains(":")){
			String[] b = type.split(":");
			type = b[0];
			ty = Integer.parseInt(b[1]);
		}
		Material m = Material.matchMaterial(type);
		ItemStack item = new ItemStack(m, amount, (short) ty);
		if(m == Material.MONSTER_EGG){
			switch(Version.getVersion()){
			case v1_11_R1:
				item = NMS_v1_11_R1.getSpawnEgg(EntityType.fromId(ty), amount);
				break;
			case v1_10_R1:
				item = NMS_v1_10_R1.getSpawnEgg(EntityType.fromId(ty), amount);
				break;
			case v1_9_R2:
				item = NMS_v1_9_R2.getSpawnEgg(EntityType.fromId(ty), amount);
				break;
			case v1_9_R1:
				item = NMS_v1_9_R1.getSpawnEgg(EntityType.fromId(ty), amount);
				break;
			default:
				break;
			}
		}
		ItemMeta me = item.getItemMeta();
		me.setDisplayName(color(name));
		for(String L:lore)l.add(color(L));
		me.setLore(l);
		item.setItemMeta(me);
		if(Enchanted){
			item = addGlow(item);
		}
		return item;
	}
	@SuppressWarnings("deprecation")
	public static ItemStack makeItem(String type, int amount, String name){
		int ty = 0;
		if(type.contains(":")){
			String[] b = type.split(":");
			type = b[0];
			ty = Integer.parseInt(b[1]);
		}
		Material m = Material.matchMaterial(type);
		ItemStack item = new ItemStack(m, amount, (short) ty);
		if(m == Material.MONSTER_EGG){
			switch(Version.getVersion()){
			case v1_11_R1:
				item = NMS_v1_11_R1.getSpawnEgg(EntityType.fromId(ty), amount);
				break;
			case v1_10_R1:
				item = NMS_v1_10_R1.getSpawnEgg(EntityType.fromId(ty), amount);
				break;
			case v1_9_R2:
				item = NMS_v1_9_R2.getSpawnEgg(EntityType.fromId(ty), amount);
				break;
			case v1_9_R1:
				item = NMS_v1_9_R1.getSpawnEgg(EntityType.fromId(ty), amount);
				break;
			default:
				break;
			}
		}
		ItemMeta me = item.getItemMeta();
		me.setDisplayName(color(name));
		item.setItemMeta(me);
		return item;
	}
	@SuppressWarnings("deprecation")
	public static ItemStack makeItem(Material material, int amount, int ty, String name, List<String> lore){
		ArrayList<String> l = new ArrayList<String>();
		ItemStack item = new ItemStack(material, amount, (short) ty);
		ItemMeta m = item.getItemMeta();
		if(material == Material.MONSTER_EGG){
			switch(Version.getVersion()){
			case v1_11_R1:
				item = NMS_v1_11_R1.getSpawnEgg(EntityType.fromId(ty), amount);
				break;
			case v1_10_R1:
				item = NMS_v1_10_R1.getSpawnEgg(EntityType.fromId(ty), amount);
				break;
			case v1_9_R2:
				item = NMS_v1_9_R2.getSpawnEgg(EntityType.fromId(ty), amount);
				break;
			case v1_9_R1:
				item = NMS_v1_9_R1.getSpawnEgg(EntityType.fromId(ty), amount);
				break;
			default:
				break;
			}
		}
		m.setDisplayName(color(name));
		for(String L:lore)l.add(color(L));
		m.setLore(l);
		item.setItemMeta(m);
		return item;
	}
	@SuppressWarnings("deprecation")
	public static ItemStack makeItem(String id, int amount, String name, List<String> lore, Map<Enchantment, Integer> enchants){
		ArrayList<String> l = new ArrayList<String>();
		String ma = id;
		int ty = 0;
		if(ma.contains(":")){
			String[] b = ma.split(":");
			ma = b[0];
			ty = Integer.parseInt(b[1]);
		}
		Material material = Material.matchMaterial(ma);
		ItemStack item = new ItemStack(material, amount, (short) ty);
		if(material == Material.MONSTER_EGG){
			switch(Version.getVersion()){
			case v1_11_R1:
				item = NMS_v1_11_R1.getSpawnEgg(EntityType.fromId(ty), amount);
				break;
			case v1_10_R1:
				item = NMS_v1_10_R1.getSpawnEgg(EntityType.fromId(ty), amount);
				break;
			case v1_9_R2:
				item = NMS_v1_9_R2.getSpawnEgg(EntityType.fromId(ty), amount);
				break;
			case v1_9_R1:
				item = NMS_v1_9_R1.getSpawnEgg(EntityType.fromId(ty), amount);
				break;
			default:
				break;
			}
		}
		ItemMeta m = item.getItemMeta();
		m.setDisplayName(color(name));
		for(String L:lore)l.add(color(L));
		m.setLore(l);
		item.setItemMeta(m);
		item.addUnsafeEnchantments(enchants);
		return item;
	}
	@SuppressWarnings("deprecation")
	public static ItemStack makeItem(String id, int amount, String name, List<String> lore, Map<Enchantment, Integer> enchants, Boolean glowing){
		ArrayList<String> l = new ArrayList<String>();
		String ma = id;
		int ty = 0;
		if(ma.contains(":")){
			String[] b = ma.split(":");
			ma = b[0];
			ty = Integer.parseInt(b[1]);
		}
		Material material = Material.matchMaterial(ma);
		ItemStack item = new ItemStack(material, amount, (short) ty);
		if(material == Material.MONSTER_EGG){
			switch(Version.getVersion()){
			case v1_11_R1:
				item = NMS_v1_11_R1.getSpawnEgg(EntityType.fromId(ty), amount);
				break;
			case v1_10_R1:
				item = NMS_v1_10_R1.getSpawnEgg(EntityType.fromId(ty), amount);
				break;
			case v1_9_R2:
				item = NMS_v1_9_R2.getSpawnEgg(EntityType.fromId(ty), amount);
				break;
			case v1_9_R1:
				item = NMS_v1_9_R1.getSpawnEgg(EntityType.fromId(ty), amount);
				break;
			default:
				break;
			}
		}
		ItemMeta m = item.getItemMeta();
		m.setDisplayName(color(name));
		for(String L:lore)l.add(color(L));
		m.setLore(l);
		item.setItemMeta(m);
		item.addUnsafeEnchantments(enchants);
		if(glowing){
			item = addGlow(item);
		}
		return item;
	}
	@SuppressWarnings("deprecation")
	public static ItemStack makeItem(Material material, int amount, int ty, String name, List<String> lore, Map<Enchantment, Integer> enchants){
		ArrayList<String> l = new ArrayList<String>();
		ItemStack item = new ItemStack(material, amount, (short) ty);
		if(material == Material.MONSTER_EGG){
			switch(Version.getVersion()){
			case v1_11_R1:
				item = NMS_v1_11_R1.getSpawnEgg(EntityType.fromId(ty), amount);
				break;
			case v1_10_R1:
				item = NMS_v1_10_R1.getSpawnEgg(EntityType.fromId(ty), amount);
				break;
			case v1_9_R2:
				item = NMS_v1_9_R2.getSpawnEgg(EntityType.fromId(ty), amount);
				break;
			case v1_9_R1:
				item = NMS_v1_9_R1.getSpawnEgg(EntityType.fromId(ty), amount);
				break;
			default:
				break;
			}
		}
		ItemMeta m = item.getItemMeta();
		m.setDisplayName(color(name));
		for(String L:lore)l.add(color(L));
		m.setLore(l);
		item.setItemMeta(m);
		item.addUnsafeEnchantments(enchants);
		return item;
	}
	
	/**
	 * 
	 * @param player
	 * @param name
	 * @param amount
	 * @param lore
	 * @param enchants
	 * @param glowing
	 * @return
	 */
	public static ItemStack makePlayerHead(String player, int amount, String name, ArrayList<String> lore, Map<Enchantment, Integer> enchants, boolean glowing){
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
		SkullMeta m = (SkullMeta) head.getItemMeta();
		if(!player.equals("")){
			m.setOwner(player);
		}
		if(name != null){
			m.setDisplayName(color(name));
		}
		if(lore != null){
			ArrayList<String> l = new ArrayList<String>();
			for(String L:lore){
				l.add(color(L));
			}
			m.setLore(l);
		}
		head.setItemMeta(m);
		if(enchants != null){
			head.addUnsafeEnchantments(enchants);
		}
		if(glowing){
			head = addGlow(head);
		}
		return head;
	}
	
	public static ItemStack addLore(ItemStack item, String i){
		ArrayList<String> lore = new ArrayList<String>();
		ItemMeta m = item.getItemMeta();
		if(item.getItemMeta().hasLore()){
			lore.addAll(item.getItemMeta().getLore());
		}
		lore.add(i);
		m.setLore(lore);
		item.setItemMeta(m);
		return item;
	}
	public static boolean isInt(String s) {
	    try {
	        Integer.parseInt(s);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}
	public static Location getLoc(Player player){
		return player.getLocation();
	}
	public static void runCMD(Player player, String CMD){
		player.performCommand(CMD);
	}
	public static Player getPlayer(String name){
		return Bukkit.getServer().getPlayer(name);
	}
	public static boolean isOnline(String name, CommandSender p){
		for(Player player : Bukkit.getServer().getOnlinePlayers()){
			if(player.getName().equalsIgnoreCase(name)){
				return true;
			}
		}
		p.sendMessage(color(getPrefix() + Main.settings.getConfig().getString("Settings.Not-Online")));
		return false;
	}
	public static void removeItem(ItemStack item, Player player){
		if(item.getAmount() <= 1){
			player.getInventory().removeItem(item);
		}
		if(item.getAmount() > 1){
			item.setAmount(item.getAmount() - 1);
		}
	}
	@SuppressWarnings("deprecation")
	public static ItemStack getItemInHand(Player player){
		if(Version.getVersion().getVersionInteger()>=Version.v1_9_R1.getVersionInteger()){
			return player.getInventory().getItemInMainHand();
		}else{
			return player.getItemInHand();
		}
	}
	@SuppressWarnings("deprecation")
	public static void setItemInHand(Player player, ItemStack item){
		if(Version.getVersion().getVersionInteger()>=Version.v1_9_R1.getVersionInteger()){
			player.getInventory().setItemInMainHand(item);
		}else{
			player.setItemInHand(item);
		}
	}
	public static boolean permCheck(Player player, String perm){
		if(!player.hasPermission("CrazyCrates." + perm)){
			player.sendMessage(color(Main.settings.getConfig().getString("Settings.No-Permission")));
			return false;
		}
		return true;
	}
	public static int getKeys(Player player, Crate crate){
		String uuid = player.getUniqueId().toString();
		return Main.settings.getData().getInt("Players." + uuid + "." + crate.getName());
	}
	public static void takeKeys(int Amount, Player player, Crate crate){
		String uuid = player.getUniqueId().toString();
		int keys = getKeys(player, crate);
		Main.settings.getData().set("Players." + uuid + "." + crate.getName(), keys - Amount);
		Main.settings.saveData();
	}
	public static void addKeys(int Amount, Player player, Crate crate, KeyType type){
		if(type == KeyType.VIRTUAL_KEY){
			String uuid = player.getUniqueId().toString();
			int keys = getKeys(player, crate);
			Main.settings.getData().set("Players." + uuid + "." + crate.getName(), keys + Amount);
			Main.settings.saveData();
		}else if(type == KeyType.PHYSICAL_KEY){
			FileConfiguration file = crate.getFile();
			String name = color(file.getString("Crate.PhysicalKey.Name"));
			List<String> lore = file.getStringList("Crate.PhysicalKey.Lore");
			String ID = file.getString("Crate.PhysicalKey.Item");
			Boolean enchanted = false;
			if(file.contains("Crate.PhysicalKey.Glowing")){
				enchanted = file.getBoolean("Crate.PhysicalKey.Glowing");
			}
			player.getInventory().addItem(makeItem(ID, Amount, name, lore, enchanted));
		}
	}
	public static ItemStack getKey(Crate crate){
		FileConfiguration file = crate.getFile();
		String name = color(file.getString("Crate.PhysicalKey.Name"));
		List<String> lore = file.getStringList("Crate.PhysicalKey.Lore");
		String ID = file.getString("Crate.PhysicalKey.Item");
		Boolean enchanted = false;
		if(file.contains("Crate.PhysicalKey.Glowing")){
			enchanted = file.getBoolean("Crate.PhysicalKey.Glowing");
		}
		return makeItem(ID, 1, name, lore, enchanted);
	}
	public static ArrayList<String> getCrates(){
		ArrayList<String> crates = new ArrayList<String>();
		for(String crate : Main.settings.getAllCratesNames()){
			crates.add(crate);
		}
		return crates;
	}
	public static String getPrefix(){
		return color(Main.settings.getConfig().getString("Settings.Prefix"));
	}
	public static void pasteSchem(String schem, Location loc){
		switch(Version.getVersion()){
			case TOO_NEW:
				Bukkit.getLogger().log(Level.SEVERE, "[Crazy Crates]>> Your server is too new for this plugin. "
						+ "Please update or remove this plugin to stop further Errors.");
				break;
			case v1_11_R1:
				NMS_v1_11_R1.pasteSchematic(new File(plugin.getDataFolder()+"/Schematics/"+schem), loc);
				break;
			case v1_10_R1:
				NMS_v1_10_R1.pasteSchematic(new File(plugin.getDataFolder()+"/Schematics/"+schem), loc);
				break;
			case v1_9_R2:
				NMS_v1_9_R2.pasteSchematic(new File(plugin.getDataFolder()+"/Schematics/"+schem), loc);
				break;
			case v1_9_R1:
				NMS_v1_9_R1.pasteSchematic(new File(plugin.getDataFolder()+"/Schematics/"+schem), loc);
				break;
			case v1_8_R3:
				NMS_v1_8_R3.pasteSchematic(new File(plugin.getDataFolder()+"/Schematics/"+schem), loc);
				break;
			case v1_8_R2:
				NMS_v1_8_R2.pasteSchematic(new File(plugin.getDataFolder()+"/Schematics/"+schem), loc);
				break;
			case v1_8_R1:
				NMS_v1_8_R1.pasteSchematic(new File(plugin.getDataFolder()+"/Schematics/"+schem), loc);
				break;
			case TOO_OLD:
				Bukkit.getLogger().log(Level.SEVERE, "[Crazy Crates]>> Your server is too far out of date. "
						+ "Please update or remove this plugin to stop further Errors.");
				break;
		}
	}
	public static List<Location> getLocations(String shem, Location loc){
		switch(Version.getVersion()){
			case TOO_NEW:
				Bukkit.getLogger().log(Level.SEVERE, "[Crazy Crates]>> Your server is too new for this plugin. "
						+ "Please update or remove this plugin to stop further Errors.");
				break;
			case v1_11_R1:
				return NMS_v1_11_R1.getLocations(new File(plugin.getDataFolder()+"/Schematics/"+shem), loc);
			case v1_10_R1:
				return NMS_v1_10_R1.getLocations(new File(plugin.getDataFolder()+"/Schematics/"+shem), loc);
			case v1_9_R2:
				return NMS_v1_9_R2.getLocations(new File(plugin.getDataFolder()+"/Schematics/"+shem), loc);
			case v1_9_R1:
				return NMS_v1_9_R1.getLocations(new File(plugin.getDataFolder()+"/Schematics/"+shem), loc);
			case v1_8_R3:
				return NMS_v1_8_R3.getLocations(new File(plugin.getDataFolder()+"/Schematics/"+shem), loc);
			case v1_8_R2:
				return NMS_v1_8_R2.getLocations(new File(plugin.getDataFolder()+"/Schematics/"+shem), loc);
			case v1_8_R1:
				return NMS_v1_8_R1.getLocations(new File(plugin.getDataFolder()+"/Schematics/"+shem), loc);
			case TOO_OLD:
				Bukkit.getLogger().log(Level.SEVERE, "[Crazy Crates]>> Your server is too far out of date. "
						+ "Please update or remove this plugin to stop further Errors.");
				break;
		}
		return null;
	}
	public static void playChestAction(Block b, boolean open) {
        Location location = b.getLocation();
        Material type = b.getType();
        if(type == Material.CHEST || type == Material.TRAPPED_CHEST || type == Material.ENDER_CHEST){
        	switch(Version.getVersion()){
				case TOO_NEW:
					Bukkit.getLogger().log(Level.SEVERE, "[Crazy Crates]>> Your server is too new for this plugin. "
							+ "Please update or remove this plugin to stop further Errors.");
					break;
				case v1_11_R1:
					NMS_v1_11_R1.openChest(b, location, open);
					break;
				case v1_10_R1:
					NMS_v1_10_R1.openChest(b, location, open);
					break;
				case v1_9_R2:
					NMS_v1_9_R2.openChest(b, location, open);
					break;
				case v1_9_R1:
					NMS_v1_9_R1.openChest(b, location, open);
					break;
				case v1_8_R3:
					NMS_v1_8_R3.openChest(b, location, open);
					break;
				case v1_8_R2:
					NMS_v1_8_R2.openChest(b, location, open);
					break;
				case v1_8_R1:
					NMS_v1_8_R1.openChest(b, location, open);
					break;
				case TOO_OLD:
					Bukkit.getLogger().log(Level.SEVERE, "[Crazy Crates]>> Your server is too far out of date. "
							+ "Please update or remove this plugin to stop further Errors.");
					break;
			}
        }
    }
	public static ItemStack addGlow(ItemStack item) {
		switch(Version.getVersion()){
			case TOO_NEW:
				Bukkit.getLogger().log(Level.SEVERE, "[Crazy Crates]>> Your server is too new for this plugin. "
						+ "Please update or remove this plugin to stop further Errors.");
				break;
			case v1_11_R1:
				return NMS_v1_11_R1.addGlow(item);
			case v1_10_R1:
				return NMS_v1_10_R1.addGlow(item);
			case v1_9_R2:
				return NMS_v1_9_R2.addGlow(item);
			case v1_9_R1:
				return NMS_v1_9_R1.addGlow(item);
			case v1_8_R3:
				return NMS_v1_8_R3.addGlow(item);
			case v1_8_R2:
				return NMS_v1_8_R2.addGlow(item);
			case v1_8_R1:
				return NMS_v1_8_R1.addGlow(item);
			case TOO_OLD:
				Bukkit.getLogger().log(Level.SEVERE, "[Crazy Crates]>> Your server is too far out of date. "
						+ "Please update or remove this plugin to stop further Errors.");
				break;
		}
		return item;
    }
	public static String pickRandomSchem(){
		File f = new File(plugin.getDataFolder()+"/Schematics/");
		String[] schems = f.list();
		ArrayList<String> schematics = new ArrayList<String>();
		for(String i : schems){
			if(!i.equalsIgnoreCase(".DS_Store")){
				schematics.add(i);
			}
		}
		Random r = new Random();
		return schematics.get(r.nextInt(schematics.size()));
	}
	public static boolean isInvFull(Player player){
		if(player.getInventory().firstEmpty() == -1){
			return true;
		}
		return false;
	}
	public static Integer randomNumber(int min, int max){
		Random i = new Random();
		return min + i.nextInt(max - min);
	}
	
	public static boolean isSimilar(ItemStack one, ItemStack two){
		if(one.getType() == two.getType()){
			if(one.hasItemMeta()){
				if(one.getItemMeta().hasDisplayName()){
					if(one.getItemMeta().getDisplayName().equalsIgnoreCase(two.getItemMeta().getDisplayName())){
						if(one.getItemMeta().hasLore()){
							if(one.getItemMeta().getLore().size() == two.getItemMeta().getLore().size()){
								int i = 0;
								for(String lore : one.getItemMeta().getLore()){
									if(!lore.equals(two.getItemMeta().getLore().get(i))){
										return false;
									}
									i++;
								}
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	public static void hasUpdate(){
		try {
			HttpURLConnection c = (HttpURLConnection)new URL("http://www.spigotmc.org/api/general.php").openConnection();
			c.setDoOutput(true);
			c.setRequestMethod("POST");
			c.getOutputStream().write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=17599").getBytes("UTF-8"));
			String oldVersion = plugin.getDescription().getVersion();
			String newVersion = new BufferedReader(new InputStreamReader(c.getInputStream())).readLine().replaceAll("[a-zA-Z ]", "");
			if(!newVersion.equals(oldVersion)) {
				Bukkit.getConsoleSender().sendMessage(getPrefix()+color("&cYour server is running &7v"+oldVersion+"&c and the newest is &7v"+newVersion+"&c."));
			}
		}
		catch(Exception e) {
			return;
		}
	}
	
	public static void hasUpdate(Player player){
		try {
			HttpURLConnection c = (HttpURLConnection)new URL("http://www.spigotmc.org/api/general.php").openConnection();
			c.setDoOutput(true);
			c.setRequestMethod("POST");
			c.getOutputStream().write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=17599").getBytes("UTF-8"));
			String oldVersion = plugin.getDescription().getVersion();
			String newVersion = new BufferedReader(new InputStreamReader(c.getInputStream())).readLine().replaceAll("[a-zA-Z ]", "");
			if(!newVersion.equals(oldVersion)) {
				player.sendMessage(getPrefix()+color("&cYour server is running &7v"+oldVersion+"&c and the newest is &7v"+newVersion+"&c."));
			}
		}
		catch(Exception e) {
			return;
		}
	}
	
	public static Set<String> getEnchantments(){
		HashMap<String, String> enchants = new HashMap<String, String>();
		enchants.put("ARROW_DAMAGE", "Power");
		enchants.put("ARROW_FIRE", "Flame");
		enchants.put("ARROW_INFINITE", "Infinity");
		enchants.put("ARROW_KNOCKBACK", "Punch");
		enchants.put("DAMAGE_ALL", "Sharpness");
		enchants.put("DAMAGE_ARTHROPODS", "Bane_Of_Arthropods");
		enchants.put("DAMAGE_UNDEAD", "Smite");
		enchants.put("DEPTH_STRIDER", "Depth_Strider");
		enchants.put("DIG_SPEED", "Efficiency");
		enchants.put("DURABILITY", "Unbreaking");
		enchants.put("FIRE_ASPECT", "Fire_Aspect");
		enchants.put("KNOCKBACK", "KnockBack");
		enchants.put("LOOT_BONUS_BLOCKS", "Fortune");
		enchants.put("LOOT_BONUS_MOBS", "Looting");
		enchants.put("LUCK", "Luck_Of_The_Sea");
		enchants.put("LURE", "Lure");
		enchants.put("OXYGEN", "Respiration");
		enchants.put("PROTECTION_ENVIRONMENTAL", "Protection");
		enchants.put("PROTECTION_EXPLOSIONS", "Blast_Protection");
		enchants.put("PROTECTION_FALL", "Feather_Falling");
		enchants.put("PROTECTION_FIRE", "Fire_Protection");
		enchants.put("PROTECTION_PROJECTILE", "Projectile_Protection");
		enchants.put("SILK_TOUCH", "Silk_Touch");
		enchants.put("THORNS", "Thorns");
		enchants.put("WATER_WORKER", "Aqua_Affinity");
		enchants.put("BINDING_CURSE", "Curse_Of_Binding");
		enchants.put("MENDING", "Mending");
		enchants.put("FROST_WALKER", "Frost_Walker");
		enchants.put("VANISHING_CURSE", "Curse_Of_Vanishing");
		return enchants.keySet();
	}
	
}