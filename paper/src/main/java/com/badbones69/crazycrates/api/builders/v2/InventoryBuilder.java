package com.badbones69.crazycrates.api.builders.v2;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.builders.v2.types.CrateMainMenu;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.badbones69.crazycrates.config.ConfigManager;
import com.badbones69.crazycrates.config.impl.ConfigKeys;
import com.badbones69.crazycrates.tasks.InventoryManager;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.GuiItem;
import com.ryderbelserion.vital.paper.api.builders.gui.types.BaseGui;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import us.crazycrew.crazycrates.api.users.UserManager;

import java.util.List;

import static java.util.regex.Matcher.quoteReplacement;

public abstract class InventoryBuilder {

    protected final CrazyCrates plugin = CrazyCrates.getPlugin();

    protected final CrateManager crateManager = this.plugin.getCrateManager();

    protected final UserManager userManager = this.plugin.getUserManager();

    protected final InventoryManager inventoryManager = this.plugin.getInventoryManager();

    protected final SettingsManager config = ConfigManager.getConfig();

    public void addMenuButton(final Player player, final Crate crate, final BaseGui gui, final int row, final int column) {
        if (this.config.getProperty(ConfigKeys.enable_crate_menu)) {
            gui.setItem(row, column, new GuiItem(this.inventoryManager.getMenuButton(player), action -> {
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

                crate.playSound(player, player.getLocation(), "click-sound", "ui.button.click", Sound.Source.PLAYER);

                new CrateMainMenu(player, this.config.getProperty(ConfigKeys.inventory_name), this.config.getProperty(ConfigKeys.inventory_rows)).open();
            }));
        }
    }

}