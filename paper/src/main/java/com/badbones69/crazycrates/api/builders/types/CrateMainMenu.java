package com.badbones69.crazycrates.api.builders.types;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.hooks.HeadDatabaseListener;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.builders.ItemBuilder;
import com.badbones69.crazycrates.api.utils.ItemUtils;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import com.badbones69.crazycrates.tasks.InventoryManager;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.platform.config.ConfigManager;
import us.crazycrew.crazycrates.platform.config.impl.ConfigKeys;
import com.badbones69.crazycrates.api.builders.InventoryBuilder;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import java.text.NumberFormat;
import java.util.List;
import java.util.UUID;

public class CrateMainMenu extends InventoryBuilder {

    private final @NotNull BukkitUserManager userManager = this.plugin.getUserManager();

    private final @NotNull CrateManager crateManager = this.plugin.getCrateManager();

    private final @NotNull SettingsManager config = ConfigManager.getConfig();

    public CrateMainMenu(@NotNull final Player player, final int size, @NotNull final String title) {
        super(player, size, title);
    }

    @Override
    public InventoryBuilder build() {
        Inventory inventory = getInventory();

        Player player = getPlayer();
        UUID uuid = player.getUniqueId();

        if (this.config.getProperty(ConfigKeys.filler_toggle)) {
            String id = this.config.getProperty(ConfigKeys.filler_item);
            String name = this.config.getProperty(ConfigKeys.filler_name);
            List<String> lore = this.config.getProperty(ConfigKeys.filler_lore);

            ItemStack item = new ItemBuilder().setMaterial(id).setDisplayName(name).setDisplayLore(lore).setTarget(getPlayer()).build();

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

                            item.setDisplayName(option.replace("{player}", player.getName())); // ryder - this didn't need to be replaceAll, what logic would an owner need to put the player name twice in the displayName?
                        }

                        if (option.contains("lore:")) {
                            option = option.replace("lore:", "");
                            String[] lore = option.split(",");

                            for (String line : lore) {
                                option = getCrates(option);

                                item.addDisplayLore(option.replaceAll("\\{player}", player.getName()));
                            }
                        }

                        if (option.contains("glowing:")) item.setGlowing(option.replace("glowing:", "").equalsIgnoreCase("true"));

                        //todo() test the new options.
                        if (option.contains("hdb:")) item.setSkull(option.replace("hdb:", ""), HeadDatabaseListener.getHeads());

                        if (option.contains("player:")) item.setPlayer(option.replace("{player}", player.getName())); // ryder - this doesn't need to be replaceAll, what logic would an owner need to put the player name twice in the displayName?

                        if (option.contains("slot:")) slot = Integer.parseInt(option.replace("slot:", ""));

                        if (option.contains("unbreakable-item:")) item.setUnbreakable(option.replace("unbreakable-item:", "").equalsIgnoreCase("true"));

                        if (option.contains("hide-item-flags:")) item.hideItemFlags(option.replace("hide-item-flags:", "").equalsIgnoreCase("true"));
                    }

                    if (slot > getSize()) continue;

                    slot--;

                    inventory.setItem(slot, item.setTarget(player).build());
                }
            }
        }

        for (Crate crate : this.crateManager.getUsableCrates()) {
            FileConfiguration file = crate.getFile();

            if (file != null) {
                ConfigurationSection section = file.getConfigurationSection("Crate");

                if (section != null) {
                    if (section.getBoolean("InGUI", false)) {
                        String crateName = crate.getName();

                        int slot = section.getInt("Slot");

                        if (slot > getSize()) continue;

                        slot--;

                        ItemBuilder builder = new ItemBuilder().addLorePlaceholder("%keys%", NumberFormat.getNumberInstance().format(this.userManager.getVirtualKeys(uuid, crateName)))
                                .addLorePlaceholder("%keys_physical%", NumberFormat.getNumberInstance().format(this.userManager.getPhysicalKeys(uuid, crateName)))
                                .addLorePlaceholder("%keys_total%", NumberFormat.getNumberInstance().format(this.userManager.getTotalKeys(uuid, crateName)))
                                .addLorePlaceholder("%crate_opened%", NumberFormat.getNumberInstance().format(this.userManager.getCrateOpened(uuid, crateName)))
                                .addLorePlaceholder("%player%", getPlayer().getName()).setCrateName(crate.getName()).setDisplayName(section.getString("CrateName", crateName)).setMaterial(section.getString("Item", "chest"));

                        inventory.setItem(slot, ItemUtils.getItem(section, builder, player).build());
                    }
                }
            }
        }

        return this;
    }

    private String getCrates(String option) {
    private @NotNull String getCrates(@NotNull String option) {
        if (option.isEmpty()) return "";
        for (Crate crate : this.crateManager.getUsableCrates()) {
            option = option.replaceAll("%" + crate.getName().toLowerCase() + "}", this.userManager.getVirtualKeys(getPlayer().getUniqueId(), crate.getName()) + "")
                    .replaceAll("%" + crate.getName().toLowerCase() + "_physical%", this.userManager.getPhysicalKeys(getPlayer().getUniqueId(), crate.getName()) + "")
                    .replaceAll("%" + crate.getName().toLowerCase() + "_total%", this.userManager.getTotalKeys(getPlayer().getUniqueId(), crate.getName()) + "")
                    .replaceAll("%" + crate.getName().toLowerCase() + "_opened%", this.userManager.getCrateOpened(getPlayer().getUniqueId(), crate.getName()) + "");
        }

        return option;
    }

    public static class CrateMenuListener implements Listener {

        private final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

        private final @NotNull InventoryManager inventoryManager = this.plugin.getInventoryManager();

        private final @NotNull SettingsManager config = ConfigManager.getConfig();

        private final @NotNull CrateManager crateManager = this.plugin.getCrateManager();

        private final @NotNull BukkitUserManager userManager = this.plugin.getUserManager();

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            Inventory inventory = event.getInventory();

            if (!(inventory.getHolder(false) instanceof CrateMainMenu holder)) return;

            event.setCancelled(true);

            Player player = holder.getPlayer();
            Location location = player.getLocation();
            String playerWorld = player.getWorld().getName();
            UUID uuid = player.getUniqueId();

            ItemStack item = event.getCurrentItem();

            if (item == null || item.getType() == Material.AIR) return;

            if (!item.hasItemMeta()) return;

            Crate crate = this.crateManager.getCrateFromName(ItemUtils.getKey(item.getItemMeta()));

            if (crate == null) return;

            String crateName = crate.getName();

            if (event.getAction() == InventoryAction.PICKUP_HALF) { // Right-clicked the item
                if (crate.isPreviewEnabled()) {
                    crate.playSound(player, location, "click-sound", "ui.button.click", SoundCategory.PLAYERS);

                    player.closeInventory();

                    this.inventoryManager.addViewer(player);
                    this.inventoryManager.openNewCratePreview(player, crate);
                } else {
                    player.sendRichMessage(Messages.preview_disabled.getMessage(player, "{crate}", crateName));
                }

                return;
            }

            if (this.crateManager.isInOpeningList(player)) {
                player.sendRichMessage(Messages.already_opening_crate.getMessage(player, "{crate}", crateName));

                return;
            }

            boolean hasKey = false;
            KeyType keyType = KeyType.virtual_key;

            if (this.userManager.getVirtualKeys(uuid, crateName) >= 1) {
                hasKey = true;
            } else {
                if (this.config.getProperty(ConfigKeys.virtual_accepts_physical_keys) && this.userManager.hasPhysicalKey(uuid, crateName, false)) {
                    hasKey = true;
                    keyType = KeyType.physical_key;
                }
            }

            if (!hasKey) {
                if (this.config.getProperty(ConfigKeys.need_key_sound_toggle)) {
                    player.playSound(player.getLocation(), Sound.valueOf(this.config.getProperty(ConfigKeys.need_key_sound)), SoundCategory.PLAYERS, 1f, 1f);
                }

                player.sendRichMessage(Messages.no_virtual_key.getMessage(player, "{crate}", crateName));

                return;
            }

            for (String world : this.config.getProperty(ConfigKeys.disabled_worlds)) {
                if (world.equalsIgnoreCase(playerWorld)) {
                    player.sendRichMessage(Messages.world_disabled.getMessage(player, "{world}", playerWorld));

                    return;
                }
            }

            if (MiscUtils.isInventoryFull(player)) {
                player.sendRichMessage(Messages.inventory_not_empty.getMessage(player, "{crate}", crateName));

                return;
            }

            this.crateManager.openCrate(player, crate, keyType, location, true, false);
        }
    }
}