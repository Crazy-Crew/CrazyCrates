package com.badbones69.crazycrates.api.builders.types;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.enums.Permissions;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.other.ItemBuilder;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.api.builders.InventoryBuilder;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import us.crazycrew.crazycrates.api.users.UserManager;
import java.util.HashMap;

public class CrateAdminMenu extends InventoryBuilder {

    public CrateAdminMenu(Player player, int size, String title) {
        super(player, size, title);
    }

    @Override
    public InventoryBuilder build() {
        Inventory inventory = getInventory();

        inventory.setItem(49, new ItemBuilder().setMaterial(Material.CHEST)
                .setName("&cWhat is this menu?")
                .addLore("")
                .addLore("&dA cheat cheat menu of all your available keys.")
                .addLore("&7&lRight click to get virtual keys.")
                .addLore("&7&lLeft click to get physical keys.").build());

        for (Crate crate : this.plugin.getCrateManager().getUsableCrates()) {
            if (inventory.firstEmpty() >= 0) inventory.setItem(inventory.firstEmpty(), crate.getKey(getPlayer()));
        }

        return this;
    }

    public static class CrateAdminListener implements Listener {

        @NotNull
        private final CrazyCrates plugin = CrazyCrates.get();

        @NotNull
        private final CrateManager crateManager = this.plugin.getCrateManager();

        @NotNull
        private final UserManager userManager = this.plugin.getCrazyHandler().getUserManager();

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            Inventory inventory = event.getInventory();

            if (!(inventory.getHolder(false) instanceof CrateAdminMenu holder)) return;

            event.setCancelled(true);

            Player player = holder.getPlayer();

            InventoryView view = holder.getView();

            if (event.getClickedInventory() != view.getTopInventory()) return;

            if (!Permissions.CRAZYCRATES_ACCESS.hasPermission(player)) {
                player.closeInventory(InventoryCloseEvent.Reason.CANT_USE);
                player.sendMessage(Messages.no_permission.getMessage(player));
                return;
            }

            ItemStack item = event.getCurrentItem();

            if (item == null || item.getType() == Material.AIR) return;

            if (!this.crateManager.isKey(item)) return;

            Crate crate = this.crateManager.getCrateFromKey(item);

            ClickType clickType = event.getClick();

            switch (clickType) {
                case LEFT -> {
                    ItemStack key = crate.getKey(player);

                    player.getInventory().addItem(key);

                    if (key.getItemMeta() != null) {
                        HashMap<String, String> placeholders = new HashMap<>();

                        placeholders.put("%amount%", String.valueOf(1));
                        placeholders.put("%key%", crate.getKeyName());

                        player.sendMessage(Messages.obtaining_keys.getMessage(placeholders, player));
                    }
                }

                case RIGHT -> {
                    this.userManager.addKeys(1, player.getUniqueId(), crate.getName(), KeyType.virtual_key);

                    ItemStack key = crate.getKey(player);

                    if (key.getItemMeta() != null) {
                        HashMap<String, String> placeholders = new HashMap<>();

                        placeholders.put("%amount%", String.valueOf(1));
                        placeholders.put("%key%", crate.getKeyName());

                        player.sendMessage(Messages.obtaining_keys.getMessage(placeholders, player));
                    }
                }
            }
        }
    }
}