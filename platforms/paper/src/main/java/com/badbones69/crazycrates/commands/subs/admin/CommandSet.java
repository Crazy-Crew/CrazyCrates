package com.badbones69.crazycrates.commands.subs.admin;

import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.enums.settings.Messages;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.commands.subs.CommandManager;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import java.util.HashMap;

public class CommandSet extends CommandManager {

    @SubCommand("set")
    @Permission(value = "crazycrates.command.admin.set", def = PermissionDefault.OP)
    public void onAdminCrateSet(Player player, @Suggestion("crates") String crateName) {
        for (Crate crate : crazyManager.getCrates()) {
            if (crate.getName().equalsIgnoreCase(crateName)) {
                Block block = player.getTargetBlock(null, 5);

                if (block.isEmpty()) {
                    player.sendMessage(Messages.MUST_BE_LOOKING_AT_A_BLOCK.getMessage());
                    return;
                }

                crazyManager.addCrateLocation(block.getLocation(), crate);

                HashMap<String, String> placeholders = new HashMap<>();

                placeholders.put("%Crate%", crate.getName());
                placeholders.put("%Prefix%", Methods.getPrefix());

                player.sendMessage(Messages.CREATED_PHYSICAL_CRATE.getMessage(placeholders));

                return;
            }
        }

        player.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", crateName));
    }
}