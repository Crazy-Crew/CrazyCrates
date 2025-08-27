package com.badbones69.crazycrates.paper.tasks.menus;

import com.badbones69.crazycrates.core.config.beans.ModelData;
import com.badbones69.crazycrates.paper.api.builders.LegacyItemBuilder;
import com.badbones69.crazycrates.paper.api.builders.gui.StaticInventoryBuilder;
import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.api.enums.other.keys.ItemKeys;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.managers.events.enums.EventType;
import com.badbones69.crazycrates.paper.utils.ItemUtils;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import com.badbones69.crazycrates.core.config.impl.ConfigKeys;
import com.ryderbelserion.fusion.core.api.utils.StringUtils;
import com.ryderbelserion.fusion.paper.api.builders.gui.interfaces.Gui;
import com.ryderbelserion.fusion.paper.api.builders.gui.interfaces.GuiFiller;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import java.text.NumberFormat;
import java.util.UUID;

public class CrateMainMenu extends StaticInventoryBuilder {

    public CrateMainMenu(@NotNull final Player player, @NotNull final String title, final int rows) {
        super(player, title, rows);
    }

    private final Player player = getPlayer();
    private final Gui gui = getGui();

    @Override
    public void open() {
        final UUID uuid = this.player.getUniqueId();

        if (this.config.getProperty(ConfigKeys.filler_toggle)) {
            final GuiFiller guiFiller = gui.getFiller();

            final ModelData fillerModel = this.config.getProperty(ConfigKeys.filler_item_model);

            guiFiller.fill(new LegacyItemBuilder(this.plugin)
                    .withType(this.config.getProperty(ConfigKeys.filler_item))
                    .setDisplayName(this.config.getProperty(ConfigKeys.filler_name))
                    .setDisplayLore(this.config.getProperty(ConfigKeys.filler_lore))
                    .setCustomModelData(this.config.getProperty(ConfigKeys.filler_model_data))
                    .setItemModel(fillerModel.getNamespace(), fillerModel.getId())
                    .asGuiItem());
        }

        if (this.config.getProperty(ConfigKeys.gui_customizer_toggle)) {
            for (String custom : this.config.getProperty(ConfigKeys.gui_customizer)) {
                final LegacyItemBuilder item = new LegacyItemBuilder(this.plugin);

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

                        case "custom-model-data" -> item.setCustomModelData(value);

                        case "glowing" -> item.setGlowing(StringUtils.tryParseBoolean(value).orElse(false));

                        case "slot" -> slot = StringUtils.tryParseInt(value).orElse(-1).intValue();

                        case "unbreakable-item" -> item.setUnbreakable(StringUtils.tryParseBoolean(value).orElse(false));

                        case "hide-item-flags" -> item.setHidingItemFlags(StringUtils.tryParseBoolean(value).orElse(false));

                        case "command" -> MiscUtils.sendCommand(value);
                    }
                }

                this.gui.setItem(slot, item.setPlayer(this.player).asGuiItem());
            }
        }

        for (Crate crate : this.crateManager.getUsableCrates()) {
            final YamlConfiguration file = crate.getFile();

            final ConfigurationSection section = file.getConfigurationSection("Crate");

            if (section != null) {
                if (section.getBoolean("InGUI", false)) {
                    final String fileName = crate.getFileName();

                    int slot = section.getInt("Slot");

                    final int virtualKeys = this.userManager.getVirtualKeys(uuid, fileName);
                    final int physicalKeys = this.userManager.getPhysicalKeys(uuid, fileName);

                    final int totalKeys = virtualKeys + physicalKeys;

                    final int openedCrates = this.userManager.getCrateOpened(uuid, fileName);

                    final NumberFormat instance = NumberFormat.getNumberInstance();

                    final LegacyItemBuilder builder = new LegacyItemBuilder(this.plugin)
                            .withType(section.getString("Item", "chest").toLowerCase())
                            .setDisplayName(crate.getCrateName())
                            .setCustomModelData(section.getString("Custom-Model-Data", ""))
                            .setItemModel(section.getString("Model.Namespace", ""), section.getString("Model.Id", ""))
                            .addLorePlaceholder("%keys%", instance.format(virtualKeys))
                            .addLorePlaceholder("%keys_physical%", instance.format(physicalKeys))
                            .addLorePlaceholder("%keys_total%", instance.format(totalKeys))
                            .addLorePlaceholder("%crate_opened%", instance.format(openedCrates))
                            .addLorePlaceholder("%keys_raw%", String.valueOf(virtualKeys))
                            .addLorePlaceholder("%keys_physical_raw%", String.valueOf(physicalKeys))
                            .addLorePlaceholder("%keys_total_raw%", String.valueOf(totalKeys))
                            .addLorePlaceholder("%crate_opened_raw", String.valueOf(openedCrates))
                            .addLorePlaceholder("%player%", this.player.getName())
                            .setPersistentString(ItemKeys.crate_key.getNamespacedKey(), fileName);

                    this.gui.setItem(slot, ItemUtils.getItem(section, builder, this.player).asGuiItem(event -> {
                        final String fancyName = crate.getCrateName();

                        switch (event.getClick()) {
                            case ClickType.LEFT -> {
                                final boolean isLeftClickToPreview = this.config.getProperty(ConfigKeys.crate_virtual_interaction);

                                if (isLeftClickToPreview) {
                                    openPreview(crate, fancyName);
                                } else {
                                    openCrate(uuid, crate, fileName, fancyName);
                                }
                            }

                            case ClickType.RIGHT -> {
                                final boolean isRightClickToOpen = this.config.getProperty(ConfigKeys.crate_virtual_interaction);

                                if (isRightClickToOpen) {
                                    openCrate(uuid, crate, fileName, fancyName);
                                } else {
                                    openPreview(crate, fancyName);
                                }
                            }
                        }
                    }));
                }
            }
        }

        this.gui.open(this.player);
    }

    private void openCrate(@NotNull final UUID uuid, @NotNull final Crate crate, @NotNull final String fileName, @NotNull final String fancyName) {
        if (this.crateManager.isInOpeningList(this.player)) {
            Messages.already_opening_crate.sendMessage(this.player, "{crate}", fancyName);

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
            if (this.config.getProperty(ConfigKeys.need_key_sound_toggle)) {
                final String property = this.config.getProperty(ConfigKeys.need_key_sound);
                final Sound sound = Sound.sound(Key.key(property), Sound.Source.MASTER, 1f, 1f);

                this.player.playSound(sound);
            }

            Messages.no_virtual_key.sendMessage(this.player, "{crate}", fancyName);

            return;
        }

        for (String world : this.config.getProperty(ConfigKeys.disabled_worlds)) {
            if (world.equalsIgnoreCase(this.player.getWorld().getName())) {
                Messages.world_disabled.sendMessage(this.player, "{world}", this.player.getWorld().getName());

                return;
            }
        }

        if (MiscUtils.isInventoryFull(this.player)) {
            Messages.inventory_not_empty.sendMessage(this.player, "{crate}", fancyName);

            return;
        }

        this.crateManager.openCrate(this.player, crate, keyType, this.player.getLocation(), true, false, EventType.event_crate_opened);
    }

    private void openPreview(@NotNull final Crate crate, @NotNull final String fancyName) {
        if (crate.isPreviewEnabled()) {
            crate.playSound(this.player, this.player.getLocation(), "click-sound", "ui.button.click", Sound.Source.MASTER);

            this.gui.close(this.player, InventoryCloseEvent.Reason.OPEN_NEW, false);

            this.inventoryManager.openNewCratePreview(this.player, crate);
        } else {
            Messages.preview_disabled.sendMessage(this.player, "{crate}", fancyName);
        }
    }
}