package com.badbones69.crazycrates.commands;

import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.CrazyCrates;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KeyCommand implements CommandExecutor {
    
    private CrazyCrates cc = CrazyCrates.getInstance();
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLable, String[] args) {
        // /key [player]
        // /key redeem <crate> [amount] << Will be added later.
        if (args.length == 0) {
            if (sender instanceof Player) {
                if (!Methods.permCheck(sender, "access")) {
                    return true;
                }
            } else {
                sender.sendMessage(Messages.MUST_BE_A_PLAYER.getMessage());
                return true;
            }
            Player player = (Player) sender;
            List<String> message = new ArrayList<>();
            message.add(Messages.PERSONAL_HEADER.getMessageNoPrefix());
            HashMap<Crate, Integer> keys = cc.getVirtualKeys(player);
            boolean hasKeys = false;
            for (Crate crate : keys.keySet()) {
                int amount = keys.get(crate);
                if (amount > 0) {
                    hasKeys = true;
                    HashMap<String, String> placeholders = new HashMap<>();
                    placeholders.put("%Crate%", crate.getFile().getString("Crate.Name", ""));
                    placeholders.put("%Keys%", amount + "");
                    message.add(Messages.PER_CRATE.getMessageNoPrefix(placeholders));
                }
            }
            if (hasKeys) {
                player.sendMessage(Messages.convertList(message));
            } else {
                player.sendMessage(Messages.PERSONAL_NO_VIRTUAL_KEYS.getMessage());
            }
            return true;
        } else {
            if (sender instanceof Player) {
                if (!Methods.permCheck(sender, "admin")) {
                    return true;
                }
            }
            String player = args[0];
            List<String> message = new ArrayList<>();
            message.add(Messages.OTHER_PLAYER_HEADER.getMessageNoPrefix("%Player%", player));
            HashMap<Crate, Integer> keys = cc.getVirtualKeys(player);
            boolean hasKeys = false;
            for (Crate crate : keys.keySet()) {
                int amount = keys.get(crate);
                if (amount > 0) {
                    hasKeys = true;
                    HashMap<String, String> placeholders = new HashMap<>();
                    placeholders.put("%Crate%", crate.getFile().getString("Crate.Name"));
                    placeholders.put("%Keys%", amount + "");
                    message.add(Messages.PER_CRATE.getMessageNoPrefix(placeholders));
                }
            }
            if (hasKeys) {
                sender.sendMessage(Messages.convertList(message));
            } else {
                sender.sendMessage(Messages.OTHER_PLAYER_NO_VIRTUAL_KEYS.getMessage("%Player%", player));
            }
        }
        return true;
    }
    
}
