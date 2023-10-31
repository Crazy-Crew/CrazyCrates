package com.badbones69.crazycrates.paper.listeners;

import ch.jalu.configme.SettingsManager;
import us.crazycrew.crazycrates.common.config.types.Config;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.api.crates.CrateManager;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.ItemBuilder;
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
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import us.crazycrew.crazycrates.paper.CrazyHandler;
import us.crazycrew.crazycrates.paper.api.enums.Translation;
import us.crazycrew.crazycrates.paper.utils.MiscUtils;
import us.crazycrew.crazycrates.paper.utils.MsgUtils;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class MenuListener implements Listener {

    private static final CrazyCrates plugin = CrazyCrates.getPlugin(CrazyCrates.class);

    private static final SettingsManager config = plugin.getConfigManager().getConfig();

    private static final CrazyHandler crazyHandler = plugin.getCrazyHandler();

    private static final CrateManager crateManager = plugin.getCrateManager();
    
    public static void openGUI(Player player) {
        int size = config.getProperty(Config.inventory_size);
        Inventory inv = player.getServer().createInventory(null, size, MsgUtils.sanitizeColor(config.getProperty(Config.inventory_name)));

        if (config.getProperty(Config.filler_toggle)) {
            String id = config.getProperty(Config.filler_item);
            String name = config.getProperty(Config.filler_name);
            List<String> lore = config.getProperty(Config.filler_lore);
            ItemStack item = new ItemBuilder().setMaterial(id).setName(name).setLore(lore).build();

            for (int i = 0; i < size; i++) {
                inv.setItem(i, item.clone());
            }
        }

        if (config.getProperty(Config.gui_customizer_toggle)) {
            List<String> customizer = config.getProperty(Config.gui_customizer);

            if (!customizer.isEmpty()) {
                for (String custom : customizer) {
                    int slot = 0;
                    ItemBuilder item = new ItemBuilder();
                    String[] split = custom.split(", ");

                    for (String option : split) {

                        if (option.contains("Item:")) item.setMaterial(option.replace("Item:", ""));

                        if (option.contains("Name:")) {
                            option = option.replace("Name:", "");

                            option = getCrates(player, option);

                            item.setName(option.replaceAll("%player%", player.getName()));
                        }

                        if (option.contains("Lore:")) {
                            option = option.replace("Lore:", "");
                            String[] lore = option.split(",");

                            for (String line : lore) {
                                option = getCrates(player, option);

                                item.addLore(option.replaceAll("%player%", player.getName()));
                            }
                        }

                        if (option.contains("Glowing:")) item.setGlow(Boolean.parseBoolean(option.replace("Glowing:", "")));

                        if (option.contains("Player:")) item.setPlayerName(option.replaceAll("%player%", player.getName()));

                        if (option.contains("Slot:")) slot = Integer.parseInt(option.replace("Slot:", ""));

                        if (option.contains("Unbreakable-Item")) item.setUnbreakable(Boolean.parseBoolean(option.replace("Unbreakable-Item:", "")));

                        if (option.contains("Hide-Item-Flags")) item.hideItemFlags(Boolean.parseBoolean(option.replace("Hide-Item-Flags:", "")));
                    }

                    if (slot > size) continue;

                    slot--;
                    inv.setItem(slot, item.build());
                }
            }
        }

        for (Crate crate : plugin.getCrateManager().getCrates()) {
            FileConfiguration file = crate.getFile();

            if (file != null) {
                if (file.getBoolean("Crate.InGUI")) {
                    String path = "Crate.";
                    int slot = file.getInt(path + "Slot");

                    if (slot > size) continue;

                    slot--;
                    inv.setItem(slot, new ItemBuilder()
                    .setMaterial(file.getString(path + "Item"))
                    .setName(file.getString(path + "Name"))
                    .setLore(file.getStringList(path + "Lore"))
                    .setCrateName(crate.getName())
                    .setPlayerName(file.getString(path + "Player"))
                    .setGlow(file.getBoolean(path + "Glowing"))
                    .addLorePlaceholder("%Keys%", NumberFormat.getNumberInstance().format(crazyHandler.getUserManager().getVirtualKeys(player.getUniqueId(), crate.getName())))
                    .addLorePlaceholder("%Keys_Physical%", NumberFormat.getNumberInstance().format(crazyHandler.getUserManager().getPhysicalKeys(player.getUniqueId(), crate.getName())))
                    .addLorePlaceholder("%Keys_Total%", NumberFormat.getNumberInstance().format(crazyHandler.getUserManager().getTotalKeys(player.getUniqueId(), crate.getName())))
                    .addLorePlaceholder("%crate_opened%", NumberFormat.getNumberInstance().format(crazyHandler.getUserManager().getCrateOpened(player.getUniqueId(), crate.getName())))
                    .addLorePlaceholder("%Player%", player.getName())
                    .build());
                }
            }
        }

        player.openInventory(inv);
    }

    private static String getCrates(Player player, String option) {
        for (Crate crate : plugin.getCrateManager().getCrates()) {
            if (crate.getCrateType() != CrateType.menu) {
                option = option.replaceAll("%" + crate.getName().toLowerCase() + "%", crazyHandler.getUserManager().getVirtualKeys(player.getUniqueId(), crate.getName()) + "")
                .replaceAll("%" + crate.getName().toLowerCase() + "_physical%", crazyHandler.getUserManager().getPhysicalKeys(player.getUniqueId(), crate.getName()) + "")
                .replaceAll("%" + crate.getName().toLowerCase() + "_total%", crazyHandler.getUserManager().getTotalKeys(player.getUniqueId(), crate.getName()) + "")
                .replaceAll("%" + crate.getName().toLowerCase() + "_opened%", crazyHandler.getUserManager().getCrateOpened(player.getUniqueId(), crate.getName()) + "");
            }
        }

        return option;
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        Inventory inv = e.getClickedInventory();

        if (inv != null) {
            for (Crate crate : plugin.getCrateManager().getCrates()) {
                if (crate.getCrateType() != CrateType.menu && crate.isCrateMenu(e.getView())) return;
            }

            if (e.getView().getTitle().equals(MsgUtils.sanitizeColor(config.getProperty(Config.inventory_name)))) {
                e.setCancelled(true);

                if (e.getCurrentItem() != null) {
                    ItemStack item = e.getCurrentItem();

                    if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                        NBTItem nbtItem = new NBTItem(item);

                        if (nbtItem.hasNBTData() && nbtItem.hasTag("CrazyCrates-Crate")) {
                            Crate crate = plugin.getCrateManager().getCrateFromName(nbtItem.getString("CrazyCrates-Crate"));

                            if (crate != null) {
                                if (e.getAction() == InventoryAction.PICKUP_HALF) { // Right-clicked the item

                                    if (crate.isPreviewEnabled()) {
                                        player.closeInventory();
                                        PreviewListener.setPlayerInMenu(player, true);
                                        PreviewListener.openNewPreview(player, crate);
                                    } else {
                                        player.sendMessage(Translation.preview_disabled.getString());
                                    }

                                    return;
                                }

                                if (crateManager.isInOpeningList(player)) {
                                    player.sendMessage(Translation.already_opening_crate.getString());
                                    return;
                                }

                                boolean hasKey = false;
                                KeyType keyType = KeyType.virtual_key;

                                if (plugin.getCrazyHandler().getUserManager().getVirtualKeys(player.getUniqueId(), crate.getName()) >= 1) {
                                    hasKey = true;
                                } else {
                                    if (config.getProperty(Config.virtual_accepts_physical_keys) && crazyHandler.getUserManager().hasPhysicalKey(player.getUniqueId(), crate.getName(), false)) {
                                        hasKey = true;
                                        keyType = KeyType.physical_key;
                                    }
                                }

                                if (!hasKey) {
                                    if (config.getProperty(Config.need_key_sound_toggle)) {
                                        player.playSound(player.getLocation(), Sound.valueOf(config.getProperty(Config.need_key_sound)), 1f, 1f);
                                    }

                                    player.sendMessage(Translation.no_virtual_key.getString());
                                    return;
                                }

                                for (String world : getDisabledWorlds()) {
                                    if (world.equalsIgnoreCase(player.getWorld().getName())) {
                                        player.sendMessage(Translation.world_disabled.getMessage("%world%", player.getWorld().getName()).toString());
                                        return;
                                    }
                                }

                                if (MiscUtils.isInventoryFull(player)) {
                                    player.sendMessage(Translation.inventory_not_empty.getString());
                                    return;
                                }

                                crateManager.openCrate(player, crate, keyType, player.getLocation(), true, false);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private ArrayList<String> getDisabledWorlds() {
        return new ArrayList<>(config.getProperty(Config.disabledWorlds));
    }
}