package com.badbones69.crazycrates.paper.api.builders.gui;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.common.config.beans.inventories.ItemPlacement;
import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.CrazyCratesPaper;
import com.badbones69.crazycrates.paper.api.enums.other.Plugins;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.managers.BukkitUserManager;
import com.badbones69.crazycrates.paper.managers.InventoryManager;
import com.badbones69.crazycrates.common.config.ConfigManager;
import com.badbones69.crazycrates.common.config.impl.ConfigKeys;
import com.badbones69.crazycrates.paper.tasks.crates.CrateManager;
import com.badbones69.crazycrates.paper.tasks.menus.CrateMainMenu;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.builders.gui.GuiBuilder;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
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

    protected final CrazyCratesPaper platform = this.plugin.getPlatform();

    protected final FusionPaper fusion = this.platform.getFusion();

    protected final CrateManager crateManager = this.platform.getCrateManager();

    protected final BukkitUserManager userManager = this.platform.getUserManager();

    protected final InventoryManager inventoryManager = this.platform.getInventoryManager();

    protected final SettingsManager config = ConfigManager.getConfig();

    public void addMenuButton(@NotNull final Player player, @NotNull final Crate crate, @NotNull final GuiBuilder gui) {
        if (!this.config.getProperty(ConfigKeys.enable_crate_menu)) return;

        final ItemPlacement placement = this.config.getProperty(ConfigKeys.menu_button_placement);

        final int row = placement.getRow();
        final int column = placement.getColumn();

        final int rows = gui.getRows();

        final int safeRow = Math.min(row == -1 ? rows : row, rows);

        final ItemStack itemStack = this.inventoryManager.getMenuButton(player);

        if (itemStack.isEmpty()) {
            return;
        }

        gui.addSlotAction(safeRow, column, itemStack, _ -> {
            if (this.config.getProperty(ConfigKeys.menu_button_override)) {
                final List<String> commands = this.config.getProperty(ConfigKeys.menu_button_command_list);

                if (!commands.isEmpty()) {
                    commands.forEach(value -> {
                        final String command = value.replaceAll("%player%", quoteReplacement(player.getName())).replaceAll("%crate%", quoteReplacement(crate.getFileName()));

                        MiscUtils.sendCommand(command);
                    });

                    return;
                }

                this.fusion.log(Level.WARNING, "The property %s is empty, so no commands were run.", ConfigKeys.menu_button_command_list.getPath());

                return;
            }

            crate.playSound(player, player.getLocation(), "click-sound", "ui.button.click", Sound.Source.MASTER);

            new CrateMainMenu(player, this.config.getProperty(ConfigKeys.inventory_name), this.config.getProperty(ConfigKeys.inventory_rows)).open();
        });
    }

    public final String parse(@NotNull final Player player, @NotNull final String title) {
        return Plugins.placeholder_api.isEnabled() ? PlaceholderAPI.setPlaceholders(player, title) : title;
    }

    public final String getCrates(@NotNull final String option) {
        if (option.isEmpty()) return "";

        final UUID uuid = this.player.getUniqueId();

        String safe = option;

        for (final Crate crate : this.crateManager.getUsableCrates()) {
            final String fileName = crate.getFileName();
            final String crateName = fileName.toLowerCase();

            final int virtual = this.userManager.getVirtualKeys(uuid, fileName);
            final int physical = this.userManager.getPhysicalKeys(uuid, fileName);

            final int total = virtual + physical;

            final int opened = this.userManager.getCrateOpened(uuid, fileName);

            safe = this.fusion.replacePlaceholders(option, Map.of(
                    "%{}%".replace("{}", crateName), StringUtils.formatNumber(virtual),
                    "%{}_physical%".replace("{}", crateName), StringUtils.format(physical),
                    "%{}_total%", StringUtils.format(total),
                    "%{}_opened%", StringUtils.format(opened),
                    "%{}_raw%", String.valueOf(virtual),
                    "%{}_raw_physical", String.valueOf(physical),
                    "%{}_raw_total%", String.valueOf(total),
                    "%{}_raw_opened%", String.valueOf(opened)
            ));
        }

        return safe;
    }
}