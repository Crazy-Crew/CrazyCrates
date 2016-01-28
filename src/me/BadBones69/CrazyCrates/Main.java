package me.BadBones69.CrazyCrates;

import java.io.IOException;

import me.BadBones69.CrazyCrates.CrateTypes.CSGO;
import me.BadBones69.CrazyCrates.CrateTypes.QCC;

import org.bukkit.Bukkit;
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
					sender.sendMessage(Api.color("&6/CC Give <Crate> <Amount> <Player> &7- Give a player keys for a Chest."));
					sender.sendMessage(Api.color("&6/CC GiveAll <Crate> <Amount> &7- Gives all online players keys for a Chest."));
					sender.sendMessage(Api.color("&6/CC Set <Crate> <Amount> <Player> &7- Set a players key amount for a Chest."));
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
					return true;
				}
			}
			if(args.length==3){
				if(args[0].equalsIgnoreCase("GiveAll")){ // /Crate GiveAll <Crate> <Amount>
					if(sender instanceof Player)if(!Api.permCheck((Player)sender, "Admin"))return true;
					if(!Api.isInt(args[2])){
						sender.sendMessage(Api.color(Api.getPrefix()+"&c"+args[2]+" is is not a Number."));
						return true;
					}
					int amount = Integer.parseInt(args[2]);
					for(String crate : Api.getCrates()){
						if(crate.equalsIgnoreCase(args[1])){
							sender.sendMessage(Api.color(Api.getPrefix()+"&7You have given everyone &6"+amount+" &7Keys."));
							for(Player p : Bukkit.getServer().getOnlinePlayers()){
								Api.addKeys(amount, p, crate);
							}
							return true;
						}
					}
					sender.sendMessage(Api.color(Api.getPrefix()+"&c"+args[1]+" is is not a Crate."));
					return true;
				}
			}
			if(args.length==4){
				if(args[0].equalsIgnoreCase("Give")){ // /Crate Give <Crate> <Amount> <Player>
					if(sender instanceof Player)if(!Api.permCheck((Player)sender, "Admin"))return true;
					if(!Api.isOnline(args[3], sender))return true;
					if(!Api.isInt(args[2])){
						sender.sendMessage(Api.color(Api.getPrefix()+"&c"+args[2]+" is is not a Number."));
						return true;
					}
					int amount = Integer.parseInt(args[2]);
					Player target = Api.getPlayer(args[3]);
					for(String crate : Api.getCrates()){
						if(crate.equalsIgnoreCase(args[1])){
							sender.sendMessage(Api.color(Api.getPrefix()+"&7You have given &6"+target.getName()+" "+amount+" &7Keys."));
							Api.addKeys(amount, target, crate);
							return true;
						}
					}
					sender.sendMessage(Api.color(Api.getPrefix()+"&c"+args[1]+" is is not a Crate."));
					return true;
				}
				if(args[0].equalsIgnoreCase("Set")){ // /Crate Set <Crate> <Amount> <Player>
					if(sender instanceof Player)if(!Api.permCheck((Player)sender, "Admin"))return true;
					if(!Api.isOnline(args[3], sender))return true;
					if(!Api.isInt(args[2])){
						sender.sendMessage(Api.color(Api.getPrefix()+"&c"+args[2]+" is is not a Number."));
						return true;
					}
					int amount = Integer.parseInt(args[2]);
					Player target = Api.getPlayer(args[3]);
					for(String crate : Api.getCrates()){
						if(crate.equalsIgnoreCase(args[1])){
							sender.sendMessage(Api.color(Api.getPrefix()+"&7You have set &6"+target.getName()+"&7's &6"+crate+" &7key to &6"+amount+"&7."));
							Api.setKeys(amount, target, crate);
							return true;
						}
					}
					sender.sendMessage(Api.color(Api.getPrefix()+"&c"+args[1]+" is is not a Crate."));
					return true;
				}
			}
			sender.sendMessage(Api.color(Api.getPrefix()+"&cPlease do /CC Help for more info."));
		}
		return false;
	}
}