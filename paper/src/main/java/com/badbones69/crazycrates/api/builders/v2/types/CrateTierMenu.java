package com.badbones69.crazycrates.api.builders.v2.types;

import com.badbones69.crazycrates.api.builders.types.CrateMainMenu;
import com.badbones69.crazycrates.api.builders.v2.StaticInventoryBuilder;
import com.badbones69.crazycrates.api.enums.misc.Keys;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Tier;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.badbones69.crazycrates.config.impl.ConfigKeys;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.Gui;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.GuiFiller;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.GuiItem;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import java.util.List;
import static java.util.regex.Matcher.quoteReplacement;

public class CrateTierMenu extends StaticInventoryBuilder {

    public CrateTierMenu(final Player player, final Crate crate) {
        super(player, crate);
    }

    private final Player player = getPlayer();
    private final Crate crate = getCrate();
    private final Gui gui = getGui();

    @Override
    public void open() {
        if (this.crate == null) return;

        final CrateType crateType = this.crate.getCrateType();

        if (crateType != CrateType.casino && crateType != CrateType.cosmic) return;

        final boolean isPreviewBorderEnabled = this.crate.isPreviewTierBorderToggle();

        if (isPreviewBorderEnabled) {
            final ItemStack itemStack = this.crate.getPreviewTierBorderItem().setPlayer(this.player).getStack();

            final GuiFiller guiFiller = this.gui.getFiller();

            guiFiller.fillTop(new GuiItem(itemStack));
            guiFiller.fillBottom(new GuiItem(itemStack));
        }

        final List<Tier> tiers = this.crate.getTiers();

        tiers.forEach(tier -> {
            final ItemStack item = tier.getTierItem(this.player);
            final int slot = tier.getSlot();

            this.gui.setItem(slot, new GuiItem(item, action -> {
                final ItemStack itemStack = action.getCurrentItem();

                if (itemStack == null || itemStack.getType().isAir()) return;

                final PersistentDataContainerView tags = itemStack.getPersistentDataContainer();

                if (tags.has(Keys.crate_tier.getNamespacedKey())) {
                    this.crate.playSound(this.player, this.player.getLocation(), "click-sound", "ui.button.click", Sound.Source.PLAYER);

                    //todo() obviously got to replace this
                    final Inventory cratePreviewMenu = this.crate.getPreview(this.player, this.inventoryManager.getPage(this.player), tier);

                    this.player.openInventory(cratePreviewMenu);
                }
            }));
        });

        if (this.config.getProperty(ConfigKeys.enable_crate_menu)) {
            this.gui.setItem(5, 5, new GuiItem(this.inventoryManager.getMenuButton(this.player), action -> {
                if (this.config.getProperty(ConfigKeys.menu_button_override)) {
                    final List<String> commands = this.config.getProperty(ConfigKeys.menu_button_command_list);

                    if (!commands.isEmpty()) {
                        commands.forEach(value -> {
                            final String command = value.replaceAll("%player%", quoteReplacement(this.player.getName())).replaceAll("%crate%", quoteReplacement(this.crate.getFileName()));

                            MiscUtils.sendCommand(command);
                        });

                        return;
                    }

                    if (MiscUtils.isLogging()) this.plugin.getComponentLogger().warn("The property {} is empty, so no commands were run.", ConfigKeys.menu_button_command_list.getPath());

                    return;
                }

                this.crate.playSound(this.player, this.player.getLocation(), "click-sound", "ui.button.click", Sound.Source.PLAYER);

                final CrateMainMenu crateMainMenu = new CrateMainMenu(this.player, this.config.getProperty(ConfigKeys.inventory_name), this.config.getProperty(ConfigKeys.inventory_size));
                this.player.openInventory(crateMainMenu.build().getInventory());
            }));
        }

        this.gui.open(this.player);
    }
}