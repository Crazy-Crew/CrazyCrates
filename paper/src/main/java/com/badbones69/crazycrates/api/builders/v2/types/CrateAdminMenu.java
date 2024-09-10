package com.badbones69.crazycrates.api.builders.v2.types;

import com.badbones69.crazycrates.api.builders.ItemBuilder;
import com.badbones69.crazycrates.api.builders.v2.DynamicInventoryBuilder;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.enums.Permissions;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.GuiItem;
import com.ryderbelserion.vital.paper.api.builders.gui.types.PaginatedGui;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCloseEvent;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CrateAdminMenu extends DynamicInventoryBuilder {

    public CrateAdminMenu(final Player player) {
        super(player, "<bold><red>Admin Keys</bold>", 6);
    }

    private final Player player = getPlayer();
    private final PaginatedGui gui = getGui();

    @Override
    public void open() {
        this.gui.setItem(6, 5, new GuiItem(new ItemBuilder(Material.CHEST)
                .setDisplayName("<red>What is this menu?")
                .addDisplayLore("")
                .addDisplayLore("<light_purple>A cheat cheat menu of all your available keys.")
                .addDisplayLore(" <gold>⤷ Right click to get virtual keys.")
                .addDisplayLore(" <gold>⤷ Shift right click to get 8 virtual keys.")
                .addDisplayLore(" <gold>⤷ Left click to get physical keys.")
                .addDisplayLore(" <gold>⤷ Shift left click to get 8 physical keys.")
                .getStack(), action -> {
            if (!Permissions.CRAZYCRATES_ACCESS.hasPermission(this.player)) {
                Messages.no_permission.sendMessage(this.player);

                this.gui.close(this.player, InventoryCloseEvent.Reason.CANT_USE, false);
            }
        }));

        this.crateManager.getUsableCrates().forEach(crate -> this.gui.addPageItem(new GuiItem(crate.getKey(1), action -> {
            this.plugin.getLogger().warning("Material: " + action.getCurrentItem().getType().getKey().getKey());

            if (!Permissions.CRAZYCRATES_ACCESS.hasPermission(this.player)) {
                Messages.no_permission.sendMessage(this.player);

                this.gui.close(this.player, InventoryCloseEvent.Reason.CANT_USE, false);
            }

            final String fileName = crate.getFileName();
            final UUID uuid = this.player.getUniqueId();

            final ClickType click = action.getClick();

            final Map<String, String> placeholders = new HashMap<>();

            placeholders.put("{amount}", "1");
            placeholders.put("{key}", crate.getKeyName());

            switch (click) {
                case LEFT -> {
                    MiscUtils.addItem(this.player, crate.getKey(this.player));

                    this.player.playSound(this.player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1f, 1f);

                    placeholders.put("{keytype}", KeyType.physical_key.getFriendlyName());

                    Messages.obtaining_keys.sendMessage(this.player, placeholders);
                }

                case SHIFT_LEFT -> {
                    MiscUtils.addItem(this.player, crate.getKey(8, this.player));

                    this.player.playSound(this.player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1f, 1f);

                    placeholders.put("{keytype}", KeyType.physical_key.getFriendlyName());
                    placeholders.put("{amount}", "8");

                    Messages.obtaining_keys.sendMessage(this.player, placeholders);
                }

                case RIGHT -> {
                    this.userManager.addKeys(uuid, fileName, KeyType.virtual_key, 1);

                    this.player.playSound(this.player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1f, 1f);

                    placeholders.put("{keytype}", KeyType.physical_key.getFriendlyName());

                    Messages.obtaining_keys.sendMessage(this.player, placeholders);
                }

                case SHIFT_RIGHT -> {
                    this.userManager.addKeys(uuid, fileName, KeyType.virtual_key, 8);

                    this.player.playSound(this.player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1f, 1f);

                    placeholders.put("{keytype}", KeyType.physical_key.getFriendlyName());
                    placeholders.put("{amount}", "8");

                    Messages.obtaining_keys.sendMessage(this.player, placeholders);
                }
            }
        })));

        if (this.gui.getNextPageNumber() > 1) {
            setNextButton(6, 6, action -> {
                this.player.playSound(this.player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);

                this.gui.next();
            });

            setBackButton(6, 4, action -> {
                this.player.playSound(this.player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);

                this.gui.previous();
            });
        }

        this.gui.open(this.player, 1);
    }
}