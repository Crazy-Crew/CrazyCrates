package com.badbones69.crazycrates.commands.crates.types.admin;

import com.badbones69.crazycrates.api.builders.types.CrateAdminMenu;
import com.badbones69.crazycrates.api.builders.types.core.CratesMenu;
import com.badbones69.crazycrates.commands.crates.types.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class CommandAdmin extends BaseCommand {

    public enum MenuType {
        ADMIN_KEYS_MENU("Admin_Keys_Menu"),
        CRATES_MENU("Crates_Menu");

        private final String type;

        MenuType(String type) {
            this.type = type;
        }

        public final String getType() {
            return this.type;
        }
    }

    @Command("admin")
    @Permission(value = "crazycrates.admin", def = PermissionDefault.OP)
    public void admin(Player player, MenuType type) {
        switch (type) {
            case ADMIN_KEYS_MENU -> player.openInventory(new CrateAdminMenu(player, "<bold><red>Admin Keys</bold>", 54).build().getInventory());

            case CRATES_MENU -> player.openInventory(new CratesMenu(player).build().getInventory());
        }
    }
}