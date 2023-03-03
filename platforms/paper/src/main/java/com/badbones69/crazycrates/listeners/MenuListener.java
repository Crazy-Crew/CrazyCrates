package com.badbones69.crazycrates.listeners;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.enums.types.CrateType;
import com.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;

public class MenuListener implements Listener {

    private static final CrazyCrates plugin = CrazyCrates.getPlugin();

    private static final CrazyManager crazyManager = plugin.getStarter().getCrazyManager();
    
    public static void openGUI(Player player) {
        //int size = Config.PREVIEW_MENU_SIZE;
        //Inventory inv = plugin.getServer().createInventory(null, size, Methods.sanitizeColor(Config.PREVIEW_MENU_NAME));

        //if (Config.FILLER_ITEMS_TOGGLE) {
            //ItemStack item = new ItemBuilder().setMaterial(Config.FILLER_ITEMS_ITEM).setName(Config.FILLER_ITEMS_NAME).setLore(Config.FILLER_ITEMS_LORE).build();

            //for (int i = 0; i < size; i++) {
                //inv.setItem(i, item.clone());
            //}
        //}

        /*for (String custom : Config.FILLER_EXTRA_ITEMS) {
            int slot = 0;
            ItemBuilder itemBuilder = new ItemBuilder();
            String[] split = custom.split(", ");

            for (String option : split) {

                if (option.contains("Item:")) itemBuilder.setMaterial(option.replace("Item:", ""));

                if (option.contains("Name:")) {
                    option = option.replace("Name:", "");

                    option = getCrates(player, option);

                    itemBuilder.setName(option.replaceAll("%player%", player.getName()));
                }

                if (option.contains("Lore:")) {
                    option = option.replace("Lore:", "");
                    String[] d = option.split(",");

                    for (String l : d) {
                        option = getCrates(player, option);

                        itemBuilder.addLore(option.replaceAll("%player%", player.getName()));
                    }
                }

                if (option.contains("Glowing:")) itemBuilder.setGlow(Boolean.parseBoolean(option.replace("Glowing:", "")));

                if (option.contains("Player:")) itemBuilder.setPlayerName(option.replaceAll("%player%", player.getName()));

                if (option.contains("Slot:")) slot = Integer.parseInt(option.replace("Slot:", ""));

                if (option.contains("Unbreakable-Item")) itemBuilder.setUnbreakable(Boolean.parseBoolean(option.replace("Unbreakable-Item:", "")));

                if (option.contains("Hide-Item-Flags")) itemBuilder.hideItemFlags(Boolean.parseBoolean(option.replace("Hide-Item-Flags:", "")));
            }

            if (slot > size) continue;

            slot--;
            //inv.setItem(slot, itemBuilder.build());
        }*/

        for (Crate crate : crazyManager.getCrates()) {
            FileConfiguration file = crate.getFile();

            if (file != null) {
                if (file.getBoolean("Crate.InGUI")) {
                    String path = "Crate.";
                    int slot = file.getInt(path + "Slot");

                    //if (slot > size) continue;

                    slot--;
                    //inv.setItem(slot, new ItemBuilder()
                    //        .setMaterial(file.getString(path + "Item"))
                    //        .setName(file.getString(path + "Name"))
                    //        .setLore(file.getStringList(path + "Lore"))
                    //        .setCrateName(crate.getName())
                    //        .setPlayerName(file.getString(path + "Player"))
                    //        .setGlow(file.getBoolean(path + "Glowing"))
                    //        .addLorePlaceholder("%Keys%", NumberFormat.getNumberInstance().format(crazyManager.getVirtualKeys(player, crate)))
                    //        .addLorePlaceholder("%Keys_Physical%", NumberFormat.getNumberInstance().format(crazyManager.getPhysicalKeys(player, crate)))
                     //       .addLorePlaceholder("%Keys_Total%", NumberFormat.getNumberInstance().format(crazyManager.getTotalKeys(player, crate)))
                    //        .addLorePlaceholder("%Player%", player.getName())
                    //        .build());
                }
            }
        }

        //player.openInventory(inv);
    }

    private static String getCrates(Player player, String option) {
        for (Crate crate : crazyManager.getCrates()) {
            if (crate.getCrateType() != CrateType.MENU) {
                option = option.replaceAll("%" + crate.getName().toLowerCase() + "%", crazyManager.getVirtualKeys(player, crate) + "")
                .replaceAll("%" + crate.getName().toLowerCase() + "_physical%", crazyManager.getPhysicalKeys(player, crate) + "")
                .replaceAll("%" + crate.getName().toLowerCase() + "_total%", crazyManager.getTotalKeys(player, crate) + "");
            }
        }

        return option;
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();

        if (inv != null) {

            for (Crate crate : crazyManager.getCrates()) {
                if (crate.getCrateType() != CrateType.MENU && crate.isCrateMenu(e.getView())) return;
            }

            //if (e.getView().getTitle().equals(Methods.sanitizeColor(Config.PREVIEW_MENU_NAME))) {
                e.setCancelled(true);

                if (e.getCurrentItem() != null) {
                    ItemStack item = e.getCurrentItem();

                    if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                        /*NBTItem nbtItem = new NBTItem(item);

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
                                    if (Config.VIRTUAL_ACCEPTS_PHYSICAL_KEYS && crazyManager.hasPhysicalKey(player, crate, false)) {
                                        hasKey = true;
                                        keyType = KeyType.PHYSICAL_KEY;
                                    }
                                }

                                if (!hasKey) {
                                    if (Config.KEY_SOUND_TOGGLE) {
                                        Sound sound = Sound.valueOf(Config.KEY_SOUND_NAME);

                                        player.playSound(player.getLocation(), sound, 1f, 1f);
                                    }

                                    //player.sendMessage(Messages.NO_VIRTUAL_KEY.getMessage());
                                    return;
                                }

                                if (Config.DISABLED_WORLDS_TOGGLE) {
                                    for (String world : Config.DISABLED_WORLDS) {
                                        if (world.equalsIgnoreCase(player.getWorld().getName())) {
                                            //player.sendMessage(Messages.WORLD_DISABLED.getMessage("%World%", player.getWorld().getName()));
                                            return;
                                        }
                                    }
                                }

                                if (Methods.isInventoryFull(player)) {
                                    //player.sendMessage(Messages.INVENTORY_FULL.getMessage());
                                    return;
                                }

                                crazyManager.openCrate(player, crate, keyType, player.getLocation(), true, false);
                            }
                        }*/
                    }
                }
            //}
        }
    }
    
    private List<String> getDisabledWorlds() {
        return Collections.emptyList();
    }
}