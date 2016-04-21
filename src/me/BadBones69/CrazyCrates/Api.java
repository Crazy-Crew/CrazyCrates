package me.BadBones69.CrazyCrates;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class Api{
	public static HashMap<Player, String> path = new HashMap<Player, String>();
	public static HashMap<Player, String> Key = new HashMap<Player, String>();
	public static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CrazyCrates");
	@SuppressWarnings("static-access")
	public Api(Plugin plugin){
		this.plugin = plugin;
	}
	public static Integer getVersion(){
		String ver = Bukkit.getServer().getClass().getPackage().getName();
		ver = ver.substring(ver.lastIndexOf('.')+1);
		ver=ver.replaceAll("_", "").replaceAll("R", "").replaceAll("v", "");
		return Integer.parseInt(ver);
	}
	public static String color(String msg){
		msg = msg.replaceAll("(&([a-f0-9]))", "\u00A7$2");
		msg = msg.replaceAll("&l", ChatColor.BOLD + "");
		msg = msg.replaceAll("&o", ChatColor.ITALIC + "");
		msg = msg.replaceAll("&k", ChatColor.MAGIC + "");
		msg = msg.replaceAll("&n", ChatColor.UNDERLINE + "");
		return msg;
	}
	public static String removeColor(String msg){
		msg = ChatColor.stripColor(msg);
		return msg;
	}
	public static ItemStack displayItem(Player player, Location chest){
		HashMap<ItemStack, String> items = getItems(player);
		int stop = 0;
		for(;items.size()==0;stop++){
			if(stop==100){
				break;
			}
			items=getItems(player);
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
		path.put(player, pa);
		if(Main.settings.getFile(GUI.Crate.get(player)).contains(path.get(player) + ".Items")){
			for(ItemStack i : getFinalItems(path.get(player), player)){
				player.getInventory().addItem(i);
			}
		}
		if(Main.settings.getFile(GUI.Crate.get(player)).contains(path.get(player) + ".Commands")){
			for(String command : Main.settings.getFile(GUI.Crate.get(player)).getStringList(path.get(player) + ".Commands")){
				command = color(command);
				command = command.replace("%Player%", player.getName());
				command = command.replace("%player%", player.getName());
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
			}
		}
		if(Main.settings.getFile(GUI.Crate.get(player)).getBoolean(path.get(player) + ".Firework")){
			fireWork(chest);
		}
		return I.get(pick);
	}
	public static HashMap<ItemStack, String> getItems(Player player){
		HashMap<ItemStack, String> items = new HashMap<ItemStack, String>();
		for(String reward : Main.settings.getFile(GUI.Crate.get(player)).getConfigurationSection("Crate.Prizes").getKeys(false)){
			Material mt = Material.matchMaterial(Main.settings.getFile(GUI.Crate.get(player)).getString("Crate.Prizes." + reward + ".DisplayItem"));
			int chance = Main.settings.getFile(GUI.Crate.get(player)).getInt("Crate.Prizes." + reward + ".Chance");
			ItemStack item = new ItemStack(mt);
			ItemMeta m = item.getItemMeta();
			m.setDisplayName(color(Main.settings.getFile(GUI.Crate.get(player)).getString("Crate.Prizes." + reward + ".DisplayName")));
			item.setItemMeta(m);
			Random number = new Random();
			int num;
			for(int counter = 1; counter<=1; counter++){
				num = 1 + number.nextInt(99);
				if(num >= 1 && num <= chance)items.put(item, "Crate.Prizes."+reward);
			}
		}
		return items;
	}
	public static ArrayList<ItemStack> getFinalItems(String reward, Player player){
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		for(String l : Main.settings.getFile(GUI.Crate.get(player)).getStringList(reward+".Items")){
			ArrayList<String> lore = new ArrayList<String>();
			HashMap<Enchantment, Integer> enchs = new HashMap<Enchantment, Integer>();
			String name = "";
			int amount = 1;
			String m = "Stone";
			for(String i : l.split(", ")){
				if(i.contains("Item:")){
					i = i.replaceAll("Item:", "");
					m=i;
				}
				if(i.contains("Name:")){
					i = i.replaceAll("Name:", "");
					i = i.replaceAll("_", " ");
					name = color(i);
				}
				if(i.contains("Amount:")){
					i = i.replaceAll("Amount:", "");
					amount = Integer.parseInt(i);
				}
				if(i.contains("Lore:")){
					i = i.replaceAll("Lore:", "");
					for(String L : i.split(",")){
						L = color(L);
						lore.add(L);
					}
				}
				for(String enc : getEnchants()){
					if(i.contains(enc+":")){
						String[] breakdown = i.split(":");
						int lvl = Integer.parseInt(breakdown[1]);
						enchs.put(Enchantment.getByName(enc), lvl);
					}
				}
			}
			items.add(makeItem(m, amount, name, lore, enchs));
		}
		return items;
	}
	public static ArrayList<String> getEnchants(){
		ArrayList<String> en = new ArrayList<String>();
		en.add("PROTECTION_ENVIRONMENTAL");
		en.add("PROTECTION_FIRE");
		en.add("PROTECTION_FALL");
		en.add("PROTECTION_EXPLOSIONS");
		en.add("ROTECTION_PROJECTILE");
		en.add("OXYGEN");
		en.add("WATER_WORKER");
		en.add("DAMAGE_ALL");
		en.add("DAMAGE_UNDEAD");
		en.add("DAMAGE_ARTHROPODS");
		en.add("KNOCKBACK");
		en.add("FIRE_ASPECT");
		en.add("LOOT_BONUS_MOBS");
		en.add("DIG_SPEED");
		en.add("SILK_TOUCH");
		en.add("DURABILITY");
		en.add("LOOT_BONUS_BLOCKS");
		en.add("ARROW_DAMAGE");
		en.add("ARROW_KNOCKBACK");
		en.add("ARROW_FIRE");
		en.add("ARROW_INFINITE");
		return en;
	}
	public static void fireWork(Location loc) {
		Firework fw = loc.getWorld().spawn(loc, Firework.class);
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
		detonate(fw);
	}
	private static void detonate(final Firework f) {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				f.detonate();
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
	public static ItemStack makeItem(String id, int amount, String name, List<String> lore){
		ArrayList<String> l = new ArrayList<String>();
		String ma = id;
		int type = 0;
		if(ma.contains(":")){
			String[] b = ma.split(":");
			ma = b[0];
			type = Integer.parseInt(b[1]);
		}
		Material material = Material.matchMaterial(ma);
		ItemStack item = new ItemStack(material, amount, (short) type);
		ItemMeta m = item.getItemMeta();
		m.setDisplayName(color(name));
		for(String L:lore)l.add(color(L));
		m.setLore(l);
		item.setItemMeta(m);
		return item;
	}
	public static ItemStack makeItem(Material material, int amount, int type, String name, List<String> lore){
		ArrayList<String> l = new ArrayList<String>();
		ItemStack item = new ItemStack(material, amount, (short) type);
		ItemMeta m = item.getItemMeta();
		m.setDisplayName(color(name));
		for(String L:lore)l.add(color(L));
		m.setLore(l);
		item.setItemMeta(m);
		return item;
	}
	public static ItemStack makeItem(String id, int amount, String name, List<String> lore, Map<Enchantment, Integer> enchants){
		ArrayList<String> l = new ArrayList<String>();
		String ma = id;
		int type = 0;
		if(ma.contains(":")){
			String[] b = ma.split(":");
			ma = b[0];
			type = Integer.parseInt(b[1]);
		}
		Material material = Material.matchMaterial(ma);
		ItemStack item = new ItemStack(material, amount, (short) type);
		ItemMeta m = item.getItemMeta();
		m.setDisplayName(color(name));
		for(String L:lore)l.add(color(L));
		m.setLore(l);
		item.setItemMeta(m);
		item.addUnsafeEnchantments(enchants);
		return item;
	}
	public static ItemStack makeItem(Material material, int amount, int type, String name, List<String> lore, Map<Enchantment, Integer> enchants){
		ArrayList<String> l = new ArrayList<String>();
		ItemStack item = new ItemStack(material, amount, (short) type);
		ItemMeta m = item.getItemMeta();
		m.setDisplayName(color(name));
		for(String L:lore)l.add(color(L));
		m.setLore(l);
		item.setItemMeta(m);
		item.addUnsafeEnchantments(enchants);
		return item;
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
		p.sendMessage(color("&cThat player is not online at this time."));
		return false;
	}
	public static void removeItem(ItemStack item, Player player){
		if(item.getAmount() <= 1){
			player.getInventory().removeItem(item);
		}
		if(item.getAmount() > 1){
			ItemStack i = item;
			i.setAmount(item.getAmount() - 1);
		}
	}
	public static boolean permCheck(Player player, String perm){
		if(!player.hasPermission("CrazyCrates." + perm)){
			player.sendMessage(color("&cYou do not have permission to use that command!"));
			return false;
		}
		return true;
	}
	public static int getKeys(Player player, String crate){
		String uuid = player.getUniqueId().toString();
		return Main.settings.getData().getInt("Players."+uuid+"."+crate);
	}
	public static void takeKeys(int Amount, Player player, String crate){
		String uuid = player.getUniqueId().toString();
		int keys = getKeys(player, crate);
		Main.settings.getData().set("Players."+uuid+"."+crate, keys-Amount);
		Main.settings.saveData();
	}
	public static void addKeys(int Amount, Player player, String crate, String Type){
		if(Main.settings.getLocations().getConfigurationSection("Locations")==null){
			Main.settings.getLocations().set("Locations.Clear", null);
			Main.settings.saveLocations();
		}
		if(Type.equals("Virtual")){
			String uuid = player.getUniqueId().toString();
			int keys = getKeys(player, crate);
			Main.settings.getData().set("Players."+uuid+"."+crate, keys+Amount);
			Main.settings.saveData();
			return;
		}
		if(Type.equals("Physical")){
			String name = color(Main.settings.getFile(crate).getString("Crate.PhysicalKey.Name"));
			List<String> lore = Main.settings.getFile(crate).getStringList("Crate.PhysicalKey.Lore");
			String ma = Main.settings.getFile(crate).getString("Crate.PhysicalKey.Item");
			int type = 0;
			if(ma.contains(":")){
				String[] b = ma.split(":");
				ma = b[0];
				type = Integer.parseInt(b[1]);
			}
			HashMap<Enchantment, Integer> Enchantments = new HashMap<Enchantment, Integer>();
			for(String en : Main.settings.getFile(crate).getStringList("Crate.PhysicalKey.Enchantments")){
				String[] breakdown = en.split(":");
				String enchantment = breakdown[0];
				int lvl = Integer.parseInt(breakdown[1]);
				Enchantments.put(Enchantment.getByName(enchantment), lvl);
			}
			player.getInventory().addItem(makeItem(Material.matchMaterial(ma), Amount, type, name, lore, Enchantments));
			return;
		}
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
		if(Api.getVersion()==183){
			OnePointEight.pasteSchematic(new File(plugin.getDataFolder()+"/Schematics/"+schem), loc);
		}
		if(Api.getVersion()==191){
			OnePointNine.pasteSchematic(new File(plugin.getDataFolder()+"/Schematics/"+schem), loc);
		}
	}
	public static List<Location> getLocations(String shem, Location loc){
		if(Api.getVersion()==183){
			return OnePointEight.getLocations(new File(plugin.getDataFolder()+"/Schematics/"+shem), loc);
		}
		else{
			return OnePointNine.getLocations(new File(plugin.getDataFolder()+"/Schematics/"+shem), loc);
		}
	}
	public static String pickRandomSchem(){
		File f = new File(plugin.getDataFolder()+"/Schematics/");
		String[] schems = f.list();
		ArrayList<String> schematics = new ArrayList<String>();
		for(String i : schems){
			schematics.add(i);
		}
		Random r = new Random();
		return schematics.get(r.nextInt(schematics.size()));
	}
}