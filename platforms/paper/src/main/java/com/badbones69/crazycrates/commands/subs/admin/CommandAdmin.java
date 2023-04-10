package com.badbones69.crazycrates.commands.subs.admin;

import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.commands.subs.CommandManager;
import com.badbones69.crazycrates.enums.types.CrateType;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.permissions.PermissionDefault;

public class CommandAdmin extends CommandManager {

    @SubCommand("admin")
    @Permission(value = "crazycrates.command.admin.access", def = PermissionDefault.OP)
    public void onAdminMenu(Player player) {
        int size = crazyManager.getCrates().size();
        int slots = 9;

        for (; size > 9; size -= 9) slots += 9;

        Inventory inv = plugin.getServer().createInventory(null, slots, Methods.color("&4&lAdmin Keys"));

        for (Crate crate : crazyManager.getCrates()) {
            if (crate.getCrateType() != CrateType.MENU) {
                if (inv.firstEmpty() >= 0) inv.setItem(inv.firstEmpty(), crate.getAdminKey());
            }
        }

        player.openInventory(inv);
    }
}