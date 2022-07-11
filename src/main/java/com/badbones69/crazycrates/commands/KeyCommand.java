package com.badbones69.crazycrates.commands;

import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.enums.settings.Messages;
import com.badbones69.crazycrates.api.enums.Permissions;
import com.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KeyCommand implements CommandExecutor {
    
    private final CrazyManager crazyManager = CrazyManager.getInstance();
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        // /key [player]
        // /key redeem <crate> [amount] << Will be added later.
        if (args.length == 0) {
            if (sender instanceof Player player) {
                if (!Methods.permCheck(sender, Permissions.CRAZY_CRATES_PLAYER_KEY, false)) {
                    return true;
                }
            } else {
                sender.sendMessage(Messages.MUST_BE_A_PLAYER.getMessage());
                return true;
            }

            List<String> message = new ArrayList<>();
            message.add(Messages.PERSONAL_HEADER.getMessageNoPrefix());
            HashMap<Crate, Integer> keys = crazyManager.getVirtualKeys(player);
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
                if (!Methods.permCheck(sender, Permissions.CRAZY_CRATES_PLAYER_KEY_OTHERS, false)) {
                    return true;
                }
            }

            String player = args[0];
            List<String> message = new ArrayList<>();
            message.add(Messages.OTHER_PLAYER_HEADER.getMessageNoPrefix("%Player%", player));
            HashMap<Crate, Integer> keys = crazyManager.getVirtualKeys(player);
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