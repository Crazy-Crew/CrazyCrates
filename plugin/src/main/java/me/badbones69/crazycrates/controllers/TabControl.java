package me.badbones69.crazycrates.controllers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TabControl implements TabCompleter {
	
	private static String[] COMMANDS = {"additem", "admin", "list", "open", "tp", "give", "giveall", "take", "set", "reload"};
	
	@Override public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {
		List<String> completions = new ArrayList<>();
		List<String> commands = new ArrayList<>(Arrays.asList(COMMANDS));
		if(args.length == 1) {
			StringUtil.copyPartialMatches(args[0], commands, completions);
		}
		Collections.sort(completions);
		return completions;
	}
	
}