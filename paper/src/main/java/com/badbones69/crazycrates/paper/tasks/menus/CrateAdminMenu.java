package com.badbones69.crazycrates.paper.tasks.menus;

import com.badbones69.crazycrates.paper.api.builders.LegacyItemBuilder;
import com.badbones69.crazycrates.paper.api.builders.gui.DynamicInventoryBuilder;
import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.api.enums.Permissions;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import com.badbones69.crazycrates.core.config.impl.ConfigKeys;
import com.ryderbelserion.fusion.paper.api.builder.gui.interfaces.GuiFiller;
import com.ryderbelserion.fusion.paper.api.builder.gui.interfaces.GuiItem;
import com.ryderbelserion.fusion.paper.api.builder.gui.types.PaginatedGui;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemType;
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
        final GuiFiller guiFiller = this.gui.getFiller();

        final GuiItem guiItem = new GuiItem(ItemType.BLACK_STAINED_GLASS_PANE);

        guiFiller.fillTop(guiItem);
        guiFiller.fillBottom(guiItem);

        this.crateManager.getUsableCrates().forEach(crate -> this.gui.addItem(new GuiItem(crate.getKey(1), action -> {
            if (!Permissions.CRAZYCRATES_ACCESS.hasPermission(this.player)) {
                Messages.no_permission.sendMessage(this.player);

                this.gui.close(this.player, InventoryCloseEvent.Reason.CANT_USE, false);

                return;
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

        this.gui.open(this.player, gui -> {
            final int rows = gui.getRows();

            gui.setItem(rows, 5, new LegacyItemBuilder(ItemType.CHEST)
                    .setDisplayName("<red>What is this menu?")
                    .addDisplayLore(" <gold>⤷ Right click to go back to the main menu!")
                    .addDisplayLore("")
                    .addDisplayLore("<light_purple>A cheat cheat menu of all your available keys.")
                    .addDisplayLore(" <gold>⤷ Right click to get virtual keys.")
                    .addDisplayLore(" <gold>⤷ Shift right click to get 8 virtual keys.")
                    .addDisplayLore(" <gold>⤷ Left click to get physical keys.")
                    .addDisplayLore(" <gold>⤷ Shift left click to get 8 physical keys.")
                    .asGuiItem(action -> {
                        if (!Permissions.CRAZYCRATES_ACCESS.hasPermission(this.player)) {
                            Messages.no_permission.sendMessage(this.player);

                            gui.close(this.player, InventoryCloseEvent.Reason.CANT_USE, false);

                            return;
                        }

                        if (this.config.getProperty(ConfigKeys.enable_crate_menu)) {
                            this.player.playSound(this.player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);

                            new CrateMainMenu(this.player, this.config.getProperty(ConfigKeys.inventory_name), this.config.getProperty(ConfigKeys.inventory_rows)).open();
                        }
                    }));

            setBackButton(rows, 4, false);
            setNextButton(rows, 6, false);
        });
    }
}