package com.badbones69.crazycrates.api.builders.types;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.enums.PersistentKeys;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.utils.ItemUtils;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import com.ryderbelserion.vital.common.utils.StringUtil;
import com.badbones69.crazycrates.api.builders.ItemBuilder;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.config.ConfigManager;
import com.badbones69.crazycrates.config.impl.ConfigKeys;
import com.badbones69.crazycrates.api.builders.InventoryBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import java.text.NumberFormat;
import java.util.List;
import java.util.UUID;

public class CrateMainMenu extends InventoryBuilder {

    private @NotNull final BukkitUserManager userManager = this.plugin.getUserManager();

    private @NotNull final SettingsManager config = ConfigManager.getConfig();

    public CrateMainMenu(@NotNull final Player player, @NotNull final String title, final int size) {
        super(player, title, size);
    }

    public CrateMainMenu() {}

    @Override
    public InventoryBuilder build() {
        final Inventory inventory = getInventory();

        final Player player = getPlayer();
        final UUID uuid = player.getUniqueId();

        if (this.config.getProperty(ConfigKeys.filler_toggle)) {
            final String id = this.config.getProperty(ConfigKeys.filler_item);
            final String name = this.config.getProperty(ConfigKeys.filler_name);
            final List<String> lore = this.config.getProperty(ConfigKeys.filler_lore);

            final ItemStack item = new ItemBuilder().withType(id.toLowerCase()).setDisplayName(name).setDisplayLore(lore).setPlayer(getPlayer()).setCustomModelData(this.config.getProperty(ConfigKeys.filler_model_data)).getStack();

            for (int i = 0; i < getSize(); i++) {
                inventory.setItem(i, item);
            }
        }

        if (this.config.getProperty(ConfigKeys.gui_customizer_toggle)) {
            final List<String> customizer = this.config.getProperty(ConfigKeys.gui_customizer);

            if (!customizer.isEmpty()) {
                for (String custom : customizer) {
                    final ItemBuilder item = new ItemBuilder();
                    int slot = 0;

                    for (String key : custom.split(", ")) {
                        String option = key.split(":")[0];
                        String value = key.replace(option + ":", "").replace(option, "");

                        switch (option.toLowerCase()) {
                            case "item" -> item.withType(value.toLowerCase());
                            case "name" -> item.setDisplayName(getCrates(value).replace("{player}", player.getName()));

                            case "lore" -> {
                                String[] lore = value.split(",");

                                for (String line : lore) {
                                    item.addDisplayLore(getCrates(line).replace("{player}", player.getName()));
                                }
                            }

                            case "custom-model-data" -> item.setCustomModelData(StringUtil.tryParseInt(value).orElse(-1).intValue());

                            case "glowing" -> item.setGlowing(StringUtil.tryParseBoolean(value).orElse(null));

                            case "slot" -> slot = StringUtil.tryParseInt(value).orElse(-1).intValue();

                            case "unbreakable-item" -> item.setUnbreakable(StringUtil.tryParseBoolean(value).orElse(false));

                            case "hide-item-flags" -> item.setHidingItemFlags(StringUtil.tryParseBoolean(value).orElse(false));
                        }
                    }

                    if (slot > getSize()) continue;

                    slot--;

                    inventory.setItem(slot, item.setPlayer(player).getStack());
                }
            }
        }

        for (Crate crate : this.crateManager.getUsableCrates()) {
            final YamlConfiguration file = crate.getFile();

            final ConfigurationSection section = file.getConfigurationSection("Crate");

            if (section != null) {
                if (section.getBoolean("InGUI", false)) {
                    final String fileName = crate.getFileName();

                    int slot = section.getInt("Slot");

                    if (slot > getSize()) continue;

                    slot--;

                    final ItemBuilder builder = new ItemBuilder()
                            .withType(section.getString("Item", "chest").toLowerCase())
                            .setDisplayName(crate.getCrateName())
                            .setCustomModelData(section.getInt("Custom-Model-Data", -1))
                            .addLorePlaceholder("%keys%", NumberFormat.getNumberInstance().format(this.userManager.getVirtualKeys(uuid, fileName)))
                            .addLorePlaceholder("%keys_physical%", NumberFormat.getNumberInstance().format(this.userManager.getPhysicalKeys(uuid, fileName)))
                            .addLorePlaceholder("%keys_total%", NumberFormat.getNumberInstance().format(this.userManager.getTotalKeys(uuid, fileName)))
                            .addLorePlaceholder("%crate_opened%", NumberFormat.getNumberInstance().format(this.userManager.getCrateOpened(uuid, fileName)))
                            .addLorePlaceholder("%player%", getPlayer().getName())
                            .setPersistentString(PersistentKeys.crate_key.getNamespacedKey(), fileName);

                    inventory.setItem(slot, ItemUtils.getItem(section, builder, player).getStack());
                }
            }
        }

        return this;
    }

    @Override
    public void run(InventoryClickEvent event) {
        final Inventory inventory = event.getInventory();

        if (!(inventory.getHolder(false) instanceof CrateMainMenu holder)) return;

        event.setCancelled(true);

        final Player player = holder.getPlayer();
        final Location location = player.getLocation();
        final String playerWorld = player.getWorld().getName();
        final UUID uuid = player.getUniqueId();

        final ItemStack item = event.getCurrentItem();

        if (item == null || item.getType() == Material.AIR) return;

        final Crate crate = this.crateManager.getCrateFromName(ItemUtils.getKey(item.getPersistentDataContainer()));

        if (crate == null) return;

        final String fileName = crate.getFileName();
        final String fancyName = crate.getCrateName();

        if (event.getAction() == InventoryAction.PICKUP_HALF) { // Right-clicked the item
            if (crate.isPreviewEnabled()) {
                crate.playSound(player, location, "click-sound", "ui.button.click", Sound.Source.PLAYER);

                player.closeInventory();

                this.inventoryManager.addViewer(player);
                this.inventoryManager.openNewCratePreview(player, crate);
            } else {
                Messages.preview_disabled.sendMessage(player, "{crate}", fancyName);
            }

            return;
        }

        if (this.crateManager.isInOpeningList(player)) {
            Messages.already_opening_crate.sendMessage(player, "{crate}", fancyName);

            return;
        }

        boolean hasKey = false;
        KeyType keyType = KeyType.virtual_key;

        if (this.userManager.getVirtualKeys(uuid, fileName) >= 1) {
            hasKey = true;
        } else {
            if (this.config.getProperty(ConfigKeys.virtual_accepts_physical_keys) && this.userManager.hasPhysicalKey(uuid, fileName, false)) {
                hasKey = true;
                keyType = KeyType.physical_key;
            }
        }

        if (!hasKey) {
            //todo() convert this to a bean property!
            if (this.config.getProperty(ConfigKeys.need_key_sound_toggle)) {
                Sound sound = Sound.sound(Key.key(this.config.getProperty(ConfigKeys.need_key_sound)), Sound.Source.PLAYER, 1f, 1f);

                player.playSound(sound);
            }

            Messages.no_virtual_key.sendMessage(player, "{crate}", fancyName);

            return;
        }

        for (String world : this.config.getProperty(ConfigKeys.disabled_worlds)) {
            if (world.equalsIgnoreCase(playerWorld)) {
                Messages.world_disabled.sendMessage(player, "{world}", playerWorld);

                return;
            }
        }

        if (MiscUtils.isInventoryFull(player)) {
            Messages.inventory_not_empty.sendMessage(player, "{crate}", fancyName);

            return;
        }

        this.crateManager.openCrate(player, crate, keyType, location, true, false);
    }

    private @NotNull String getCrates(@NotNull String option) {
        if (option.isEmpty()) return "";

        final UUID uuid = getPlayer().getUniqueId();

        for (Crate crate : this.crateManager.getUsableCrates()) {
            final String fileName = crate.getFileName();
            final String lowerCase = fileName.toLowerCase();

            option = option.replaceAll("%" + lowerCase + "}", this.userManager.getVirtualKeys(uuid, fileName) + "")
                    .replaceAll("%" + lowerCase + "_physical%", this.userManager.getPhysicalKeys(uuid, fileName) + "")
                    .replaceAll("%" + lowerCase + "_total%", this.userManager.getTotalKeys(uuid, fileName) + "")
                    .replaceAll("%" + lowerCase + "_opened%", this.userManager.getCrateOpened(uuid, fileName) + "");
        }

        return option;
    }
}