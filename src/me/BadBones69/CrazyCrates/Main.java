package me.BadBones69.CrazyCrates;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

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
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.BadBones69.CrazyCrates.CrateTypes.CSGO;
import me.BadBones69.CrazyCrates.CrateTypes.Cosmic;
import me.BadBones69.CrazyCrates.CrateTypes.CrateOnTheGo;
import me.BadBones69.CrazyCrates.CrateTypes.QCC;
import me.BadBones69.CrazyCrates.CrateTypes.QuickCrate;
import me.BadBones69.CrazyCrates.CrateTypes.Roulette;
import me.BadBones69.CrazyCrates.CrateTypes.Wheel;
import me.BadBones69.CrazyCrates.CrateTypes.Wonder;

public class Main extends JavaPlugin implements Listener{
	public static SettingsManager settings = SettingsManager.getInstance();
	private static HashMap<Player, HashMap<ItemStack, ItemStack>> Keys = new HashMap<Player, HashMap<ItemStack, ItemStack>>();
	@Override
	public void onDisable(){
		if(!QCC.crates.isEmpty()){
			for(Player player : QCC.crates.keySet()){
				QCC.undoBuild(player);
			}
		}
		if(!QuickCrate.Reward.isEmpty()){
			for(Player player : QuickCrate.Reward.keySet()){
				QuickCrate.Reward.get(player).remove();
			}
		}
	}
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
		Api.hasUpdate();
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		Bukkit.getServer().getPluginManager().registerEvents(new CC(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new GUI(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new QCC(this), this);
		Bukkit.getServer().getPluginManager().registerEvents(new CSGO(this), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Wheel(this), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Wonder(this), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Cosmic(this), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Roulette(this), this);
		Bukkit.getServer().getPluginManager().registerEvents(new CrateOnTheGo(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new QuickCrate(this), this);
		if(Bukkit.getServer().getOnlinePlayers()!=null){
			for(Player player : Bukkit.getServer().getOnlinePlayers()){
				String uuid = player.getUniqueId().toString();
				if(!Main.settings.getData().contains("Players."+uuid)){
					Main.settings.getData().set("Players."+uuid+".Name", player.getName());
					for(String crate : Main.settings.getAllCratesNames()){
						int amount = Main.settings.getFile(crate).getInt("Crate.StartingKeys");
						Main.settings.getData().set("Players."+uuid+"."+crate, amount);
					}
					Main.settings.saveData();
				}
			}
		}
		try {
			Metrics metrics = new Metrics(this); metrics.start();
		} catch (IOException e) {
			System.out.println("Error Submitting stats!");
		}
	}
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String commandLable, String[] args){
		if(commandLable.equalsIgnoreCase("CrazyCrates")||commandLable.equalsIgnoreCase("CC")||commandLable.equalsIgnoreCase("Crate")
				||commandLable.equalsIgnoreCase("CCrate")||commandLable.equalsIgnoreCase("CrazyCrate")){
			if(args.length == 0){
				if(sender instanceof Player)if(!Api.permCheck((Player)sender, "Access"))return true;
				GUI.openGUI((Player)sender);
				return true;
			}
			if(args.length >= 1){
				if(args[0].equalsIgnoreCase("Help")){
					if(sender instanceof Player)if(!Api.permCheck((Player)sender, "Access"))return true;
					sender.sendMessage(Api.color("&3&lCrazy Crates Help Menu"));
					sender.sendMessage(Api.color("&6/CC &7- Opens the GUI."));
					sender.sendMessage(Api.color("&6/CC Admin &7- Opens the Admin Keys GUI."));
					sender.sendMessage(Api.color("&6/CC List &7- Lists all the Crates."));
					sender.sendMessage(Api.color("&6/CC Open <Crate> [Player] &7- Opens a crate for a player."));
					sender.sendMessage(Api.color("&6/CC Tp <Location> &7- Teleport to a Crate."));
					sender.sendMessage(Api.color("&6/CC Give <Physical/Virtual> <Crate> [Amount] [Player] &7- Give a player keys for a Chest."));
					sender.sendMessage(Api.color("&6/CC GiveAll <Physical/Virtual> <Crate> [Amount] &7- Gives all online players keys for a Chest."));
					sender.sendMessage(Api.color("&6/CC Create <Location Name> <Crate> &7- Set the block you are looking at as a crate."));
					sender.sendMessage(Api.color("&6/CC Set <Location Name> <Crate> &7- Change a Locations Crate Type."));
					sender.sendMessage(Api.color("&6/CC Remove <Location Name> &7- Delete a Crate Location."));
					sender.sendMessage(Api.color("&6/CC Reload &7- Reloads the Config and Data Files."));
					return true;
				}
				if(args[0].equalsIgnoreCase("Reload")){
					if(sender instanceof Player)if(!Api.permCheck((Player)sender, "Admin"))return true;
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
					sender.sendMessage(Api.color(Api.getPrefix()+settings.getConfig().getString("Settings.Reload")));
					return true;
				}
				if(args[0].equalsIgnoreCase("Admin")){
					if(!(sender instanceof Player))return true;
					Player player = (Player)sender;
					if(!Api.permCheck(player, "Admin"))return true;
					int size=Api.getCrates().size();
					int slots=9;
					for(;size>9;size-=9)slots+=9;
					Inventory inv = Bukkit.createInventory(null, slots, Api.color("&4&lAdmin Keys"));
					HashMap<ItemStack, ItemStack> keys = new HashMap<ItemStack, ItemStack>();
					for(String crate : Api.getCrates()){
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
						ItemStack item1 = Api.makeItem(id, 1, name, lore, enchanted);
						ItemStack item2 = Api.makeItem(id, 1, name, settings.getFile(crate).getStringList("Crate.PhysicalKey.Lore"), enchanted);
						inv.addItem(item1);
						keys.put(item1, item2);
					}
					Keys.put(player, keys);
					player.openInventory(inv);
					return true;
				}
				if(args[0].equalsIgnoreCase("List")){
					if(sender instanceof Player)if(!Api.permCheck((Player) sender, "Admin"))return true;
					String crates = "";
					for(String vo : settings.getAllCratesNames()){
						crates += Api.color("&a"+vo+"&8, ");
					}
					crates += Api.color("&aMenu&8, ");
					crates = crates.substring(0, crates.length()-2);
					sender.sendMessage(Api.color("&e&lCrates:&f "+crates));
					sender.sendMessage(Api.color("&e&lAll Crate Locations:"));
					sender.sendMessage(Api.color("&c[Locations Name]&8, &c[Crate]&8, &c[World]&8, &c[X]&8, &c[Y]&8, &c[Z]"));
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
						String msg = Api.color("&8[&b" + line + "&8]: "+"&c"+i+"&8, &c"+crate+"&8, &c"+W+
								"&8, &c"+X+"&8, &c"+Y+"&8, &c"+Z);
						line++;
						sender.sendMessage(msg);
					}
					return true;
				}
			}
			if(args[0].equalsIgnoreCase("TP")){// /CC TP <Location>
				if(sender instanceof Player)if(!Api.permCheck((Player)sender, "Admin"))return true;
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
							Location loc = new Location(W,X,Y,Z);
							((Player)sender).teleport(loc.add(.5, 0, .5));
							sender.sendMessage(Api.color(Api.getPrefix()+"&7You have been teleported to &6"+name+"&7."));
							return true;
						}
					}
					sender.sendMessage(Api.color(Api.getPrefix()+"&cThere is no location called &6"+Loc+"76."));
					return true;
				}
				sender.sendMessage(Api.color(Api.getPrefix()+"&c/CC TP <Location Name>"));
				return true;
			}
			if(args[0].equalsIgnoreCase("Delete")||args[0].equalsIgnoreCase("Del")||args[0].equalsIgnoreCase("Remove")||args[0].equalsIgnoreCase("R")){// /Crate Delete <Location Name>
				if(sender instanceof Player)if(!Api.permCheck((Player)sender, "Admin"))return true;
				if(args.length==2){
					String LN = args[1]; //Location Name
					if(settings.getLocations().getConfigurationSection("Locations") == null){
						settings.getLocations().set("Locations.clear", null);
						settings.saveLocations();
					}
					if(Main.settings.getLocations().getConfigurationSection("Locations")==null){
						Main.settings.getLocations().set("Locations.Clear", null);
						Main.settings.saveLocations();
					}
					for(String location : settings.getLocations().getConfigurationSection("Locations").getKeys(false)){
						if(location.equalsIgnoreCase(LN)){
							settings.getLocations().set("Locations."+location, null);
							settings.saveLocations();
							sender.sendMessage(Api.color(Api.getPrefix()+"&7You have just removed &6"+location+"&7."));
							return true;
						}
					}
					sender.sendMessage(Api.color(Api.getPrefix()+"&cThere is no Location called &6"+LN+"&c."));
					return true;
				}
				sender.sendMessage(Api.color(Api.getPrefix()+"&c/CC Remove <Location Name>"));
				return true;
			}
			if(args[0].equalsIgnoreCase("Set")||args[0].equalsIgnoreCase("S")){ // /Crate Set <Location Name> <Crate>
				if(sender instanceof Player)if(!Api.permCheck((Player)sender, "Admin"))return true;
				if(args.length==3){
					String LN = args[1]; //Location Name
					String C = args[2]; //Crate
					for(String crate : Api.getCrates()){
						if(crate.equalsIgnoreCase(C)||C.equalsIgnoreCase("Menu")){
							if(settings.getLocations().getConfigurationSection("Locations") == null){
								settings.getLocations().set("Locations.clear", null);
								settings.saveLocations();
								sender.sendMessage(Api.color(Api.getPrefix()+"&cThere is no Location called &6"+LN+"&c."));
								return true;
							}
							if(C.equalsIgnoreCase("Menu")){
								crate = "Menu";
							}
							for(String location : settings.getLocations().getConfigurationSection("Locations").getKeys(false)){
								if(location.equalsIgnoreCase(LN)){
									settings.getLocations().set("Locations."+location+".Crate", crate);
									settings.saveLocations();
									sender.sendMessage(Api.color(Api.getPrefix()+"&7You have just set &6"+location+" &7as a &6"+crate+" &7Crate."));
									return true;
								}
							}
							sender.sendMessage(Api.color(Api.getPrefix()+"&cThere is no Location called &6"+LN+"&c."));
							return true;
						}
					}
					sender.sendMessage(Api.color(Api.getPrefix()+"&cThere is no Crates called &6"+C+"&c."));
					return true;
				}
				sender.sendMessage(Api.color(Api.getPrefix()+"&c/CC Set <Location Name> <Crate>"));
				return true;
			}
			if(args[0].equalsIgnoreCase("Create")){ // /Crate Create <Location Name> <Crate>
				if(sender instanceof Player)if(!Api.permCheck((Player)sender, "Admin"))return true;
				if(args.length==3){
					Player player = (Player) sender;
					String LN = args[1]; //Location Name
					String C = args[2]; //Crate
					for(String crate : Api.getCrates()){
						if(crate.equalsIgnoreCase(C)||C.equalsIgnoreCase("Menu")){
							if(settings.getLocations().contains("Locations")){
								for(String name : settings.getLocations().getConfigurationSection("Locations").getKeys(false)){
									if(name.equalsIgnoreCase(LN)){
										player.sendMessage(Api.color(Api.getPrefix()+"&6"+name+" &calready exists."));
										return true;
									}
								}
							}
							if(C.equalsIgnoreCase("Menu")){
								crate = "Menu";
							}
							Block block = player.getTargetBlock((HashSet<Byte>)null, 5);
							if(block.isEmpty()){
								player.sendMessage(Api.color(Api.getPrefix()+"&cYou must be looking at a block."));
								return true;
							}
							Location loc = block.getLocation();
							settings.getLocations().set("Locations."+LN+".Crate", crate);
							settings.getLocations().set("Locations."+LN+".World", loc.getWorld().getName());
							settings.getLocations().set("Locations."+LN+".X", loc.getBlockX());
							settings.getLocations().set("Locations."+LN+".Y", loc.getBlockY());
							settings.getLocations().set("Locations."+LN+".Z", loc.getBlockZ());
							settings.saveLocations();
							player.sendMessage(Api.color(Api.getPrefix()+"&7You have just created a new Crate Location."));
							return true;
						}
					}
					sender.sendMessage(Api.color(Api.getPrefix()+"&c"+C+" is is not a Crate."));
					return true;
				}
				sender.sendMessage(Api.color(Api.getPrefix()+"&c/Crate Create <Location Name> <Crate>"));
				return true;
			}
			if(args[0].equalsIgnoreCase("GiveAll")){// /Crate GiveAll <Physical/Virtual> <Crate> <Amount>
				if(sender instanceof Player)if(!Api.permCheck((Player)sender, "Admin"))return true;
				if(args.length>=3){
					int amount = 1;
					if(args.length>=4){
						if(!Api.isInt(args[3])){
							sender.sendMessage(Api.color(Api.getPrefix()+"&c"+args[3]+" is is not a Number."));
							return true;
						}
						amount = Integer.parseInt(args[3]);
					}
					String type = args[1];
					if(!(type.equalsIgnoreCase("Virtual")||type.equalsIgnoreCase("V")||type.equalsIgnoreCase("Physical")||type.equalsIgnoreCase("P"))){
						sender.sendMessage(Api.color(Api.getPrefix()+"&cPlease use Virtual/V or Physical/P for a Key type."));
						return true;
					}
					for(String crate : Api.getCrates()){
						if(crate.equalsIgnoreCase(args[2])){
							if(settings.getFile(crate).getString("Crate.CrateType").equalsIgnoreCase("CrateOnTheGo")){
								sender.sendMessage(Api.color(Api.getPrefix()+"&7You have given everyone &6"+amount+" &7Crates."));
							}else{
								sender.sendMessage(Api.color(Api.getPrefix()+"&7You have given everyone &6"+amount+" &7Keys."));
							}
							for(Player p : Bukkit.getServer().getOnlinePlayers()){
								if(settings.getFile(crate).getString("Crate.CrateType").equalsIgnoreCase("CrateOnTheGo")){
									CrateOnTheGo.giveCrate(p, amount, crate);
									return true;
								}
								if(type.equalsIgnoreCase("Virtual")||type.equalsIgnoreCase("V")){
									Api.addKeys(amount, p, crate, "Virtual");
								}
								if(type.equalsIgnoreCase("Physical")||type.equalsIgnoreCase("P")){
									Api.addKeys(amount, p, crate, "Physical");
								}
							}
							return true;
						}
					}
					sender.sendMessage(Api.color(Api.getPrefix()+"&c"+args[1]+" is is not a Crate."));
					return true;
					}
				sender.sendMessage(Api.color(Api.getPrefix()+"&c/Crate GiveAll <Physical/Virtual> <Crate> <Amount>"));
				return true;
			}
			if(args[0].equalsIgnoreCase("Open")){// /CC Open <Crate> [Player]
				if(sender instanceof Player)if(!Api.permCheck((Player) sender, "Admin"))return true;
				if(args.length>=2){
					for(String crate : Api.getCrates()){
						if(crate.equalsIgnoreCase(args[1])){
							Player player = null;
							String type = Main.settings.getFile(crate).getString("Crate.CrateType");
							if(args.length>=3){
								if(Api.isOnline(args[2], sender)){
									player = Api.getPlayer(args[2]);
								}else{
									return true;
								}
							}else{
								if(!(sender instanceof Player)){
									sender.sendMessage(Api.color(Api.getPrefix()+"&c/Crate Open <Crate> [Player]"));
									return true;
								}else{
									player = (Player) sender;
								}
							}
							if(GUI.Crate.containsKey(player)){
								sender.sendMessage(Api.color(Api.getPrefix()+Main.settings.getConfig().getString("Settings.Crate-Already-Opened")));
								return true;
							}
							if(type.equalsIgnoreCase("Wheel")){
								GUI.Crate.put(player, crate);
								CC.Crate.put(player, crate);
								Api.Key.put(player, "Free");
								Wheel.startWheel(player);
							}
							if(type.equalsIgnoreCase("Wonder")){
								GUI.Crate.put(player, crate);
								CC.Crate.put(player, crate);
								Api.Key.put(player, "Free");
								Wonder.startWonder(player);
							}
							if(type.equalsIgnoreCase("Cosmic")){
								GUI.Crate.put(player, crate);
								CC.Crate.put(player, crate);
								Api.Key.put(player, "Free");
								Cosmic.openCosmic(player);
							}
							if(type.equalsIgnoreCase("QuadCrate")){
								GUI.Crate.put(player, crate);
								CC.Crate.put(player, crate);
								Api.Key.put(player, "Free");
								QCC.startBuild(player, player.getLocation(), Material.CHEST);
							}
							if(type.equalsIgnoreCase("CSGO")){
								GUI.Crate.put(player, crate);
								CC.Crate.put(player, crate);
								Api.Key.put(player, "Free");
								CSGO.openCSGO(player);
							}
							if(type.equalsIgnoreCase("Roulette")){
								GUI.Crate.put(player, crate);
								CC.Crate.put(player, crate);
								Api.Key.put(player, "Free");
								Roulette.openRoulette(player);
							}
							if(type.equalsIgnoreCase("QuickCrate")){
								sender.sendMessage(Api.color(Api.getPrefix()+Main.settings.getConfig().getString("Settings.Cant-Be-Virtual-Crate")));
								return true;
							}
							if(type.equalsIgnoreCase("CrateOnTheGo")){
								sender.sendMessage(Api.color(Api.getPrefix()+Main.settings.getConfig().getString("Settings.Cant-Be-Virtual-Crate")));
								return true;
							}
							if(type.equalsIgnoreCase("FireCracker")){
								sender.sendMessage(Api.color(Api.getPrefix()+Main.settings.getConfig().getString("Settings.Cant-Be-Virtual-Crate")));
								return true;
							}
							sender.sendMessage(Api.color(Api.getPrefix()+"&7You have just opened the &6"+crate+" &7crate for &6"+player.getName()+"&7."));
							return true;
						}
					}
					sender.sendMessage(Api.color(Api.getPrefix()+"&c"+args[1]+" is is not a Crate."));
					return true;
				}
				sender.sendMessage(Api.color(Api.getPrefix()+"&c/Crate Open <Crate> [Player]"));
				return true;
			}
			if(args[0].equalsIgnoreCase("Give")){// /Crate Give <Physical/Virtual> <Crate> [Amount] [Player]
				if(sender instanceof Player)if(!Api.permCheck((Player)sender, "Admin"))return true;
				if(args.length==3){
					String type = args[1];
					for(String crate : Api.getCrates()){
						if(crate.equalsIgnoreCase(args[2])){
							if(settings.getFile(crate).getString("Crate.CrateType").equalsIgnoreCase("CrateOnTheGo")){
								sender.sendMessage(Api.color(Api.getPrefix()+"&7You have given &6"+ sender.getName()+" "+1+" &7Crates."));
								CrateOnTheGo.giveCrate((Player)sender, 1, crate);
								return true;
							}
							sender.sendMessage(Api.color(Api.getPrefix()+"&7You have given &6"+ sender.getName()+" "+1+" &7Keys."));
							if(type.equalsIgnoreCase("Virtual")||type.equalsIgnoreCase("V")){
								Api.addKeys(1, (Player)sender, crate, "Virtual");
							}
							if(type.equalsIgnoreCase("Physical")||type.equalsIgnoreCase("P")){
								Api.addKeys(1, (Player)sender, crate, "Physical");
							}
							return true;
						}
					}
					sender.sendMessage(Api.color(Api.getPrefix()+"&c"+args[2]+" is is not a Crate."));
					return true;
				}
				if(args.length==4){
					String type = args[1];
					int amount = Integer.parseInt(args[3]);
					for(String crate : Api.getCrates()){
						if(crate.equalsIgnoreCase(args[2])){
							if(settings.getFile(crate).getString("Crate.CrateType").equalsIgnoreCase("CrateOnTheGo")){
								sender.sendMessage(Api.color(Api.getPrefix()+"&7You have given &6"+ sender.getName()+" "+amount+" &7Crates."));
								CrateOnTheGo.giveCrate((Player)sender, amount, crate);
								return true;
							}
							sender.sendMessage(Api.color(Api.getPrefix()+"&7You have given &6"+ sender.getName()+" "+amount+" &7Keys."));
							if(type.equalsIgnoreCase("Virtual")||type.equalsIgnoreCase("V")){
								Api.addKeys(amount, (Player)sender, crate, "Virtual");
							}
							if(type.equalsIgnoreCase("Physical")||type.equalsIgnoreCase("P")){
								Api.addKeys(amount, (Player)sender, crate, "Physical");
							}
							return true;
						}
					}
					sender.sendMessage(Api.color(Api.getPrefix()+"&c"+args[2]+" is is not a Crate."));
					return true;
				}
				if(args.length==5){
					String type = args[1];
					if(!Api.isOnline(args[4], sender))return true;
					if(!Api.isInt(args[3])){
						sender.sendMessage(Api.color(Api.getPrefix()+"&c"+args[3]+" is is not a Number."));
						return true;
					}
					if(!(type.equalsIgnoreCase("Virtual")||type.equalsIgnoreCase("V")||type.equalsIgnoreCase("Physical")||type.equalsIgnoreCase("P"))){
						sender.sendMessage(Api.color(Api.getPrefix()+"&cPlease use Virtual/V or Physical/P for a Key type."));
						return true;
					}
					int amount = Integer.parseInt(args[3]);
					Player target = Api.getPlayer(args[4]);
					for(String crate : Api.getCrates()){
						if(crate.equalsIgnoreCase(args[2])){
							if(settings.getFile(crate).getString("Crate.CrateType").equalsIgnoreCase("CrateOnTheGo")){
								sender.sendMessage(Api.color(Api.getPrefix()+"&7You have given &6"+target.getName()+" "+amount+" &7Crates."));
								CrateOnTheGo.giveCrate(target, amount, crate);
								return true;
							}
							sender.sendMessage(Api.color(Api.getPrefix()+"&7You have given &6"+target.getName()+" "+amount+" &7Keys."));
							if(type.equalsIgnoreCase("Virtual")||type.equalsIgnoreCase("V")){
								Api.addKeys(amount, target, crate, "Virtual");
							}
							if(type.equalsIgnoreCase("Physical")||type.equalsIgnoreCase("P")){
								Api.addKeys(amount, target, crate, "Physical");
							}
							return true;
						}
					}
					sender.sendMessage(Api.color(Api.getPrefix()+"&c"+args[2]+" is is not a Crate."));
					return true;
				}
				sender.sendMessage(Api.color(Api.getPrefix()+"&c/Crate Give <Physical/Virtual> <Crate> [Amount] [Player]"));
				return true;
			}
		}
		sender.sendMessage(Api.color(Api.getPrefix()+"&cPlease do /CC Help for more info."));
		return false;
	}
	@EventHandler
	public void onInvClick(InventoryClickEvent e){
		Inventory inv = e.getInventory();
		Player player = (Player)e.getWhoClicked();
		if(inv!=null){
			if(inv.getName().equals(Api.color("&4&lAdmin Keys"))){
				e.setCancelled(true);
				if(!Api.permCheck(player, "Admin")){
					player.closeInventory();
					return;
				}
				int slot = e.getRawSlot();
				if(slot<inv.getSize()){
					if(e.getAction()==InventoryAction.PICKUP_ALL){
						ItemStack item = inv.getItem(slot);
						if(item!=null){
							if(item.getType()!=Material.AIR){
								for(String crate : Api.getCrates()){
									if(settings.getFile(crate).getString("Crate.CrateType").equalsIgnoreCase("CrateOnTheGo")){
										CrateOnTheGo.giveCrate(player, 1, crate);
										return;
									}
								}
								player.getInventory().addItem(Keys.get(player).get(item));
							}
						}
					}
					if(e.getAction()==InventoryAction.PICKUP_HALF){
						ItemStack item = inv.getItem(slot);
						for(String crate : Api.getCrates()){
							String name = settings.getFile(crate).getString("Crate.PhysicalKey.Name");
							if(item.getItemMeta().getDisplayName().equals(Api.color(name))){
								Api.addKeys(1, player, crate, "Virtual");
								player.sendMessage(Api.getPrefix()+Api.color("&a&l+1 "+name));
								return;
							}
						}
					}
				}
			}
		}
	}
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		final Player player = e.getPlayer();
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
			@Override
			public void run() {
				if(player.getName().equals("BadBones69")){
					player.sendMessage(Api.getPrefix()+Api.color("&7This server is running your Crazy Crates Plugin. "
						+ "&7It is running version &av"+Bukkit.getServer().getPluginManager().getPlugin("CrazyCrates").getDescription().getVersion()+"&7."));
				}
				if(player.isOp()){
					Api.hasUpdate(player);
				}
			}
		}, 40);
	}
}