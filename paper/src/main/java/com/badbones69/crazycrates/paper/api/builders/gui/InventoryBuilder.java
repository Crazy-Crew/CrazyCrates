package com.badbones69.crazycrates.paper.api.builders.gui;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.core.config.beans.inventories.ItemPlacement;
import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.enums.other.Plugins;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.managers.BukkitUserManager;
import com.badbones69.crazycrates.paper.managers.InventoryManager;
import com.badbones69.crazycrates.core.config.ConfigManager;
import com.badbones69.crazycrates.core.config.impl.ConfigKeys;
import com.badbones69.crazycrates.paper.tasks.crates.CrateManager;
import com.badbones69.crazycrates.paper.tasks.menus.CrateMainMenu;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.builders.gui.interfaces.GuiItem;
import com.ryderbelserion.fusion.paper.builders.gui.types.BaseGui;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static java.util.regex.Matcher.quoteReplacement;

public abstract class InventoryBuilder {

    protected final Player player;

    public InventoryBuilder(@NotNull final Player player) {
        this.player = player;
    }

    protected final CrazyCrates plugin = CrazyCrates.getPlugin();

    protected final FusionPaper fusion = this.plugin.getFusion();

    protected final ComponentLogger logger = this.plugin.getComponentLogger();

    protected final CrateManager crateManager = this.plugin.getCrateManager();

    protected final BukkitUserManager userManager = this.plugin.getUserManager();

    protected final InventoryManager inventoryManager = this.plugin.getInventoryManager();

    protected final SettingsManager config = ConfigManager.getConfig();

    public void addMenuButton(@NotNull final Player player, @NotNull final Crate crate, @NotNull final BaseGui gui) {
        if (!this.config.getProperty(ConfigKeys.enable_crate_menu)) return;

        final ItemPlacement placement = this.config.getProperty(ConfigKeys.menu_button_placement);

        final int row = placement.getRow();
        final int column = placement.getColumn();

        final int rows = gui.getRows();

        final int safeRow = Math.min(row == -1 ? rows : row, rows);

        gui.setItem(safeRow, column, new GuiItem(this.inventoryManager.getMenuButton(player), action -> {
            if (this.config.getProperty(ConfigKeys.menu_button_override)) {
                final List<String> commands = this.config.getProperty(ConfigKeys.menu_button_command_list);

                if (!commands.isEmpty()) {
                    commands.forEach(value -> {
                        final String command = value.replaceAll("%player%", quoteReplacement(player.getName())).replaceAll("%crate%", quoteReplacement(crate.getFileName())); //todo() update

                        MiscUtils.sendCommand(command);
                    });

                    return;
                }

                if (MiscUtils.isLogging()) this.logger.warn("The property {} is empty, so no commands were run.", ConfigKeys.menu_button_command_list.getPath());

                return;
            }

            crate.playSound(player, player.getLocation(), "click-sound", "ui.button.click", Sound.Source.MASTER);

            new CrateMainMenu(player, this.config.getProperty(ConfigKeys.inventory_name), this.config.getProperty(ConfigKeys.inventory_rows)).open();
        }));
    }

    public final String parse(@NotNull final Player player, @NotNull final String title) {
        return Plugins.placeholder_api.isEnabled() ? PlaceholderAPI.setPlaceholders(player, title) : title;
    }

    public final Map<String, String> getPlaceholders() {
        final UUID uuid = this.player.getUniqueId();

        final NumberFormat instance = NumberFormat.getInstance();

        final Map<String, String> placeholders = new HashMap<>();

        for (final Crate crate : this.crateManager.getUsableCrates()) {
            final String fileName = crate.getFileName();
            final String lowerCase = fileName.toLowerCase();

            final int virtual = this.userManager.getVirtualKeys(uuid, fileName);
            final int physical = this.userManager.getPhysicalKeys(uuid, fileName);
            final int opened = this.userManager.getCrateOpened(uuid, fileName);
            final int total = virtual + physical;

            placeholders.put("{%s}".formatted(lowerCase), instance.format(virtual));
            placeholders.put("{%s_raw_physical}".formatted(lowerCase), String.valueOf(physical));
            placeholders.put("{%s_physical}".formatted(lowerCase), instance.format(physical));

            placeholders.put("{%s_raw_opened}".formatted(lowerCase), String.valueOf(opened));
            placeholders.put("{%s_raw_total}".formatted(lowerCase), String.valueOf(total));
            placeholders.put("{%s_raw}".formatted(lowerCase), String.valueOf(virtual));

            placeholders.put("{%s_opened}".formatted(lowerCase), instance.format(opened));
            placeholders.put("{%s_total}".formatted(lowerCase), instance.format(total));
        }

        placeholders.put("{player}", player.getName());

        return placeholders;
    }
}