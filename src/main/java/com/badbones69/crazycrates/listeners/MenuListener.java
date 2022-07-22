package com.badbones69.crazycrates.listeners;

import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.enums.CrateType;
import com.badbones69.crazycrates.api.enums.KeyType;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.ItemBuilder;
import com.badbones69.crazycrates.config.Config;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.text.NumberFormat;

public class MenuListener implements Listener {

    private static final CrazyManager crazyManager = CrazyManager.getInstance();
    
    public static void openGUI(Player player) {
        int size = Config.inventorySize;
        Inventory inv = player.getServer().createInventory(null, size, Methods.sanitizeColor(Config.inventoryName));

        if (Config.fillerToggle) {
            ItemStack item = new ItemBuilder().setMaterial(Config.fillerItem).setName(Config.fillerName).setLore(Config.fillerLore).build();

            for (int i = 0; i < size; i++) {
                inv.setItem(i, item.clone());
            }
        }

        for (String custom : Config.guiCustomizer) {
            int slot = 0;
            ItemBuilder item = new ItemBuilder();
            String[] split = custom.split(", ");

            for (String option : split) {

                if (option.contains("Item:")) {
                    item.setMaterial(option.replace("Item:", ""));
                }

                if (option.contains("Name:")) {
                    option = option.replace("Name:", "");

                    for (Crate crate : crazyManager.getCrates()) {
                        if (crate.getCrateType() != CrateType.MENU) {
                            option = option.replaceAll("%" + crate.getName().toLowerCase() + "%", crazyManager.getVirtualKeys(player, crate) + "")
                                    .replaceAll("%" + crate.getName().toLowerCase() + "_physical%", crazyManager.getPhysicalKeys(player, crate) + "")
                                    .replaceAll("%" + crate.getName().toLowerCase() + "_total%", crazyManager.getTotalKeys(player, crate) + "");
                        }
                    }

                    item.setName(option.replaceAll("%player%", player.getName()));
                }

                if (option.contains("Lore:")) {
                    option = option.replace("Lore:", "");
                    String[] d = option.split(",");

                    for (String l : d) {
                        for (Crate crate : crazyManager.getCrates()) {
                            if (crate.getCrateType() != CrateType.MENU) {
                                option = option.replaceAll("%" + crate.getName().toLowerCase() + "%", crazyManager.getVirtualKeys(player, crate) + "")
                                        .replaceAll("%" + crate.getName().toLowerCase() + "_physical%", crazyManager.getPhysicalKeys(player, crate) + "")
                                        .replaceAll("%" + crate.getName().toLowerCase() + "_total%", crazyManager.getTotalKeys(player, crate) + "");
                            }
                        }

                        item.addLore(option.replaceAll("%player%", player.getName()));
                    }
                }

                if (option.contains("Glowing:")) {
                    item.setGlow(Boolean.parseBoolean(option.replace("Glowing:", "")));
                }

                if (option.contains("Player:")) {
                    item.setPlayerName(option.replaceAll("%player%", player.getName()));
                }

                if (option.contains("Slot:")) {
                    slot = Integer.parseInt(option.replace("Slot:", ""));
                }

                if (option.contains("Unbreakable-Item")) {
                    item.setUnbreakable(Boolean.parseBoolean(option.replace("Unbreakable-Item:", "")));
                }

                if (option.contains("Hide-Item-Flags")) {
                    item.hideItemFlags(Boolean.parseBoolean(option.replace("Hide-Item-Flags:", "")));
                }
            }

            if (slot > size) {
                continue;
            }

            slot--;
            inv.setItem(slot, item.build());
        }

        for (Crate crate : crazyManager.getCrates()) {
            FileConfiguration file = crate.getFile();

            if (file != null) {
                if (file.getBoolean("Crate.InGUI")) {
                    String path = "Crate.";
                    int slot = file.getInt(path + "Slot");

                    if (slot > size) {
                        continue;
                    }

                    slot--;
                    inv.setItem(slot, new ItemBuilder()
                            .setMaterial(file.getString(path + "Item"))
                            .setName(file.getString(path + "Name"))
                            .setLore(file.getStringList(path + "Lore"))
                            .setCrateName(crate.getName())
                            .setPlayerName(file.getString(path + "Player"))
                            .setGlow(file.getBoolean(path + "Glowing"))
                            .addLorePlaceholder("%Keys%", NumberFormat.getNumberInstance().format(crazyManager.getVirtualKeys(player, crate)))
                            .addLorePlaceholder("%Keys_Physical%", NumberFormat.getNumberInstance().format(crazyManager.getPhysicalKeys(player, crate)))
                            .addLorePlaceholder("%Keys_Total%", NumberFormat.getNumberInstance().format(crazyManager.getTotalKeys(player, crate)))
                            .addLorePlaceholder("%Player%", player.getName())
                            .build());
                }
            }
        }

        player.openInventory(inv);
    }
    
    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();

        if (inv != null) {

            for (Crate crate : crazyManager.getCrates()) {
                if (crate.getCrateType() != CrateType.MENU && crate.isCrateMenu(e.getView())) {
                    return;
                }
            }

            if (e.getView().getTitle().equals(Methods.sanitizeColor(Config.inventoryName))) {
                e.setCancelled(true);

                if (e.getCurrentItem() != null) {
                    ItemStack item = e.getCurrentItem();

                    if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                        NBTItem nbtItem = new NBTItem(item);

                        if (nbtItem.hasNBTData() && nbtItem.hasKey("CrazyCrates-Crate")) {
                            Crate crate = crazyManager.getCrateFromName(nbtItem.getString("CrazyCrates-Crate"));

                            if (crate != null) {
                                if (e.getAction() == InventoryAction.PICKUP_HALF) { // Right-clicked the item

                                    if (crate.isPreviewEnabled()) {
                                        player.closeInventory();
                                        PreviewListener.setPlayerInMenu(player, true);
                                        PreviewListener.openNewPreview(player, crate);
                                    } else {
                                        //player.sendMessage(Messages.PREVIEW_DISABLED.getMessage());
                                    }

                                    return;
                                }

                                if (crazyManager.isInOpeningList(player)) {
                                    //player.sendMessage(Messages.CRATE_ALREADY_OPENED.getMessage());
                                    return;
                                }

                                boolean hasKey = false;
                                KeyType keyType = KeyType.VIRTUAL_KEY;

                                if (crazyManager.getVirtualKeys(player, crate) >= 1) {
                                    hasKey = true;
                                } else {
                                    if (Config.virtualAcceptsPhysicalKeys && crazyManager.hasPhysicalKey(player, crate, false)) {
                                        hasKey = true;
                                        keyType = KeyType.PHYSICAL_KEY;
                                    }
                                }

                                if (!hasKey) {
                                    Sound sound = Sound.valueOf(Config.needKeySound);

                                    player.playSound(player.getLocation(), sound, 1f, 1f);

                                    //player.sendMessage(Messages.NO_VIRTUAL_KEY.getMessage());
                                    return;
                                }

                                for (String world : Config.disabledWorlds) {
                                    if (world.equalsIgnoreCase(player.getWorld().getName())) {
                                        //player.sendMessage(Messages.WORLD_DISABLED.getMessage("%World%", player.getWorld().getName()));
                                        return;
                                    }
                                }

                                if (Methods.isInventoryFull(player)) {
                                    //player.sendMessage(Messages.INVENTORY_FULL.getMessage());
                                    return;
                                }

                                crazyManager.openCrate(player, crate, keyType, player.getLocation(), true, false);
                            }
                        }
                    }
                }
            }
        }
    }
}