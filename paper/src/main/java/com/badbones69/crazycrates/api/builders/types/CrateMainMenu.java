package com.badbones69.crazycrates.api.builders.types;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.other.ItemBuilder;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import com.badbones69.crazycrates.tasks.InventoryManager;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import com.badbones69.crazycrates.common.config.ConfigManager;
import com.badbones69.crazycrates.common.config.types.ConfigKeys;
import com.badbones69.crazycrates.CrazyHandler;
import com.badbones69.crazycrates.api.builders.InventoryBuilder;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import java.text.NumberFormat;
import java.util.List;

public class CrateMainMenu extends InventoryBuilder {

    @NotNull
    private final BukkitUserManager userManager = this.plugin.getUserManager();

    @NotNull
    private final SettingsManager config = ConfigManager.getConfig();

    public CrateMainMenu(Player player, int size, String title) {
        super(player, size, title);
    }

    @Override
    public InventoryBuilder build() {
        Inventory inventory = getInventory();

        if (this.config.getProperty(ConfigKeys.filler_toggle)) {
            String id = this.config.getProperty(ConfigKeys.filler_item);
            String name = this.config.getProperty(ConfigKeys.filler_name);
            List<String> lore = this.config.getProperty(ConfigKeys.filler_lore);

            ItemStack item = new ItemBuilder().setMaterial(id).setName(name).setLore(lore).setTarget(getPlayer()).build();

            for (int i = 0; i < getSize(); i++) {
                inventory.setItem(i, item.clone());
            }
        }

        if (this.config.getProperty(ConfigKeys.gui_customizer_toggle)) {
            List<String> customizer = this.config.getProperty(ConfigKeys.gui_customizer);

            if (!customizer.isEmpty()) {
                for (String custom : customizer) {
                    int slot = 0;
                    ItemBuilder item = new ItemBuilder();
                    String[] split = custom.split(", ");

                    for (String option : split) {

                        if (option.contains("item:")) item.setMaterial(option.replace("item:", ""));

                        if (option.contains("name:")) {
                            option = option.replace("name:", "");

                            option = getCrates(option);

                            item.setName(option.replaceAll("\\{player}", getPlayer().getName()));
                        }

                        if (option.contains("lore:")) {
                            option = option.replace("lore:", "");
                            String[] lore = option.split(",");

                            for (String line : lore) {
                                option = getCrates(option);

                                item.addLore(option.replaceAll("\\{player}", getPlayer().getName()));
                            }
                        }

                        if (option.contains("glowing:")) item.setGlow(Boolean.parseBoolean(option.replace("glowing:", "")));

                        if (option.contains("player:")) item.setPlayerName(option.replaceAll("\\{player}", getPlayer().getName()));

                        if (option.contains("slot:")) slot = Integer.parseInt(option.replace("slot:", ""));

                        if (option.contains("unbreakable-item")) item.setUnbreakable(Boolean.parseBoolean(option.replace("unbreakable-item:", "")));

                        if (option.contains("hide-item-flags")) item.hideItemFlags(Boolean.parseBoolean(option.replace("hide-item-flags:", "")));
                    }

                    if (slot > getSize()) continue;

                    slot--;
                    inventory.setItem(slot, item.setTarget(getPlayer()).build());
                }
            }
        }

        for (Crate crate : this.plugin.getCrateManager().getUsableCrates()) {
            FileConfiguration file = crate.getFile();

            if (file != null) {
                if (file.getBoolean("Crate.InGUI", false)) {
                    String path = "Crate.";
                    int slot = file.getInt(path + "Slot");

                    if (slot > getSize()) continue;

                    slot--;

                    String name = file.getString(path + "Name", path + "Name is missing in " + crate.getName() + ".yml");

                    inventory.setItem(slot, new ItemBuilder()
                            .setTarget(getPlayer())
                            .setMaterial(file.getString(path + "Item", "CHEST"))
                            .setName(name)
                            .setLore(file.getStringList(path + "Lore"))
                            .setCrateName(crate.getName())
                            .setPlayerName(file.getString(path + "Player"))
                            .setGlow(file.getBoolean(path + "Glowing"))
                            .addLorePlaceholder("%keys%", NumberFormat.getNumberInstance().format(this.userManager.getVirtualKeys(getPlayer().getUniqueId(), crate.getName())))
                            .addLorePlaceholder("%keys_physical%", NumberFormat.getNumberInstance().format(this.userManager.getPhysicalKeys(getPlayer().getUniqueId(), crate.getName())))
                            .addLorePlaceholder("%keys_total%", NumberFormat.getNumberInstance().format(this.userManager.getTotalKeys(getPlayer().getUniqueId(), crate.getName())))
                            .addLorePlaceholder("%crate_opened%", NumberFormat.getNumberInstance().format(this.userManager.getCrateOpened(getPlayer().getUniqueId(), crate.getName())))
                            .addLorePlaceholder("%player%", getPlayer().getName())
                            .build());
                }
            }
        }

        return this;
    }

    private String getCrates(String option) {
        for (Crate crate : this.plugin.getCrateManager().getUsableCrates()) {
            option = option.replaceAll("%" + crate.getName().toLowerCase() + "}", this.userManager.getVirtualKeys(getPlayer().getUniqueId(), crate.getName()) + "")
                    .replaceAll("%" + crate.getName().toLowerCase() + "_physical%", this.userManager.getPhysicalKeys(getPlayer().getUniqueId(), crate.getName()) + "")
                    .replaceAll("%" + crate.getName().toLowerCase() + "_total%", this.userManager.getTotalKeys(getPlayer().getUniqueId(), crate.getName()) + "")
                    .replaceAll("%" + crate.getName().toLowerCase() + "_opened%", this.userManager.getCrateOpened(getPlayer().getUniqueId(), crate.getName()) + "");
        }

        return option;
    }

    public static class CrateMenuListener implements Listener {

        @NotNull
        private final CrazyCrates plugin = CrazyCrates.get();

        @NotNull
        private final InventoryManager inventoryManager = this.plugin.getCrazyHandler().getInventoryManager();

        @NotNull
        private final SettingsManager config = ConfigManager.getConfig();

        @NotNull
        private final CrazyHandler crazyHandler = this.plugin.getCrazyHandler();

        @NotNull
        private final CrateManager crateManager = this.plugin.getCrateManager();

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            Inventory inventory = event.getInventory();

            if (!(inventory.getHolder(false) instanceof CrateMainMenu holder)) return;

            event.setCancelled(true);

            Player player = holder.getPlayer();

            ItemStack item = event.getCurrentItem();

            if (item == null || item.getType() == Material.AIR) return;

            if (!item.hasItemMeta()) return;

            if (!item.getItemMeta().hasDisplayName()) return;

            NBTItem nbtItem = new NBTItem(item);

            if (!nbtItem.hasNBTData() && !nbtItem.hasTag("CrazyCrates-Crate")) return;

            Crate crate = this.plugin.getCrateManager().getCrateFromName(nbtItem.getString("CrazyCrates-Crate"));

            if (crate == null) return;

            if (event.getAction() == InventoryAction.PICKUP_HALF) { // Right-clicked the item
                if (crate.isPreviewEnabled()) {
                    crate.playSound(player, player.getLocation(), "click-sound", "UI_BUTTON_CLICK", SoundCategory.PLAYERS);

                    player.closeInventory();

                    this.inventoryManager.addViewer(player);
                    this.inventoryManager.openNewCratePreview(player, crate, crate.getCrateType() == CrateType.cosmic || crate.getCrateType() == CrateType.casino);
                } else {
                    player.sendMessage(Messages.preview_disabled.getMessage("{crate}", crate.getName(), player));
                }

                return;
            }

            if (this.crateManager.isInOpeningList(player)) {
                player.sendMessage(Messages.already_opening_crate.getMessage("{crate}", crate.getName(), player));
                return;
            }

            boolean hasKey = false;
            KeyType keyType = KeyType.virtual_key;

            if (this.plugin.getCrazyHandler().getUserManager().getVirtualKeys(player.getUniqueId(), crate.getName()) >= 1) {
                hasKey = true;
            } else {
                if (this.config.getProperty(ConfigKeys.virtual_accepts_physical_keys) && this.crazyHandler.getUserManager().hasPhysicalKey(player.getUniqueId(), crate.getName(), false)) {
                    hasKey = true;
                    keyType = KeyType.physical_key;
                }
            }

            if (!hasKey) {
                if (this.config.getProperty(ConfigKeys.need_key_sound_toggle)) {
                    player.playSound(player.getLocation(), Sound.valueOf(this.config.getProperty(ConfigKeys.need_key_sound)), SoundCategory.PLAYERS, 1f, 1f);
                }

                player.sendMessage(Messages.no_virtual_key.getMessage("{crate}", crate.getName(), player));
                return;
            }

            for (String world : this.config.getProperty(ConfigKeys.disabled_worlds)) {
                if (world.equalsIgnoreCase(player.getWorld().getName())) {
                    player.sendMessage(Messages.world_disabled.getMessage("{world}", player.getWorld().getName(), player));
                    return;
                }
            }

            if (MiscUtils.isInventoryFull(player)) {
                player.sendMessage(Messages.inventory_not_empty.getMessage("{crate}", crate.getName(), player));
                return;
            }

            this.crateManager.openCrate(player, crate, keyType, player.getLocation(), true, false);
        }
    }
}