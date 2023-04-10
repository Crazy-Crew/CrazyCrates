package com.badbones69.crazycrates.commands.subs.admin;

import com.badbones69.crazycrates.api.enums.settings.Messages;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.commands.subs.CommandManager;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionDefault;
import java.util.HashMap;

public class CommandAddItem extends CommandManager {

    @SubCommand("additem")
    @Permission(value = "crazycrates.command.admin.additem", def = PermissionDefault.OP)
    public void onAdminCrateAddItem(Player player, @Suggestion("crates") String crateName, @Suggestion("prizes") String prize) {

        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() != Material.AIR) {
            Crate crate = crazyManager.getCrateFromName(crateName);

            if (crate != null) {
                try {
                    crate.addEditorItem(prize, item);
                } catch (Exception e) {
                    plugin.getServer().getLogger().warning("Failed to add a new prize to the " + crate.getName() + " crate.");

                    e.printStackTrace();
                }

                crazyManager.loadCrates();

                HashMap<String, String> placeholders = new HashMap<>();

                placeholders.put("%Crate%", crate.getName());
                placeholders.put("%Prize%", prize);

                player.sendMessage(Messages.ADDED_ITEM_WITH_EDITOR.getMessage(placeholders));
            } else {
                player.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", crateName));
            }
        } else {
            player.sendMessage(Messages.NO_ITEM_IN_HAND.getMessage());
        }
    }
}