package com.badbones69.crazycrates.api.builders.v2.types;

import com.badbones69.crazycrates.api.builders.ItemBuilder;
import com.badbones69.crazycrates.api.builders.v2.StaticInventoryBuilder;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.enums.misc.Keys;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.utils.ItemUtils;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.badbones69.crazycrates.config.impl.ConfigKeys;
import com.ryderbelserion.vital.common.utils.StringUtil;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.Gui;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.GuiFiller;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.GuiItem;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import java.text.NumberFormat;
import java.util.UUID;

public class CrateMainMenu extends StaticInventoryBuilder {

    public CrateMainMenu(final Player player, final String title, final int rows) {
        super(player, title, rows);
    }

    private final Player player = getPlayer();
    private final Gui gui = getGui();

    @Override
    public void open() {
        final UUID uuid = this.player.getUniqueId();

        if (this.config.getProperty(ConfigKeys.filler_toggle)) {
            final GuiFiller guiFiller = this.gui.getFiller();

            guiFiller.fill(new GuiItem(new ItemBuilder()
                    .withType(this.config.getProperty(ConfigKeys.filler_item))
                    .setDisplayName(this.config.getProperty(ConfigKeys.filler_name))
                    .setDisplayLore(this.config.getProperty(ConfigKeys.filler_lore))
                    .getStack()));
        }

        if (this.config.getProperty(ConfigKeys.gui_customizer_toggle)) {
            for (String custom : this.config.getProperty(ConfigKeys.gui_customizer)) {
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

                this.gui.setItem(slot, new GuiItem(item.setPlayer(this.player).getStack()));
            }
        }

        for (Crate crate : this.crateManager.getUsableCrates()) {
            final YamlConfiguration file = crate.getFile();

            final ConfigurationSection section = file.getConfigurationSection("Crate");

            if (section != null) {
                if (section.getBoolean("InGUI", false)) {
                    final String fileName = crate.getFileName();

                    int slot = section.getInt("Slot");

                    final ItemBuilder builder = new ItemBuilder()
                            .withType(section.getString("Item", "chest").toLowerCase())
                            .setDisplayName(crate.getCrateName())
                            .setCustomModelData(section.getInt("Custom-Model-Data", -1))
                            .addLorePlaceholder("%keys%", NumberFormat.getNumberInstance().format(this.userManager.getVirtualKeys(uuid, fileName)))
                            .addLorePlaceholder("%keys_physical%", NumberFormat.getNumberInstance().format(this.userManager.getPhysicalKeys(uuid, fileName)))
                            .addLorePlaceholder("%keys_total%", NumberFormat.getNumberInstance().format(this.userManager.getTotalKeys(uuid, fileName)))
                            .addLorePlaceholder("%crate_opened%", NumberFormat.getNumberInstance().format(this.userManager.getCrateOpened(uuid, fileName)))
                            .addLorePlaceholder("%player%", getPlayer().getName())
                            .setPersistentString(Keys.crate_key.getNamespacedKey(), fileName);

                    this.gui.setItem(slot, new GuiItem(ItemUtils.getItem(section, builder, player).getStack(), event -> {
                        final String fancyName = crate.getCrateName();

                        if (event.getAction() == InventoryAction.PICKUP_HALF) { // Right-clicked the item
                            if (crate.isPreviewEnabled()) {
                                crate.playSound(this.player, this.player.getLocation(), "click-sound", "ui.button.click", Sound.Source.PLAYER);

                                this.player.closeInventory();

                                this.inventoryManager.addViewer(this.player);//todo() this can be removed obviously now
                                this.inventoryManager.openNewCratePreview(this.player, crate);
                            } else {
                                Messages.preview_disabled.sendMessage(this.player, "{crate}", fancyName);
                            }

                            return;
                        }

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
                                Sound sound = Sound.sound(Key.key(this.config.getProperty(ConfigKeys.need_key_sound)), Sound.Source.PLAYER, 1f, 1f);

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

                        this.crateManager.openCrate(this.player, crate, keyType, this.player.getLocation(), true, false);
                    }));
                }
            }
        }

        this.gui.open(this.player);
    }

    private String getCrates(@NotNull String option) {
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