package me.badbones69.crazycrates;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.badbones69.crazycrates.api.Crate;
import me.badbones69.crazycrates.api.CrateType;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.FireworkDamageAPI;
import me.badbones69.crazycrates.api.KeyType;
import me.badbones69.crazycrates.api.Messages;
import me.badbones69.crazycrates.cratetypes.CSGO;
import me.badbones69.crazycrates.cratetypes.Cosmic;
import me.badbones69.crazycrates.cratetypes.CrateOnTheGo;
import me.badbones69.crazycrates.cratetypes.QCC;
import me.badbones69.crazycrates.cratetypes.QuickCrate;
import me.badbones69.crazycrates.cratetypes.Roulette;
import me.badbones69.crazycrates.cratetypes.War;
import me.badbones69.crazycrates.cratetypes.Wheel;
import me.badbones69.crazycrates.cratetypes.Wonder;
import me.badbones69.crazycrates.multisupport.Events_v1_11_R1_Down;
import me.badbones69.crazycrates.multisupport.Events_v1_12_R1_Up;
import me.badbones69.crazycrates.multisupport.MVdWPlaceholderAPISupport;
import me.badbones69.crazycrates.multisupport.PlaceholderAPISupport;
import me.badbones69.crazycrates.multisupport.Support;
import me.badbones69.crazycrates.multisupport.Version;

public class Main extends JavaPlugin implements Listener{
	
	private Boolean updateChecker = false;
	public static CrazyCrates CC = CrazyCrates.getInstance();
	public static SettingsManager settings = SettingsManager.getInstance();
	
	@Override
	public void onEnable(){
		settings.setup(this);
		if(!settings.getLocations().contains("Locations")){
			settings.getLocations().set("Locations.Clear", null);
			settings.saveLocations();
		}
		if(!settings.getData().contains("Players")){
			settings.getData().set("Players.Clear", null);
			settings.saveData();
		}
		if(settings.getConfig().contains("Settings.Update-Checker")){
			updateChecker = settings.getConfig().getBoolean("Settings.Update-Checker");
		}else{
			updateChecker = true;
		}
		Methods.hasUpdate();
		CC.loadCrates();
		GUI.loadPreviews();
		PluginManager pm  = Bukkit.getPluginManager();
		pm.registerEvents(this, this);
		pm.registerEvents(new GUI(), this);
		pm.registerEvents(new QCC(), this);
		pm.registerEvents(new War(), this);
		pm.registerEvents(new CSGO(), this);
		pm.registerEvents(new Wheel(), this);
		pm.registerEvents(new Wonder(), this);
		pm.registerEvents(new Cosmic(), this);
		pm.registerEvents(new Roulette(), this);
		pm.registerEvents(new QuickCrate(), this);
		pm.registerEvents(new CrateControl(), this);
		pm.registerEvents(new CrateOnTheGo(), this);
		if(Version.getVersion().comparedTo(Version.v1_12_R1) >= 0){
			pm.registerEvents(new Events_v1_12_R1_Up(), this);
		}else {
			pm.registerEvents(new Events_v1_11_R1_Down(), this);
		}
		try{
			if(Version.getVersion().getVersionInteger() >= Version.v1_11_R1.getVersionInteger()){
				pm.registerEvents(new FireworkDamageAPI(this), this);
			}
		}catch(Exception e){}
		if(Support.hasPlaceholderAPI()){
			new PlaceholderAPISupport(this).hook();
		}
		if(Support.hasMVdWPlaceholderAPI()){
			MVdWPlaceholderAPISupport.registerPlaceholders(this);
		}
		if(Bukkit.getServer().getOnlinePlayers() != null){
			for(Player player : Bukkit.getServer().getOnlinePlayers()){
				String uuid = player.getUniqueId().toString();
				if(!Main.settings.getData().contains("Players." + uuid)){
					Main.settings.getData().set("Players." + uuid + ".Name", player.getName());
					for(String crate : Main.settings.getAllCratesNames()){
						int amount = Main.settings.getFile(crate).getInt("Crate.StartingKeys");
						Main.settings.getData().set("Players." + uuid + "." + crate, amount);
					}
					Main.settings.saveData();
				}
			}
		}
		try {
			new MCUpdate(this, true);
		} catch (IOException e) {}
	}
	
	@Override
	public void onDisable(){
		if(!QCC.crates.isEmpty()){
			for(Player player : QCC.crates.keySet()){
				QCC.undoBuild(player);
			}
		}
		if(!QuickCrate.Rewards.isEmpty()){
			for(Player player : QuickCrate.Rewards.keySet()){
				QuickCrate.Rewards.get(player).remove();
			}
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLable, String[] args){
		if(commandLable.equalsIgnoreCase("CrazyCrates")||commandLable.equalsIgnoreCase("CC")||commandLable.equalsIgnoreCase("Crate")
				|| commandLable.equalsIgnoreCase("Crates")||commandLable.equalsIgnoreCase("CCrate")||commandLable.equalsIgnoreCase("CrazyCrate")){
			if(args.length == 0){
				if(sender instanceof Player){
					if(!Methods.permCheck((Player)sender, "Access")){
						return true;
					}
				}else{
					sender.sendMessage(Methods.getPrefix() + Methods.color("&cYou must be a player to use this command."));
					return true;
				}
				GUI.openGUI((Player)sender);
				return true;
			}
			if(args.length >= 1){
				if(args[0].equalsIgnoreCase("Help")){
					if(sender instanceof Player)if(!Methods.permCheck((Player)sender, "Access"))return true;
					sender.sendMessage(Methods.color("&3&lCrazy Crates Help Menu"));
					sender.sendMessage(Methods.color("&6/CC &7- Opens the GUI."));
					sender.sendMessage(Methods.color("&6/CC Admin &7- Opens the Admin Keys GUI."));
					sender.sendMessage(Methods.color("&6/CC List &7- Lists all the Crates."));
					sender.sendMessage(Methods.color("&6/CC Open <Crate> [Player] &7- Opens a crate for a player."));
					sender.sendMessage(Methods.color("&6/CC Preview <Crate> [Player] &7- Opens a preview of a crate."));
					sender.sendMessage(Methods.color("&6/CC Tp <Location> &7- Teleport to a Crate."));
					sender.sendMessage(Methods.color("&6/CC Give <Physical/Virtual> <Crate> [Amount] [Player] &7- Give a player keys for a Chest."));
					sender.sendMessage(Methods.color("&6/CC GiveAll <Physical/Virtual> <Crate> [Amount] &7- Gives all online players keys for a Chest."));
					sender.sendMessage(Methods.color("&6/CC Set <Crate> &7- Set a block as a crate."));
					sender.sendMessage(Methods.color("&6/CC Reload &7- Reloads the Config and Data Files."));
					return true;
				}
				if(args[0].equalsIgnoreCase("Reload")){
					if(sender instanceof Player)if(!Methods.permCheck((Player)sender, "Admin"))return true;
					settings.reloadAll();
					settings.setup(this);
					if(!settings.getLocations().contains("Locations")){
						settings.getLocations().set("Locations.Clear", null);
						settings.saveLocations();
					}
					if(!settings.getData().contains("Players")){
						settings.getData().set("Players.Clear", null);
						settings.saveData();
					}
					CC.loadCrates();
					GUI.loadPreviews();
					sender.sendMessage(Messages.RELOAD.getMessage());
					return true;
				}
				if(args[0].equalsIgnoreCase("Admin")){
					if(!(sender instanceof Player))return true;
					Player player = (Player)sender;
					if(!Methods.permCheck(player, "Admin"))return true;
					int size=Methods.getCrates().size();
					int slots=9;
					for(;size>9;size-=9)slots+=9;
					Inventory inv = Bukkit.createInventory(null, slots, Methods.color("&4&lAdmin Keys"));
					HashMap<ItemStack, ItemStack> keys = new HashMap<ItemStack, ItemStack>();
					for(String crate : Methods.getCrates()){
						String name = settings.getFile(crate).getString("Crate.PhysicalKey.Name");
						List<String> lore = settings.getFile(crate).getStringList("Crate.PhysicalKey.Lore");
						String id = settings.getFile(crate).getString("Crate.PhysicalKey.Item");
						Boolean enchanted = false;
						if(settings.getFile(crate).contains("Crate.PhysicalKey.Glowing")){
							enchanted=settings.getFile(crate).getBoolean("Crate.PhysicalKey.Glowing");
						}
						lore.add("");
						lore.add("&7&l(&6&l!&7&l) Left click for Physical Key");
						lore.add("&7&l(&6&l!&7&l) Right click for Virtual Key");
						ItemStack item1 = Methods.makeItem(id, 1, name, lore, enchanted);
						ItemStack item2 = Methods.makeItem(id, 1, name, settings.getFile(crate).getStringList("Crate.PhysicalKey.Lore"), enchanted);
						inv.addItem(item1);
						keys.put(item1, item2);
					}
					CrateControl.previewKeys.put(player, keys);
					player.openInventory(inv);
					return true;
				}
				if(args[0].equalsIgnoreCase("List")){
					if(sender instanceof Player)if(!Methods.permCheck((Player) sender, "Admin"))return true;
					String crates = "";
					for(String vo : settings.getAllCratesNames()){
						crates += Methods.color("&a"+vo+"&8, ");
					}
					crates += Methods.color("&aMenu&8, ");
					crates = crates.substring(0, crates.length()-2);
					sender.sendMessage(Methods.color("&e&lCrates:&f "+crates));
					sender.sendMessage(Methods.color("&e&lAll Crate Locations:"));
					sender.sendMessage(Methods.color("&c[ID]&8, &c[Crate]&8, &c[World]&8, &c[X]&8, &c[Y]&8, &c[Z]"));
					int line = 1;
					if(Main.settings.getLocations().getConfigurationSection("Locations")==null){
						Main.settings.getLocations().set("Locations.Clear", null);
						Main.settings.saveLocations();
					}
					for(String i : settings.getLocations().getConfigurationSection("Locations").getKeys(false)){
						String crate = settings.getLocations().getString("Locations." + i + ".Crate");
						String W = settings.getLocations().getString("Locations." + i + ".World");
						String X = settings.getLocations().getString("Locations." + i + ".X");
						String Y = settings.getLocations().getString("Locations." + i + ".Y");
						String Z = settings.getLocations().getString("Locations." + i + ".Z");
						String msg = Methods.color("&8[&b" + line + "&8]: "+"&c"+i+"&8, &c"+crate+"&8, &c"+W+
								"&8, &c"+X+"&8, &c"+Y+"&8, &c"+Z);
						line++;
						sender.sendMessage(msg);
					}
					return true;
				}
			}
			if(args[0].equalsIgnoreCase("TP")){// /CC TP <Location>
				if(sender instanceof Player)if(!Methods.permCheck((Player)sender, "Admin"))return true;
				if(args.length==2){
					String Loc = args[1];
					if(Main.settings.getLocations().getConfigurationSection("Locations")==null){
						Main.settings.getLocations().set("Locations.Clear", null);
						Main.settings.saveLocations();
					}
					for(String name : settings.getLocations().getConfigurationSection("Locations").getKeys(false)){
						if(name.equalsIgnoreCase(Loc)){
							World W = Bukkit.getServer().getWorld(settings.getLocations().getString("Locations." + name + ".World"));
							int X = settings.getLocations().getInt("Locations." + name + ".X");
							int Y = settings.getLocations().getInt("Locations." + name + ".Y");
							int Z = settings.getLocations().getInt("Locations." + name + ".Z");
							Location loc = new Location(W, X, Y, Z);
							((Player)sender).teleport(loc.add(.5, 0, .5));
							sender.sendMessage(Methods.color(Methods.getPrefix()+"&7You have been teleported to &6"+name+"&7."));
							return true;
						}
					}
					sender.sendMessage(Methods.color(Methods.getPrefix() + "&cThere is no location called &6" + Loc + "&c."));
					return true;
				}
				sender.sendMessage(Methods.color(Methods.getPrefix()+"&c/CC TP <Location Name>"));
				return true;
			}
			if(args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("s")){ // /Crate Set <Crate>
				if(sender instanceof Player)if(!Methods.permCheck((Player)sender, "Admin"))return true;
				if(args.length == 2){
					Player player = (Player) sender;
					String id = "1"; //Location Name
					for(int i = 1; settings.getLocations().contains("Locations." + i); i++){
						id = (i + 1) + "";
					}
					String c = args[1]; //Crate
					for(String crate : Methods.getCrates()){
						if(crate.equalsIgnoreCase(c) || c.equalsIgnoreCase("Menu")){
							Block block = player.getTargetBlock((Set<Material>)null, 5);
							if(settings.getLocations().contains("Locations")){
								for(String location : settings.getLocations().getConfigurationSection("Locations").getKeys(false)){
									World w = Bukkit.getWorld(Main.settings.getLocations().getString("Locations." + location + ".World"));
									int x = Main.settings.getLocations().getInt("Locations." + location + ".X");
									int y = Main.settings.getLocations().getInt("Locations." + location + ".Y");
									int z = Main.settings.getLocations().getInt("Locations." + location + ".Z");
									Location loc = new Location(w, x, y, z);
									if(block.getLocation().equals(loc)){
										id = location;
										break;
									}
								}
							}
							if(c.equalsIgnoreCase("Menu")){
								crate = "Menu";
							}
							if(block.isEmpty()){
								player.sendMessage(Methods.color(Methods.getPrefix() + "&cYou must be looking at a block."));
								return true;
							}
							Location loc = block.getLocation();
							settings.getLocations().set("Locations." + id + ".Crate", crate);
							settings.getLocations().set("Locations." + id + ".World", loc.getWorld().getName());
							settings.getLocations().set("Locations." + id + ".X", loc.getBlockX());
							settings.getLocations().set("Locations." + id + ".Y", loc.getBlockY());
							settings.getLocations().set("Locations." + id + ".Z", loc.getBlockZ());
							settings.saveLocations();
							player.sendMessage(Methods.color(Methods.getPrefix() + "&7You have just set that block to " + crate + "."));
							sender.sendMessage(Methods.color(Methods.getPrefix() + "&7To remove the crate shift break in creative to remove."));
							return true;
						}
					}
					sender.sendMessage(Methods.color(Methods.getPrefix()+"&c" + c + " is not a Crate."));
					sender.sendMessage(Methods.color(Methods.getPrefix()+"&cThere is no Crates called &6" + c + "&c."));
					return true;
				}
				sender.sendMessage(Methods.color(Methods.getPrefix()+"&c/CC Set <Crate>"));
				return true;
			}
			if(args[0].equalsIgnoreCase("GiveAll")){// /Crate GiveAll <Physical/Virtual> <Crate> <Amount>
				if(sender instanceof Player)if(!Methods.permCheck((Player)sender, "Admin"))return true;
				if(args.length>=3){
					int amount = 1;
					if(args.length>=4){
						if(!Methods.isInt(args[3])){
							sender.sendMessage(Methods.color(Methods.getPrefix()+"&c"+args[3]+" is not a Number."));
							return true;
						}
						amount = Integer.parseInt(args[3]);
					}
					String type = args[1];
					if(!(type.equalsIgnoreCase("Virtual")||type.equalsIgnoreCase("V")||type.equalsIgnoreCase("Physical")||type.equalsIgnoreCase("P"))){
						sender.sendMessage(Methods.color(Methods.getPrefix()+"&cPlease use Virtual/V or Physical/P for a Key type."));
						return true;
					}
					Crate crate = CC.getCrateFromName(args[2]);
					if(crate != null) {
						if(crate.getCrateType() == CrateType.CRATE_ON_THE_GO){
							sender.sendMessage(Methods.color(Methods.getPrefix()+"&7You have given everyone &6"+amount+" &7Crates."));
						}else{
							sender.sendMessage(Methods.color(Methods.getPrefix()+"&7You have given everyone &6"+amount+" &7Keys."));
						}
						for(Player p : Bukkit.getServer().getOnlinePlayers()){
							if(crate.getCrateType() == CrateType.CRATE_ON_THE_GO){
								p.getInventory().addItem(crate.getKey());
								return true;
							}
							if(type.equalsIgnoreCase("Virtual") || type.equalsIgnoreCase("V")){
								Methods.addKeys(amount, p, crate, KeyType.VIRTUAL_KEY);
							}
							if(type.equalsIgnoreCase("Physical") || type.equalsIgnoreCase("P")){
								Methods.addKeys(amount, p, crate, KeyType.PHYSICAL_KEY);
							}
						}
						return true;
					}
					sender.sendMessage(Methods.color(Methods.getPrefix() + "&c" + args[2] + " is not a Crate."));
					return true;
					}
				sender.sendMessage(Methods.color(Methods.getPrefix()+"&c/Crate GiveAll <Physical/Virtual> <Crate> <Amount>"));
				return true;
			}
			if(args[0].equalsIgnoreCase("Preview")){// /CC Preview <Crate> [Player]
				if(sender instanceof Player){
					if(!Methods.permCheck((Player) sender, "preview")){
						return true;
					}
				}
				if(args.length >= 2){
					Crate crate = null;
					Player player = null;
					for(Crate c : CC.getCrates()){
						if(c.getName().equalsIgnoreCase(args[1])){
							crate = c;
						}
					}
					if(crate != null){
						if(args.length >= 3){
							if(Methods.isOnline(args[2], sender)){
								player = Methods.getPlayer(args[2]);
							}else{
								return true;
							}
						}else{
							if(!(sender instanceof Player)){
								sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/Crate Preview <Crate> [Player]"));
								return true;
							}else{
								player = (Player) sender;
							}
						}
						GUI.openPreview(player, crate);
						return true;
					}
				}
				sender.sendMessage(Methods.color(Methods.getPrefix()+"&c/Crate Preview <Crate> [Player]"));
				return true;
			}
			if(args[0].equalsIgnoreCase("Open")){// /CC Open <Crate> [Player]
				if(sender instanceof Player)if(!Methods.permCheck((Player) sender, "Admin"))return true;
				if(args.length>=2){
					for(String crate : Methods.getCrates()){
						if(crate.equalsIgnoreCase(args[1])){
							Player player = null;
							if(args.length>=3){
								if(Methods.isOnline(args[2], sender)){
									player = Methods.getPlayer(args[2]);
								}else{
									return true;
								}
							}else{
								if(!(sender instanceof Player)){
									sender.sendMessage(Methods.color(Methods.getPrefix()+"&c/Crate Open <Crate> [Player]"));
									return true;
								}else{
									player = (Player) sender;
								}
							}
							if(GUI.crates.containsKey(player)){
								sender.sendMessage(Messages.CRATE_ALREADY_OPENED.getMessage());
								return true;
							}
							CrateType type = CrateType.getFromName(Main.settings.getFile(crate).getString("Crate.CrateType"));
							if(type != null){
								if(type != CrateType.CRATE_ON_THE_GO && type != CrateType.QUICK_CRATE && type != CrateType.FIRE_CRACKER){
									for(Crate c : Main.CC.getCrates()){
										if(c.getName().equalsIgnoreCase(crate)){
											CrateControl.crates.put(player, c);
											GUI.crates.put(player, c);
										}
									}
									Methods.Key.put(player, KeyType.FREE_KEY);
									CC.openCrate(player, type, player.getLocation());
									sender.sendMessage(Methods.color(Methods.getPrefix() + "&7You have just opened the &6" + crate + " &7crate for &6" + player.getName() + "&7."));
									return true;
								}else{
									sender.sendMessage(Messages.CANT_BE_A_VIRTUAL_CRATE.getMessage());
									return true;
								}
							}else{
								sender.sendMessage(Methods.color(Methods.getPrefix() + "&c" + args[1] + " is not a Crate."));
								return true;
							}
						}
					}
					sender.sendMessage(Methods.color(Methods.getPrefix()+"&c"+args[1]+" is not a Crate."));
					return true;
				}
				sender.sendMessage(Methods.color(Methods.getPrefix()+"&c/Crate Open <Crate> [Player]"));
				return true;
			}
			if(args[0].equalsIgnoreCase("Give")){// /Crate Give <Physical/Virtual> <Crate> [Amount] [Player]
				if(sender instanceof Player)if(!Methods.permCheck((Player)sender, "Admin"))return true;
				KeyType type = KeyType.getFromName(args[1]);
				if(type == null || type == KeyType.FREE_KEY) {
					sender.sendMessage(Methods.color(Methods.getPrefix()+"&cPlease use Virtual/V or Physical/P for a Key type."));
					return true;
				}
				if(args.length == 3){
					Crate crate = CC.getCrateFromName(args[2]);
					if(crate != null) {
						if(!(sender instanceof Player)){
							sender.sendMessage(Methods.color(Methods.getPrefix() + "&cYou must be a player to run this command."));
							return true;
						}
						if(crate.getCrateType() == CrateType.CRATE_ON_THE_GO){
							sender.sendMessage(Methods.color(Methods.getPrefix()+"&7You have given &6"+ sender.getName()+" "+1+" &7Crates."));
							((Player)sender).getInventory().addItem(crate.getKey());
							return true;
						}
						sender.sendMessage(Methods.color(Methods.getPrefix()+"&7You have given &6"+ sender.getName()+" "+1+" &7Keys."));
						if(type == KeyType.VIRTUAL_KEY){
							Methods.addKeys(1, (Player)sender, crate, KeyType.VIRTUAL_KEY);
						}else if(type == KeyType.PHYSICAL_KEY){
							Methods.addKeys(1, (Player)sender, crate, KeyType.PHYSICAL_KEY);
						}
						return true;
					}
					sender.sendMessage(Methods.color(Methods.getPrefix()+"&c"+args[2]+" is not a Crate."));
					return true;
				}
				if(args.length == 4){
					if(!Methods.isInt(args[3])){
						sender.sendMessage(Methods.color(Methods.getPrefix()+"&c"+args[3]+" is not a Number."));
						return true;
					}
					int amount = Integer.parseInt(args[3]);
					Crate crate = CC.getCrateFromName(args[2]);
					if(crate != null) {
						if(crate.getCrateType() == CrateType.CRATE_ON_THE_GO){
							sender.sendMessage(Methods.color(Methods.getPrefix()+"&7You have given &6"+ sender.getName()+" "+amount+" &7Crates."));
							((Player)sender).getInventory().addItem(crate.getKey());
							return true;
						}
						sender.sendMessage(Methods.color(Methods.getPrefix()+"&7You have given &6"+ sender.getName()+" "+amount+" &7Keys."));
						if(type == KeyType.VIRTUAL_KEY){
							Methods.addKeys(amount, (Player)sender, crate, KeyType.VIRTUAL_KEY);
						}else if(type == KeyType.PHYSICAL_KEY){
							Methods.addKeys(amount, (Player)sender, crate, KeyType.PHYSICAL_KEY);
						}
						return true;
					}
					sender.sendMessage(Methods.color(Methods.getPrefix()+"&c"+args[2]+" is not a Crate."));
					return true;
				}
				if(args.length == 5){
					if(!Methods.isInt(args[3])){
						sender.sendMessage(Methods.color(Methods.getPrefix()+"&c"+args[3]+" is not a Number."));
						return true;
					}
					int amount = Integer.parseInt(args[3]);
					Player target = Methods.getPlayer(args[4]);
					Crate crate = CC.getCrateFromName(args[2]);
					if(crate != null) {
						if(crate.getCrateType() == CrateType.CRATE_ON_THE_GO){
							sender.sendMessage(Methods.color(Methods.getPrefix()+"&7You have given &6"+target.getName()+" "+amount+" &7Crates."));
							((Player)sender).getInventory().addItem(crate.getKey());
							return true;
						}
						
						if(type == KeyType.VIRTUAL_KEY){
							if(target != null) {
								Methods.addKeys(amount, target, crate, KeyType.VIRTUAL_KEY);
							}else {
								if(!CC.addOfflineKeys(args[4], crate, amount)) {
									sender.sendMessage(Methods.getPrefix("&cAn internal error has occurred. Please check the console for the full error."));
									return true;
								}else {
									sender.sendMessage(Methods.getPrefix("&7You have given the offline player " + args[4] + " " + amount + " keys."));
									return true;
								}
							}
						}else if(type == KeyType.PHYSICAL_KEY){
							if(target != null) {
								Methods.addKeys(amount, target, crate, KeyType.PHYSICAL_KEY);
							}else {
								if(!CC.addOfflineKeys(args[4], crate, amount)) {
									sender.sendMessage(Methods.getPrefix("&cAn internal error has occurred. Please check the console for the full error."));
									return true;
								}else {
									sender.sendMessage(Methods.getPrefix("&7You have given the offline player " + args[4] + " " + amount + " keys."));
									return true;
								}
							}
						}
						sender.sendMessage(Methods.color(Methods.getPrefix()+"&7You have given &6"+target.getName()+" "+amount+" &7Keys."));
						return true;
					}
					sender.sendMessage(Methods.color(Methods.getPrefix()+"&c"+args[2]+" is not a Crate."));
					return true;
				}
				sender.sendMessage(Methods.color(Methods.getPrefix()+"&c/Crate Give <Physical/Virtual> <Crate> [Amount] [Player]"));
				return true;
			}
		}
		sender.sendMessage(Methods.color(Methods.getPrefix()+"&cPlease do /CC Help for more info."));
		return false;
	}
	
	public static Plugin getPlugin(){
		return Bukkit.getPluginManager().getPlugin("CrazyCrates");
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		Player player = e.getPlayer();
		CC.loadOfflinePlayersKeys(player);
		new BukkitRunnable(){
			@Override
			public void run() {
				if(player.getName().equals("BadBones69")){
					player.sendMessage(Methods.getPrefix()+Methods.color("&7This server is running your Crazy Crates Plugin. "
						+ "&7It is running version &av"+Bukkit.getServer().getPluginManager().getPlugin("CrazyCrates").getDescription().getVersion()+"&7."));
				}
				if(player.isOp() && updateChecker){
					Methods.hasUpdate(player);
				}
			}
		}.runTaskLaterAsynchronously(this, 40);
	}
	
}