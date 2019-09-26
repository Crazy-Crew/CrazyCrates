package me.badbones69.crazycrates.commands;

import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.enums.CrateType;
import me.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class CCTab implements TabCompleter {
	
	private CrazyCrates cc = CrazyCrates.getInstance();
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String commandLable, String[] args) {
		List<String> completions = new ArrayList<>();
		if(args.length == 1) {// /cc
			if(hasPermission(sender, "access")) completions.add("help");
			if(hasPermission(sender, "additem")) completions.add("additem");
			if(hasPermission(sender, "admin")) completions.add("admin");
			if(hasPermission(sender, "convert")) completions.add("convert");
			if(hasPermission(sender, "list")) completions.add("list");
			if(hasPermission(sender, "open")) completions.add("open");
			if(hasPermission(sender, "tp")) completions.add("tp");
			if(hasPermission(sender, "give")) completions.add("give");
			if(hasPermission(sender, "giveall")) completions.add("giveall");
			if(hasPermission(sender, "take")) completions.add("take");
			if(hasPermission(sender, "set")) completions.add("set");
			if(hasPermission(sender, "reload")) completions.add("reload");
			if(hasPermission(sender, "schematic")) completions.add("set1");
			if(hasPermission(sender, "schematic")) completions.add("set2");
			if(hasPermission(sender, "schematic")) completions.add("save");
			return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
		}else if(args.length == 2) {// /cc arg0
			switch(args[0].toLowerCase()) {
				case "additem":
				case "open":
					cc.getCrates().forEach(crate -> completions.add(crate.getName()));
					completions.remove("Menu");//Takes out a crate that doesn't exist as a file.
					break;
				case "set":
					cc.getCrates().forEach(crate -> completions.add(crate.getName()));
					break;
				case "tp":
					cc.getCrateLocations().forEach(location -> completions.add(location.getID()));
					break;
				case "give":
				case "giveall":
				case "take":
					completions.add("physical");
					completions.add("p");
					completions.add("virtual");
					completions.add("v");
					break;
				case "save":
					completions.add("<Schematic Name>");
					break;
				
			}
			return StringUtil.copyPartialMatches(args[1], completions, new ArrayList<>());
		}else if(args.length == 3) {// /cc arg0 arg1
			switch(args[0].toLowerCase()) {
				case "additem":
					Crate crateFromName = cc.getCrateFromName(args[1]);
					if(crateFromName != null && crateFromName.getCrateType() != CrateType.MENU) {
						cc.getCrateFromName(args[1]).getPrizes().forEach(prize -> completions.add(prize.getName()));
					}
					break;
				case "open":
					Bukkit.getOnlinePlayers().forEach(player -> completions.add(player.getName()));
					break;
				case "give":
				case "giveall":
				case "take":
					cc.getCrates().forEach(crate -> completions.add(crate.getName()));
					completions.remove("Menu");//Takes out a crate that doesn't exist as a file.
					break;
			}
			return StringUtil.copyPartialMatches(args[2], completions, new ArrayList<>());
		}else if(args.length == 4) {// /cc arg0 arg1 arg2
			switch(args[0].toLowerCase()) {
				case "give":
				case "giveall":
				case "take":
					for(int i = 1; i <= 100; i++) completions.add(i + "");
					break;
			}
			return StringUtil.copyPartialMatches(args[3], completions, new ArrayList<>());
		}else if(args.length == 5) {// /cc arg0 arg1 arg2 arg3
			switch(args[0].toLowerCase()) {
				case "give":
				case "giveall":
				case "take":
					Bukkit.getOnlinePlayers().forEach(player -> completions.add(player.getName()));
					break;
			}
			return StringUtil.copyPartialMatches(args[4], completions, new ArrayList<>());
		}
		return new ArrayList<>();
	}
	
	private boolean hasPermission(CommandSender sender, String node) {
		return sender.hasPermission("crazycrates." + node) || sender.hasPermission("crazycrates.admin");
	}
	
}