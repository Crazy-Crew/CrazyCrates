package me.BadBones69.CrazyCrates;

import java.io.IOException;
import java.util.HashSet;

import me.BadBones69.CrazyCrates.CrateTypes.CSGO;
import me.BadBones69.CrazyCrates.CrateTypes.QCC;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	public static SettingsManager settings = SettingsManager.getInstance();
	@Override
	public void onDisable(){
		if(!QCC.crates.isEmpty()){
			for(Player player : QCC.crates.keySet()){
				QCC.undoBuild(player);
			}
		}
	}
	@Override
	public void onEnable(){
		saveDefaultConfig();
		settings.setup(this);
		Bukkit.getServer().getPluginManager().registerEvents(new CC(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new GUI(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new QCC(this), this);
		Bukkit.getServer().getPluginManager().registerEvents(new CSGO(this), this);
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
		} catch (IOException e) { // Failed to submit the stats :-(
			System.out.println("Error Submitting stats!");
		}
	}
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String commandLable, String[] args){
		if(commandLable.equalsIgnoreCase("CrazyCrates")||commandLable.equalsIgnoreCase("CC")){
			if(args.length == 0){
				GUI.openGUI((Player)sender);
				return true;
			}
			if(args.length == 1){
				if(args[0].equalsIgnoreCase("Help")){
					sender.sendMessage(Api.color("&3&lCrazy Crates Help Menu"));
					sender.sendMessage(Api.color("&6/CC &7- Opens the GUI."));
					sender.sendMessage(Api.color("&6/CC List &7- Lists all the Crates."));
					sender.sendMessage(Api.color("&6/CC Tp <Location> &7- Teleport to a Crate."));
					sender.sendMessage(Api.color("&6/CC Give <Physical/Virtual> <Crate> <Amount> <Player> &7- Give a player keys for a Chest."));
					sender.sendMessage(Api.color("&6/CC GiveAll <Physical/Virtual> <Crate> <Amount> &7- Gives all online players keys for a Chest."));
					sender.sendMessage(Api.color("&6/CC Create <Location Name> <Crate> &7- Set the block you are looking at as a crate."));
					sender.sendMessage(Api.color("&6/CC Set <Location Name> <Crate> &7- Change a Locations Crate Type."));
					sender.sendMessage(Api.color("&6/CC Remove <Location Name> &7- Delete a Crate Location."));
					sender.sendMessage(Api.color("&6/CC Reload &7- Reloads the Config and Data Files."));
					sender.sendMessage(Api.color("&8&lCreated by: &4&lBadBones69"));
					return true;
				}
				if(args[0].equalsIgnoreCase("Reload")){
					if(sender instanceof Player)if(!Api.permCheck((Player)sender, "Admin"))return true;
					settings.reloadConfig();
					settings.reloadData();
					settings.reloadAll();
					sender.sendMessage(Api.color(Api.getPrefix()+"&3You have just reloaded the Config and Data Files."));
					return true;
				}
				if(args[0].equalsIgnoreCase("List")){
					if(sender instanceof Player)if(!Api.permCheck((Player) sender, "Admin"))return true;
					String crates = "";
					for(String vo : settings.getAllCratesNames()){
						crates += Api.color("&a"+vo+"&8, ");
					}
					crates = crates.substring(0, crates.length()-2);
					sender.sendMessage(Api.color("&e&lCrates:&f "+crates));
					sender.sendMessage(Api.color("&e&lAll Crate Locations:"));
					sender.sendMessage(Api.color("&c[Locations Name]&8, &c[Crate]&8, &c[World]&8, &c[X]&8, &c[Y]&8, &c[Z]"));
					int line = 1;
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
			if(args.length==2){
				if(args[0].equalsIgnoreCase("TP")){// /CC TP <Location>
					if(sender instanceof Player)if(!Api.permCheck((Player)sender, "Admin"))return true;
					String Loc = args[1];
					for(String name : settings.getLocations().getConfigurationSection("Locations").getKeys(false)){
						if(name.equalsIgnoreCase(Loc)){
							World W = Bukkit.getServer().getWorld(settings.getLocations().getString("Locations." + name + ".World"));
							int X = settings.getLocations().getInt("Locations." + name + ".X");
							int Y = settings.getLocations().getInt("Locations." + name + ".Y");
							int Z = settings.getLocations().getInt("Locations." + name + ".Z");
							Location loc = new Location(W,X,Y,Z);
							((Player)sender).teleport(loc);
							sender.sendMessage(Api.color(Api.getPrefix()+"&7You have been teleported to &6"+name+"76."));
							return true;
						}
					}
					sender.sendMessage(Api.color(Api.getPrefix()+"&cThere is no location called &6"+Loc+"76."));
					return true;
				}
				if(args[0].equalsIgnoreCase("Delete")||args[0].equalsIgnoreCase("Remove")||args[0].equalsIgnoreCase("R")){ // /Crate Delete <Location Name>
					if(sender instanceof Player)if(!Api.permCheck((Player)sender, "Admin"))return true;
					String LN = args[1]; //Location Name
					if(settings.getLocations().getConfigurationSection("Locations") == null){
						settings.getLocations().set("Locations.clear", null);
						settings.saveLocations();
					}
					for(String name : settings.getLocations().getConfigurationSection("Locations").getKeys(false)){
						if(name.equalsIgnoreCase(LN)){
							settings.getLocations().set("Locations."+name, null);
							settings.saveLocations();
							sender.sendMessage(Api.color(Api.getPrefix()+"&7You have just removed &6"+name+"&7."));
							return true;
						}
					}
					sender.sendMessage(Api.color(Api.getPrefix()+"&cThere is no Location called &6"+LN+"&c."));
					return true;
				}
			}
			if(args.length==3){
				if(args[0].equalsIgnoreCase("Set")||args[0].equalsIgnoreCase("S")){ // /Crate Set <Location Name> <Crate>
					if(sender instanceof Player)if(!Api.permCheck((Player)sender, "Admin"))return true;
					String LN = args[1]; //Location Name
					String C = args[2]; //Crate
					for(String crate : Api.getCrates()){
						if(crate.equalsIgnoreCase(C)){
							if(settings.getLocations().getConfigurationSection("Locations") == null){
								settings.getLocations().set("Locations.clear", null);
								settings.saveLocations();
								sender.sendMessage(Api.color(Api.getPrefix()+"&cThere is no Location called &6"+LN+"&c."));
								return true;
							}
							for(String name : settings.getLocations().getConfigurationSection("Locations").getKeys(false)){
								if(name.equalsIgnoreCase(LN)){
									settings.getLocations().set("Locations."+name+".Crate", crate);
									settings.saveLocations();
									sender.sendMessage(Api.color(Api.getPrefix()+"&7You have just set &6"+name+" &7as a &6"+crate+" &7Crate."));
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
				if(args[0].equalsIgnoreCase("Create")){ // /Crate Create <Location Name> <Crate>
					if(sender instanceof Player)if(!Api.permCheck((Player)sender, "Admin"))return true;
					Player player = (Player) sender;
					String LN = args[1]; //Location Name
					String C = args[2]; //Crate
					for(String crate : Api.getCrates()){
						if(crate.equalsIgnoreCase(C)){
							if(settings.getLocations().getConfigurationSection("Locations") == null){
								settings.getLocations().set("Locations.clear", null);
								settings.saveLocations();
							}
							for(String name : settings.getLocations().getConfigurationSection("Locations").getKeys(false)){
								if(name.equalsIgnoreCase(LN)){
									player.sendMessage(Api.color(Api.getPrefix()+"&6"+name+" &calready exists."));
									return true;
								}
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
			}
			if(args.length==4){
				if(args[0].equalsIgnoreCase("GiveAll")){ // /Crate GiveAll <Physical/Virtual> <Crate> <Amount>
					if(sender instanceof Player)if(!Api.permCheck((Player)sender, "Admin"))return true;
					if(!Api.isInt(args[3])){
						sender.sendMessage(Api.color(Api.getPrefix()+"&c"+args[3]+" is is not a Number."));
						return true;
					}
					String type = args[1];
					if(!(type.equalsIgnoreCase("Virtual")||type.equalsIgnoreCase("V")||type.equalsIgnoreCase("Physical")||type.equalsIgnoreCase("P"))){
						sender.sendMessage(Api.color(Api.getPrefix()+"&cPlease use Virtual/V or Physical/P for a Key type."));
						return true;
					}
					int amount = Integer.parseInt(args[3]);
					for(String crate : Api.getCrates()){
						if(crate.equalsIgnoreCase(args[2])){
							sender.sendMessage(Api.color(Api.getPrefix()+"&7You have given everyone &6"+amount+" &7Keys."));
							for(Player p : Bukkit.getServer().getOnlinePlayers()){
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
			}
			if(args.length==5){
				if(args[0].equalsIgnoreCase("Give")){ // /Crate Give <Physical/Virtual> <Crate> <Amount> <Player>
					if(sender instanceof Player)if(!Api.permCheck((Player)sender, "Admin"))return true;
					if(!Api.isOnline(args[4], sender))return true;
					if(!Api.isInt(args[3])){
						sender.sendMessage(Api.color(Api.getPrefix()+"&c"+args[3]+" is is not a Number."));
						return true;
					}
					String type = args[1];
					if(!(type.equalsIgnoreCase("Virtual")||type.equalsIgnoreCase("V")||type.equalsIgnoreCase("Physical")||type.equalsIgnoreCase("P"))){
						sender.sendMessage(Api.color(Api.getPrefix()+"&cPlease use Virtual/V or Physical/P for a Key type."));
						return true;
					}
					int amount = Integer.parseInt(args[3]);
					Player target = Api.getPlayer(args[4]);
					for(String crate : Api.getCrates()){
						if(crate.equalsIgnoreCase(args[2])){
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
			}
			sender.sendMessage(Api.color(Api.getPrefix()+"&cPlease do /CC Help for more info."));
		}
		return false;
	}
}