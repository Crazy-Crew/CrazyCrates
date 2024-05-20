package com.badbones69.crazycrates.api.builders.types;

import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.enums.Permissions;
import com.badbones69.crazycrates.api.objects.Crate;
import com.ryderbelserion.vital.paper.builders.items.ItemBuilder;
import com.ryderbelserion.vital.paper.util.MiscUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.api.builders.InventoryBuilder;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CrateAdminMenu extends InventoryBuilder {

    public CrateAdminMenu(@NotNull final Player player, @NotNull final String title, final int size) {
        super(player, title, size);
    }

    public CrateAdminMenu() {}

    @Override
    public InventoryBuilder build() {
        final Inventory inventory = getInventory();

        inventory.setItem(49, new ItemBuilder(Material.CHEST)
                .setDisplayName("<red>What is this menu?")
                .addDisplayLore("")
                .addDisplayLore("<light_purple>A cheat cheat menu of all your available keys.")
                .addDisplayLore("<bold><gray>Right click to get virtual keys.")
                .addDisplayLore("<bold><gray>Shift right click to get 8 virtual keys.")
                .addDisplayLore("<bold><gray>Left click to get physical keys.")
                .addDisplayLore("<bold><gray>Shift left click to get 8 physical keys.")
                .getStack());

        for (final Crate crate : this.crateManager.getUsableCrates()) {
            if (inventory.firstEmpty() >= 0) inventory.setItem(inventory.firstEmpty(), crate.getKey(1, getPlayer()));
        }

        return this;
    }

    @Override
    public void run(InventoryClickEvent event) {
        final Inventory inventory = event.getInventory();

        if (!(inventory.getHolder(false) instanceof CrateAdminMenu holder)) return;

        event.setCancelled(true);

        final Player player = holder.getPlayer();

        final InventoryView view = holder.getView();

        if (event.getClickedInventory() != view.getTopInventory()) return;

        if (!Permissions.CRAZYCRATES_ACCESS.hasPermission(player)) {
            player.closeInventory(InventoryCloseEvent.Reason.CANT_USE);
            player.sendRichMessage(Messages.no_permission.getMessage(player));

            return;
        }

        final ItemStack item = event.getCurrentItem();

        if (item == null || item.getType() == Material.AIR) return;

        if (!this.crateManager.isKey(item)) return;

        final Crate crate = this.crateManager.getCrateFromKey(item);

        if (crate == null) return;

        final String crateName = crate.getName();
        final UUID uuid = player.getUniqueId();

        final ClickType clickType = event.getClick();

        final Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{amount}", String.valueOf(1));
        placeholders.put("{key}", crate.getKeyName());

        switch (clickType) {
            case LEFT -> {
                ItemStack key = crate.getKey(player);

                player.getInventory().addItem(key);

                //todo() make this configurable?
                player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1f, 1f);

                placeholders.put("{keytype}", KeyType.physical_key.getFriendlyName());

                player.sendActionBar(MiscUtil.parse(Messages.obtaining_keys.getMessage(player, placeholders)));
            }

            case SHIFT_LEFT -> {
                ItemStack key = crate.getKey(8, player);

                player.getInventory().addItem(key);

                //todo() make this configurable?
                player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1f, 1f);

                placeholders.put("{keytype}", KeyType.physical_key.getFriendlyName());
                placeholders.put("{amount}", "8");

                player.sendActionBar(MiscUtil.parse(Messages.obtaining_keys.getMessage(player, placeholders)));
            }

            case RIGHT -> {
                this.userManager.addKeys(uuid, crateName, KeyType.virtual_key, 1);

                //todo() make this configurable?
                player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1f, 1f);

                placeholders.put("{keytype}", KeyType.physical_key.getFriendlyName());

                player.sendActionBar(MiscUtil.parse(Messages.obtaining_keys.getMessage(player, placeholders)));
            }

            case SHIFT_RIGHT -> {
                this.userManager.addKeys(uuid, crateName, KeyType.virtual_key, 8);

                //todo() make this configurable?
                player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1f, 1f);

                placeholders.put("{keytype}", KeyType.physical_key.getFriendlyName());
                placeholders.put("{amount}", "8");

                player.sendActionBar(MiscUtil.parse(Messages.obtaining_keys.getMessage(player, placeholders)));
            }
        }
    }
}