package com.badbones69.crazycrates.paper.api.builders.gui;

import ch.jalu.configme.SettingsManager;
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
import com.ryderbelserion.fusion.paper.api.builder.gui.objects.GuiItem;
import com.ryderbelserion.fusion.paper.api.builder.gui.types.BaseGui;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.text.NumberFormat;
import java.util.List;
import java.util.UUID;
import static java.util.regex.Matcher.quoteReplacement;

public abstract class InventoryBuilder {

    protected final Player player;

    public InventoryBuilder(final Player player) {
        this.player = player;
    }

    protected final CrazyCrates plugin = CrazyCrates.getPlugin();

    protected final CrateManager crateManager = this.plugin.getCrateManager();

    protected final BukkitUserManager userManager = this.plugin.getUserManager();

    protected final InventoryManager inventoryManager = this.plugin.getInventoryManager();

    protected final SettingsManager config = ConfigManager.getConfig();

    public void addMenuButton(final Player player, final Crate crate, final BaseGui gui, final int row, final int column) {
        if (this.config.getProperty(ConfigKeys.enable_crate_menu)) {
            gui.setItem(gui.getSlotFromRowColumn(row, column), new GuiItem(this.inventoryManager.getMenuButton(player), action -> {
                if (this.config.getProperty(ConfigKeys.menu_button_override)) {
                    final List<String> commands = this.config.getProperty(ConfigKeys.menu_button_command_list);

                    if (!commands.isEmpty()) {
                        commands.forEach(value -> {
                            final String command = value.replaceAll("%player%", quoteReplacement(player.getName())).replaceAll("%crate%", quoteReplacement(crate.getFileName()));

                            MiscUtils.sendCommand(command);
                        });

                        return;
                    }

                    if (MiscUtils.isLogging()) this.plugin.getComponentLogger().warn("The property {} is empty, so no commands were run.", ConfigKeys.menu_button_command_list.getPath());

                    return;
                }

                crate.playSound(player, player.getLocation(), "click-sound", "ui.button.click", Sound.Source.MASTER);

                new CrateMainMenu(player, this.config.getProperty(ConfigKeys.inventory_name), this.config.getProperty(ConfigKeys.inventory_rows)).open();
            }));
        }
    }

    public final String parse(final Player player, final String title) {
        return Plugins.placeholder_api.isEnabled() ? PlaceholderAPI.setPlaceholders(player, title) : title;
    }

    public final String getCrates(@NotNull String option) {
        if (option.isEmpty()) return "";

        final UUID uuid = this.player.getUniqueId();

        final NumberFormat instance = NumberFormat.getInstance();

        for (Crate crate : this.crateManager.getUsableCrates()) {
            final String fileName = crate.getFileName();
            final String lowerCase = fileName.toLowerCase();

            final int virtual = this.userManager.getVirtualKeys(uuid, fileName);
            final int physical = this.userManager.getPhysicalKeys(uuid, fileName);

            final int total = virtual + physical;

            final int opened = this.userManager.getCrateOpened(uuid, fileName);

            option = option.replaceAll("%" + lowerCase + "%", instance.format(virtual))
                    .replaceAll("%" + lowerCase + "_physical%", instance.format(physical))
                    .replaceAll("%" + lowerCase + "_total%", instance.format(total))
                    .replaceAll("%" + lowerCase + "_opened%", instance.format(opened))
                    .replaceAll("%" + lowerCase + "_raw%", String.valueOf(virtual))
                    .replaceAll("%" + lowerCase + "_raw_physical%", String.valueOf(physical))
                    .replaceAll("%" + lowerCase + "_raw_total%", String.valueOf(total))
                    .replaceAll("%" + lowerCase + "_raw_opened%", String.valueOf(opened));
        }

        return option;
    }
}