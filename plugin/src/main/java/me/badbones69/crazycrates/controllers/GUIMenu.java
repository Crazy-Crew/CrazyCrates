package me.badbones69.crazycrates.controllers;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.FileManager.Files;
import me.badbones69.crazycrates.api.enums.CrateType;
import me.badbones69.crazycrates.api.enums.KeyType;
import me.badbones69.crazycrates.api.enums.Messages;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.api.objects.ItemBuilder;
import me.badbones69.crazycrates.multisupport.itemnbtapi.NBTItem;
import org.bukkit.Bukkit;
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
import java.util.ArrayList;
import java.util.List;

public class GUIMenu implements Listener {
    
    private static CrazyCrates cc = CrazyCrates.getInstance();
    
    public static void openGUI(Player player) {
        int size = Files.CONFIG.getFile().getInt("Settings.InventorySize");
        Inventory inv = Bukkit.createInventory(null, size, Methods.sanitizeColor(Files.CONFIG.getFile().getString("Settings.InventoryName")));
        if (Files.CONFIG.getFile().contains("Settings.Filler.Toggle")) {
            if (Files.CONFIG.getFile().getBoolean("Settings.Filler.Toggle")) {
                String id = Files.CONFIG.getFile().getString("Settings.Filler.Item");
                String name = Files.CONFIG.getFile().getString("Settings.Filler.Name");
                List<String> lore = Files.CONFIG.getFile().getStringList("Settings.Filler.Lore");
                ItemStack item = new ItemBuilder().setMaterial(id).setName(name).setLore(lore).build();
                for (int i = 0; i < size; i++) {
                    inv.setItem(i, item.clone());
                }
            }
        }
        if (Files.CONFIG.getFile().contains("Settings.GUI-Customizer")) {
            for (String custom : Files.CONFIG.getFile().getStringList("Settings.GUI-Customizer")) {
                int slot = 0;
                ItemBuilder item = new ItemBuilder();
                String[] b = custom.split(", ");
                for (String i : b) {
                    if (i.contains("Item:")) {
                        item.setMaterial(i.replace("Item:", ""));
                    }
                    if (i.contains("Name:")) {
                        i = i.replace("Name:", "");
                        for (Crate crate : cc.getCrates()) {
                            if (crate.getCrateType() != CrateType.MENU) {
                                i = i.replaceAll("%" + crate.getName().toLowerCase() + "%", cc.getVirtualKeys(player, crate) + "")
                                .replaceAll("%" + crate.getName().toLowerCase() + "_physical%", cc.getPhysicalKeys(player, crate) + "")
                                .replaceAll("%" + crate.getName().toLowerCase() + "_total%", cc.getTotalKeys(player, crate) + "");
                            }
                        }
                        item.setName(i.replaceAll("%player%", player.getName()));
                    }
                    if (i.contains("Lore:")) {
                        i = i.replace("Lore:", "");
                        String[] d = i.split(",");
                        for (String l : d) {
                            for (Crate crate : cc.getCrates()) {
                                if (crate.getCrateType() != CrateType.MENU) {
                                    i = i.replaceAll("%" + crate.getName().toLowerCase() + "%", cc.getVirtualKeys(player, crate) + "")
                                    .replaceAll("%" + crate.getName().toLowerCase() + "_physical%", cc.getPhysicalKeys(player, crate) + "")
                                    .replaceAll("%" + crate.getName().toLowerCase() + "_total%", cc.getTotalKeys(player, crate) + "");
                                }
                            }
                            item.addLore(i.replaceAll("%player%", player.getName()));
                        }
                    }
                    if (i.contains("Glowing:")) {
                        item.setGlowing(Boolean.parseBoolean(i.replace("Glowing:", "")));
                    }
                    if (i.contains("Player:")) {
                        item.setPlayer(i.replaceAll("%player%", player.getName()));
                    }
                    if (i.contains("Slot:")) {
                        slot = Integer.parseInt(i.replace("Slot:", ""));
                    }
                }
                if (slot > size) {
                    continue;
                }
                slot--;
                inv.setItem(slot, item.build());
            }
        }
        for (Crate crate : cc.getCrates()) {
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
                    .setPlayer(file.getString(path + "Player"))
                    .setGlowing(file.getBoolean(path + "Glowing"))
                    .addLorePlaceholder("%Keys%", NumberFormat.getNumberInstance().format(cc.getVirtualKeys(player, crate)))
                    .addLorePlaceholder("%Keys_Physical%", NumberFormat.getNumberInstance().format(cc.getPhysicalKeys(player, crate)))
                    .addLorePlaceholder("%Keys_Total%", NumberFormat.getNumberInstance().format(cc.getTotalKeys(player, crate)))
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
        FileConfiguration config = Files.CONFIG.getFile();
        if (inv != null) {
            for (Crate crate : cc.getCrates()) {
                if (crate.getCrateType() != CrateType.MENU && crate.isCrateMenu(e.getView())) {
                    return;
                }
            }
            if (e.getView().getTitle().equals(Methods.sanitizeColor(config.getString("Settings.InventoryName")))) {
                e.setCancelled(true);
                if (e.getCurrentItem() != null) {
                    ItemStack item = e.getCurrentItem();
                    if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                        NBTItem nbtItem = new NBTItem(item);
                        if (nbtItem.hasNBTData() && nbtItem.hasKey("CrazyCrates-Crate")) {
                            Crate crate = cc.getCrateFromName(nbtItem.getString("CrazyCrates-Crate"));
                            if (crate != null) {
                                if (e.getAction() == InventoryAction.PICKUP_HALF) {//Right clicked the item
                                    if (crate.isPreviewEnabled()) {
                                        player.closeInventory();
                                        Preview.setPlayerInMenu(player, true);
                                        Preview.openNewPreview(player, crate);
                                    } else {
                                        player.sendMessage(Messages.PREVIEW_DISABLED.getMessage());
                                    }
                                    return;
                                }
                                if (cc.isInOpeningList(player)) {
                                    player.sendMessage(Messages.CRATE_ALREADY_OPENED.getMessage());
                                    return;
                                }
                                boolean hasKey = false;
                                KeyType keyType = KeyType.VIRTUAL_KEY;
                                if (cc.getVirtualKeys(player, crate) >= 1) {
                                    hasKey = true;
                                } else {
                                    if (Files.CONFIG.getFile().getBoolean("Settings.Virtual-Accepts-Physical-Keys") && cc.hasPhysicalKey(player, crate, false)) {
                                        hasKey = true;
                                        keyType = KeyType.PHYSICAL_KEY;
                                    }
                                }
                                if (!hasKey) {
                                    if (config.contains("Settings.Need-Key-Sound")) {
                                        Sound sound = Sound.valueOf(config.getString("Settings.Need-Key-Sound"));
                                        if (sound != null) {
                                            player.playSound(player.getLocation(), sound, 1f, 1f);
                                        }
                                    }
                                    player.sendMessage(Messages.NO_VIRTUAL_KEY.getMessage());
                                    return;
                                }
                                for (String world : getDisabledWorlds()) {
                                    if (world.equalsIgnoreCase(player.getWorld().getName())) {
                                        player.sendMessage(Messages.WORLD_DISABLED.getMessage("%World%", player.getWorld().getName()));
                                        return;
                                    }
                                }
                                if (Methods.isInventoryFull(player)) {
                                    player.sendMessage(Messages.INVENTORY_FULL.getMessage());
                                    return;
                                }
                                cc.openCrate(player, crate, keyType, player.getLocation(), true, false);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private ArrayList<String> getDisabledWorlds() {
        return new ArrayList<>(Files.CONFIG.getFile().getStringList("Settings.DisabledWorlds"));
    }
    
}