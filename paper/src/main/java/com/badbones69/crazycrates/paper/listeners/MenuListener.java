package com.badbones69.crazycrates.paper.listeners;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.Methods;
import com.badbones69.crazycrates.paper.api.CrazyManager;
import com.badbones69.crazycrates.paper.api.enums.settings.Messages;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.ItemBuilder;
import com.badbones69.crazycrates.paper.api.FileManager.Files;
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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class MenuListener implements Listener {

    private static final CrazyCrates plugin = CrazyCrates.getPlugin();

    private static final CrazyHandler crazyHandler = plugin.getCrazyHandler();

    private static final CrazyManager crazyManager = plugin.getStarter().getCrazyManager();

    private static final Methods methods = crazyHandler.getMethods();
    
    public static void openGUI(Player player) {
        FileConfiguration config = Files.CONFIG.getFile();

        int size = config.getInt("Settings.InventorySize");
        Inventory inv = player.getServer().createInventory(null, size, plugin.getCrazyHandler().getMethods().sanitizeColor(Files.CONFIG.getFile().getString("Settings.InventoryName")));

        if (config.contains("Settings.Filler.Toggle")) {
            if (config.getBoolean("Settings.Filler.Toggle")) {
                String id = config.getString("Settings.Filler.Item");
                String name = config.getString("Settings.Filler.Name");
                List<String> lore = config.getStringList("Settings.Filler.Lore");
                ItemStack item = new ItemBuilder().setMaterial(id).setName(name).setLore(lore).build();

                for (int i = 0; i < size; i++) {
                    inv.setItem(i, item.clone());
                }
            }
        }

        if (config.contains("Settings.GUI-Customizer")) {
            for (String custom : config.getStringList("Settings.GUI-Customizer")) {
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
                        String[] d = option.split(",");

                        for (String l : d) {
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

        for (Crate crate : crazyManager.getCrates()) {
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
        for (Crate crate : crazyManager.getCrates()) {
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
        Inventory inv = e.getInventory();
        FileConfiguration config = Files.CONFIG.getFile();

        if (inv != null) {

            for (Crate crate : crazyManager.getCrates()) {
                if (crate.getCrateType() != CrateType.menu && crate.isCrateMenu(e.getView())) return;
            }

            if (e.getView().getTitle().equals(methods.sanitizeColor(config.getString("Settings.InventoryName")))) {
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
                                        player.sendMessage(Messages.PREVIEW_DISABLED.getMessage());
                                    }

                                    return;
                                }

                                if (crazyManager.isInOpeningList(player)) {
                                    player.sendMessage(Messages.CRATE_ALREADY_OPENED.getMessage());
                                    return;
                                }

                                boolean hasKey = false;
                                KeyType keyType = KeyType.virtual_key;

                                if (plugin.getCrazyHandler().getUserManager().getVirtualKeys(player.getUniqueId(), crate.getName()) >= 1) {
                                    hasKey = true;
                                } else {
                                    if (Files.CONFIG.getFile().getBoolean("Settings.Virtual-Accepts-Physical-Keys") && crazyManager.hasPhysicalKey(player, crate, false)) {
                                        hasKey = true;
                                        keyType = KeyType.physical_key;
                                    }
                                }

                                if (!hasKey) {
                                    if (config.contains("Settings.Need-Key-Sound")) {
                                        Sound sound = Sound.valueOf(config.getString("Settings.Need-Key-Sound"));

                                        if (sound != null) player.playSound(player.getLocation(), sound, 1f, 1f);
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

                                if (methods.isInventoryFull(player)) {
                                    player.sendMessage(Messages.INVENTORY_FULL.getMessage());
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
    
    private ArrayList<String> getDisabledWorlds() {
        return new ArrayList<>(Files.CONFIG.getFile().getStringList("Settings.DisabledWorlds"));
    }
}