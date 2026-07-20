package com.badbones69.crazycrates.paper.tasks.menus;

import com.ryderbelserion.fusion.paper.builders.gui.enums.GuiBorder;
import us.crazycrew.crazycrates.api.config.impl.types.config.crate.CrateKeys;
import us.crazycrew.crazycrates.api.config.impl.types.config.gui.GuiKeys;
import us.crazycrew.crazycrates.api.enums.messages.Message;
import com.badbones69.crazycrates.paper.api.builders.gui.StaticInventoryBuilder;
import com.badbones69.crazycrates.paper.api.enums.other.keys.ItemKeys;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.managers.events.enums.EventType;
import com.badbones69.crazycrates.paper.utils.ItemUtil;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.paper.builders.gui.types.simple.SimpleGui;
import com.ryderbelserion.fusion.paper.builders.items.ItemBuilder;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CrateMainMenu extends StaticInventoryBuilder {

    public CrateMainMenu(@NotNull final Player player, @NotNull final String title, final int rows) {
        super(player, title, rows);
    }

    private final Player player = getPlayer();
    private final SimpleGui gui = getGui();

    @Override
    public void open() {
        final UUID uuid = this.player.getUniqueId();

        this.inventoryManager.getFillerButton(this.player).ifPresent(itemStack -> this.gui.getFiller().fill(GuiBorder.REMAINING_SLOTS, itemStack));

        if (this.pluginConfig.getProperty(GuiKeys.is_gui_customizer_enabled)) {
            for (final String custom : this.pluginConfig.getProperty(GuiKeys.gui_customizer)) {
                ItemBuilder item = ItemBuilder.from(ItemType.STONE);

                int slot = 0;

                for (String key : custom.split(", ")) {
                    String option = key.split(":")[0];
                    String value = key.replace(option + ":", "").replace(option, "");

                    switch (option.toLowerCase()) {
                        case "item" -> item = ItemBuilder.from(value);
                        case "name" -> item.withDisplayName(getCrates(value).replace("{player}", player.getName()));

                        case "lore" -> {
                            String[] lore = value.split(",");

                            for (String line : lore) {
                                item.addDisplayLore(getCrates(line).replace("{player}", player.getName()));
                            }
                        }

                        case "custom-model-data" -> ItemUtil.addCustomModel(item, value);

                        case "glowing" -> ItemUtil.addGlow(item, value);

                        case "slot" -> slot = StringUtils.tryParseInt(value).orElse(-1).intValue();

                        case "unbreakable-item" -> item.setUnbreakable(StringUtils.tryParseBoolean(value).orElse(false));

                        case "hide-tool-tip" -> {
                            final boolean isEnabled = StringUtils.tryParseBoolean(value).orElse(false);

                            if (isEnabled) {
                                item.hideToolTip();
                            }
                        }

                        case "components" -> {
                            final List<String> components = Arrays.stream(value.split(",")).toList();

                            if (!components.isEmpty()) {
                                item.hideComponents(components);
                            }
                        }

                        case "command" -> item.setPersistentString(ItemKeys.crate_command.getNamespacedKey(), value);
                    }
                }

                this.gui.addSlotAction(slot, item.asGuiItem(this.player, action -> {
                    final ItemStack itemStack = action.getCurrentItem();

                    if (itemStack == null || itemStack.isEmpty()) return;

                    final PersistentDataContainerView container = itemStack.getPersistentDataContainer();

                    if (!container.has(ItemKeys.crate_command.getNamespacedKey())) return;

                    final String command = container.get(ItemKeys.crate_command.getNamespacedKey(), PersistentDataType.STRING);

                    if (command == null) return;

                    MiscUtils.sendCommand(this.player, command, Map.of("{player}", this.player.getName()));
                }));
            }
        }

        for (final Crate crate : this.crateManager.getUsableCrates()) {
            final ConfigurationSection section = crate.getSection();

            if (!section.getBoolean("InGUI", false)) continue;

            final String fileName = crate.getFileName();

            int slot = section.getInt("Slot", -1);

            if (slot == -1) continue;

            final int virtualKeys = this.userManager.getVirtualKeys(uuid, fileName);
            final int physicalKeys = this.userManager.getPhysicalKeys(uuid, fileName);

            final int totalKeys = virtualKeys + physicalKeys;

            final int openedCrates = this.userManager.getCrateOpened(uuid, fileName);

            final NumberFormat instance = NumberFormat.getNumberInstance();

            final ItemBuilder builder = ItemBuilder.from(section.getString("Item", "chest").toLowerCase())
                    .withDisplayName(crate.getCrateName())
                    .addPlaceholder("%keys%", instance.format(virtualKeys))
                    .addPlaceholder("%keys_physical%", instance.format(physicalKeys))
                    .addPlaceholder("%keys_total%", instance.format(totalKeys))
                    .addPlaceholder("%crate_opened%", instance.format(openedCrates))
                    .addPlaceholder("%keys_raw%", String.valueOf(virtualKeys))
                    .addPlaceholder("%keys_physical_raw%", String.valueOf(physicalKeys))
                    .addPlaceholder("%keys_total_raw%", String.valueOf(totalKeys))
                    .addPlaceholder("%crate_opened_raw%", String.valueOf(openedCrates))
                    .addPlaceholder("%player%", this.player.getName())
                    .setPersistentString(ItemKeys.crate_key.getNamespacedKey(), fileName);

            ItemUtil.addCustomModel(builder, section.getString("Custom-Model-Data", ""));
            ItemUtil.addItemModel(builder, section.getString("Model.Namespace", ""), section.getString("Model.Id", ""));

            this.gui.addSlotAction(slot, ItemUtil.getItem(section, builder).asGuiItem(event -> {
                final String fancyName = crate.getCrateName();

                switch (event.getClick()) {
                    case ClickType.LEFT -> {
                        if (this.pluginConfig.getProperty(CrateKeys.crate_virtual_interaction)) {
                            openPreview(crate, fancyName);
                        } else {
                            openCrate(uuid, crate, fileName, fancyName);
                        }
                    }

                    case ClickType.RIGHT -> {
                        if (this.pluginConfig.getProperty(CrateKeys.crate_virtual_interaction)) {
                            openCrate(uuid, crate, fileName, fancyName);
                        } else {
                            openPreview(crate, fancyName);
                        }
                    }
                }
            }));
        }

        this.gui.open(this.player);
    }

    private void openCrate(@NotNull final UUID uuid, @NotNull final Crate crate, @NotNull final String fileName, @NotNull final String fancyName) {
        if (this.cacheManager.hasOpeningCrate(uuid)) {
            Message.crate_already_opened.sendMessage(this.player, "{crate}", fancyName);

            return;
        }

        boolean hasKey = false;
        KeyType keyType = KeyType.virtual_key;

        if (this.userManager.getVirtualKeys(uuid, fileName) >= 1) {
            hasKey = true;
        } else {
            if (this.pluginConfig.getProperty(CrateKeys.virtual_accepts_physical_keys) && this.userManager.hasPhysicalKey(uuid, fileName, false)) {
                hasKey = true;
                keyType = KeyType.physical_key;
            }
        }

        if (!hasKey) {
            if (crate.isPlaySound()) {
                this.player.playSound(Sound.sound(Key.key(crate.getSound()), Sound.Source.MASTER, 1f, 1f));
            }

            Message.no_virtual_keys.sendMessage(this.player, "{crate}", fancyName);

            return;
        }

        if (MiscUtils.isInventoryFull(this.player)) {
            Message.inventory_not_empty.sendMessage(this.player, "{crate}", fancyName);

            return;
        }

        this.crateManager.openCrate(this.player, crate, keyType, this.player.getLocation(), true, false, EventType.event_crate_opened);
    }

    private void openPreview(@NotNull final Crate crate, @NotNull final String fancyName) {
        if (crate.isPreviewEnabled()) {
            crate.playSound(this.player, this.player.getLocation(), "click-sound", "ui.button.click", Sound.Source.MASTER);

            this.gui.close(this.player, InventoryCloseEvent.Reason.OPEN_NEW, false);

            this.inventoryManager.openNewCratePreview(this.player, crate);

            return;
        }

        Message.preview_disabled.sendMessage(this.player, "{crate}", fancyName);
    }
}