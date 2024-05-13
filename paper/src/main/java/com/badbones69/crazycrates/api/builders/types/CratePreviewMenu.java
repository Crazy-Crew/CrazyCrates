package com.badbones69.crazycrates.api.builders.types;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.api.enums.PersistentKeys;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Tier;
import com.badbones69.crazycrates.tasks.InventoryManager;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.badbones69.crazycrates.config.ConfigManager;
import com.badbones69.crazycrates.config.impl.ConfigKeys;
import com.badbones69.crazycrates.api.builders.InventoryBuilder;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CratePreviewMenu extends InventoryBuilder {

    private @NotNull final InventoryManager inventoryManager = this.plugin.getInventoryManager();

    private @NotNull final SettingsManager config = ConfigManager.getConfig();

    private boolean isTier;
    private Tier tier;

    public CratePreviewMenu(@NotNull final Player player, @NotNull final String title, final int size, final int page, @NotNull final Crate crate, final boolean isTier, @Nullable final Tier tier) {
        super(player, title, size, page, crate);

        this.isTier = isTier;
        this.tier = tier;
    }

    public CratePreviewMenu() {}

    @Override
    public InventoryBuilder build() {
        final Inventory inventory = getInventory();

        setDefaultItems(inventory);

        for (ItemStack item : getPageItems(getPage())) {
            final int nextSlot = inventory.firstEmpty();

            if (nextSlot >= 0) {
                inventory.setItem(nextSlot, item);
            } else {
                break;
            }
        }

        return this;
    }

    private void setDefaultItems(@NotNull final Inventory inventory) {
        final Player player = getPlayer();
        final Crate crate = getCrate();

        if (crate.isBorderToggle()) {
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

        final int page = this.inventoryManager.getPage(player);

        if (this.inventoryManager.inCratePreview(player) && ConfigManager.getConfig().getProperty(ConfigKeys.enable_crate_menu)) {
            inventory.setItem(crate.getAbsoluteItemPosition(4), this.inventoryManager.getMenuButton(player));
        }

        if (page == 1) {
            if (crate.isBorderToggle()) {
                inventory.setItem(crate.getAbsoluteItemPosition(3), crate.getBorderItem().setPlayer(player).getStack());
            }
        } else {
            inventory.setItem(crate.getAbsoluteItemPosition(3), this.inventoryManager.getBackButton(player));
        }

        if (page == crate.getMaxPage()) {
            if (crate.isBorderToggle()) {
                inventory.setItem(crate.getAbsoluteItemPosition(5), crate.getBorderItem().setPlayer(player).getStack());
            }
        } else {
            inventory.setItem(crate.getAbsoluteItemPosition(5), this.inventoryManager.getNextButton(player));
        }
    }

    @Override
    public void run(InventoryClickEvent event) {
        final Inventory inventory = event.getInventory();

        if (!(inventory.getHolder(false) instanceof CratePreviewMenu holder)) return;

        event.setCancelled(true);

        final Player player = holder.getPlayer();

        final ItemStack item = event.getCurrentItem();

        if (item == null || item.getType() == Material.AIR) return;

        if (!item.hasItemMeta()) return;

        final Crate crate = this.inventoryManager.getCratePreview(player);

        if (crate == null) return;

        final ItemMeta itemMeta = item.getItemMeta();

        final PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        if (container.has(PersistentKeys.main_menu_button.getNamespacedKey()) && this.config.getProperty(ConfigKeys.enable_crate_menu)) { // Clicked the menu button.
            if (this.inventoryManager.inCratePreview(player)) {
                if (holder.overrideMenu()) return;

                crate.playSound(player, player.getLocation(), "click-sound","ui.button.click", Sound.Source.PLAYER);

                if (crate.isPreviewTierToggle() && crate.getCrateType() == CrateType.casino || crate.getCrateType() == CrateType.cosmic) {
                    player.openInventory(crate.getTierPreview(player));

                    return;
                }

                this.inventoryManager.removeViewer(player);
                this.inventoryManager.closeCratePreview(player);

                final CrateMainMenu crateMainMenu = new CrateMainMenu(player, this.config.getProperty(ConfigKeys.inventory_name), this.config.getProperty(ConfigKeys.inventory_size));

                player.openInventory(crateMainMenu.build().getInventory());
            }

            return;
        }

        if (container.has(PersistentKeys.next_button.getNamespacedKey())) {  // Clicked the next button.
            if (this.inventoryManager.getPage(player) < crate.getMaxPage()) {
                crate.playSound(player, player.getLocation(), "click-sound","ui.button.click", Sound.Source.PLAYER);

                this.inventoryManager.nextPage(player);

                this.inventoryManager.openCratePreview(player, crate);
            }

            return;
        }

        if (container.has(PersistentKeys.back_button.getNamespacedKey())) {  // Clicked the back button.
            if (this.inventoryManager.getPage(player) > 1 && this.inventoryManager.getPage(player) <= crate.getMaxPage()) {
                crate.playSound(player, player.getLocation(), "click-sound","ui.button.click", Sound.Source.PLAYER);

                this.inventoryManager.backPage(player);

                this.inventoryManager.openCratePreview(player, crate);
            }
        }
    }

    private @NotNull List<ItemStack> getPageItems(int page) {
        final Player player = getPlayer();
        final Crate crate = getCrate();

        final List<ItemStack> list = !this.isTier ? crate.getPreviewItems(player) : crate.getPreviewItems(this.tier, player);
        final List<ItemStack> items = new ArrayList<>();

        if (page <= 0) page = 1;

        final int max = crate.getMaxSlots() - (crate.isBorderToggle() ? 18 : crate.getMaxSlots() >= list.size() ? 0 : crate.getMaxSlots() != 9 ? 9 : 0);
        int index = page * max - max;
        int endIndex = index >= list.size() ? list.size() - 1 : index + max;

        for (;index < endIndex; index++) {
            if (index < list.size()) {
                items.add(list.get(index));
            }
        }

        for (;items.isEmpty(); page--) {
            if (page <= 0) break;
            index = page * max - max;
            endIndex = index >= list.size() ? list.size() - 1 : index + max;

            for (;index < endIndex; index++) {
                if (index < list.size()) {
                    items.add(list.get(index));
                }
            }
        }

        return items;
    }
}