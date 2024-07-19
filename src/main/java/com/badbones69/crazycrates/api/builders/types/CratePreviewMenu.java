package com.badbones69.crazycrates.api.builders.types;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.api.builders.InventoryBuilder;
import com.badbones69.crazycrates.api.enums.PersistentKeys;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Tier;
import com.badbones69.crazycrates.config.ConfigManager;
import com.badbones69.crazycrates.config.impl.ConfigKeys;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import java.util.Arrays;
import java.util.List;

public class CratePreviewMenu extends InventoryBuilder {

    private final SettingsManager config = ConfigManager.getConfig();

    public CratePreviewMenu(@NotNull final Player player, final Crate crate, final int size, final int page) {
        super(player, crate.getPreviewName(), size, page, crate);
    }

    private Tier tier = null;

    public CratePreviewMenu(@NotNull final Player player, final Tier tier, final Crate crate, final int size, final int page) {
        super(player, crate.getPreviewName(), size, page, crate);

        this.tier = tier;
    }

    public CratePreviewMenu() {}

    @Override
    public InventoryBuilder build() {
        final Inventory inventory = getInventory();
        final Player player = getPlayer();

        setDefaultItems(inventory);

        for (ItemStack item : this.manager.getPreviewItems(player, getCrate(), getPage(), this.tier != null, this.tier)) {
            final int nextSlot = inventory.firstEmpty();

            if (nextSlot >= 0) {
                inventory.setItem(nextSlot, item);
            } else {
                break;
            }
        }

        return this;
    }

    @Override
    public void run(InventoryClickEvent event) {
        final Inventory inventory = event.getInventory();

        if (!(inventory.getHolder(false) instanceof CratePreviewMenu holder)) return;

        final Player player = holder.getPlayer();

        event.setCancelled(true);

        final ItemStack itemStack = event.getCurrentItem();

        if (itemStack == null || !itemStack.hasItemMeta()) return;

        final ItemMeta itemMeta = itemStack.getItemMeta();

        final PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        final Crate crate = holder.getCrate();

        if (container.has(PersistentKeys.main_menu_button.getNamespacedKey()) && this.config.getProperty(ConfigKeys.enable_crate_menu)) { // Clicked the menu button.
            if (holder.overrideMenu()) return;

            crate.playSound(player, player.getLocation(), "click-sound","ui.button.click", Sound.Source.PLAYER);

            if (crate.isPreviewTierToggle() && crate.getCrateType() == CrateType.casino || crate.getCrateType() == CrateType.cosmic) {
                this.manager.buildTierMenu(player, crate);

                return;
            }

            this.manager.buildMainMenu(player, this.config);

            return;
        }

        if (container.has(PersistentKeys.back_button.getNamespacedKey())) {
            crate.playSound(player, player.getLocation(), "click-sound","ui.button.click", Sound.Source.PLAYER);

            this.manager.backPage(player, crate, container.getOrDefault(PersistentKeys.back_button.getNamespacedKey(), PersistentDataType.INTEGER, 1));

            return;
        }

        if (container.has(PersistentKeys.next_button.getNamespacedKey())) {
            crate.playSound(player, player.getLocation(), "click-sound","ui.button.click", Sound.Source.PLAYER);

            this.manager.nextPage(player, crate, container.getOrDefault(PersistentKeys.next_button.getNamespacedKey(), PersistentDataType.INTEGER, 1));
        }
    }

    @Override
    public void run(InventoryDragEvent event) {
        final Inventory inventory = event.getView().getTopInventory();

        if (!(inventory.getHolder(false) instanceof CratePreviewMenu)) return;

        event.setCancelled(true);
    }

    private void setDefaultItems(@NotNull final Inventory inventory) {
        final Player player = getPlayer();
        final Crate crate = getCrate();

        final boolean isBorderToggled = crate.isBorderToggle();

        if (isBorderToggled) {
            final List<Integer> borderItems = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8);

            final ItemStack itemStack = crate.getBorderItem().setPlayer(player).getStack();

            for (int i : borderItems) { // Top Border slots
                inventory.setItem(i, itemStack);
            }

            borderItems.replaceAll(crate::getAbsoluteItemPosition);

            for (int i : borderItems) { // Bottom Border slots
                inventory.setItem(i, itemStack);
            }
        }

        final int page = this.manager.getPage(player);

        if (this.config.getProperty(ConfigKeys.enable_crate_menu)) {
            inventory.setItem(crate.getAbsoluteItemPosition(4), this.manager.getMenuButton(player));
        }

        if (page == 1) {
            if (isBorderToggled) {
                inventory.setItem(crate.getAbsoluteItemPosition(3), crate.getBorderItem().setPlayer(player).getStack());
            }
        } else {
            inventory.setItem(crate.getAbsoluteItemPosition(3), this.manager.getBackButton(player));
        }

        if (page == this.manager.getMaxPages(crate)) {
            if (isBorderToggled) {
                inventory.setItem(crate.getAbsoluteItemPosition(5), crate.getBorderItem().setPlayer(player).getStack());
            }
        } else {
            inventory.setItem(crate.getAbsoluteItemPosition(5), this.manager.getNextButton(player));
        }
    }
}