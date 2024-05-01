package com.badbones69.crazycrates.api.builders.types;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.enums.PersistentKeys;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Tier;
import com.badbones69.crazycrates.tasks.InventoryManager;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.platform.config.ConfigManager;
import us.crazycrew.crazycrates.platform.config.impl.ConfigKeys;
import com.badbones69.crazycrates.api.builders.InventoryBuilder;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CratePreviewMenu extends InventoryBuilder {

    @NotNull
    private final InventoryManager inventoryManager = this.plugin.getInventoryManager();

    private final boolean isTier;
    private final Tier tier;

    public CratePreviewMenu(Crate crate, Player player, int size, int page, String title, boolean isTier, Tier tier) {
        super(crate, player, size, page, title);

        this.isTier = isTier;
        this.tier = tier;
    }

    @Override
    public InventoryBuilder build() {
        Inventory inventory = getInventory();

        setDefaultItems(inventory);

        for (ItemStack item : getPageItems(getPage())) {
            int nextSlot = inventory.firstEmpty();

            if (nextSlot >= 0) {
                inventory.setItem(nextSlot, item);
            } else {
                break;
            }
        }

        return this;
    }

    private void setDefaultItems(Inventory inventory) {
        Player player = getPlayer();
        Crate crate = getCrate();

        if (crate.isBorderToggle()) {
            List<Integer> borderItems = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8);

            for (int i : borderItems) { // Top Border slots
                inventory.setItem(i, crate.getBorderItem().setTarget(player).build());
            }

            borderItems.replaceAll(crate::getAbsoluteItemPosition);

            for (int i : borderItems) { // Bottom Border slots
                inventory.setItem(i, crate.getBorderItem().setTarget(player).build());
            }
        }

        int page = this.inventoryManager.getPage(player);

        if (this.inventoryManager.inCratePreview(player) && ConfigManager.getConfig().getProperty(ConfigKeys.enable_crate_menu)) {
            inventory.setItem(getCrate().getAbsoluteItemPosition(4), this.inventoryManager.getMenuButton(player));
        }

        if (page == 1) {
            if (crate.isBorderToggle()) {
                inventory.setItem(crate.getAbsoluteItemPosition(3), getCrate().getBorderItem().setTarget(player).build());
            }
        } else {
            inventory.setItem(crate.getAbsoluteItemPosition(3), this.inventoryManager.getBackButton(player));
        }

        if (page == crate.getMaxPage()) {
            if (crate.isBorderToggle()) {
                inventory.setItem(crate.getAbsoluteItemPosition(5), crate.getBorderItem().setTarget(player).build());
            }
        } else {
            inventory.setItem(crate.getAbsoluteItemPosition(5), this.inventoryManager.getNextButton(player));
        }
    }

    private List<ItemStack> getPageItems(int page) {
        Player player = getPlayer();
        Crate crate = getCrate();

        List<ItemStack> list = !this.isTier ? crate.getPreviewItems(player) : crate.getPreviewItems(this.tier, player);
        List<ItemStack> items = new ArrayList<>();

        if (page <= 0) page = 1;

        int max = crate.getMaxSlots() - (crate.isBorderToggle() ? 18 : crate.getMaxSlots() >= list.size() ? 0 : crate.getMaxSlots() != 9 ? 9 : 0);
        int index = page * max - max;
        int endIndex = index >= list.size() ? list.size() - 1 : index + max;

        for (; index < endIndex; index++) {
            if (index < list.size()) items.add(list.get(index));
        }

        for (; items.isEmpty(); page--) {
            if (page <= 0) break;
            index = page * max - max;
            endIndex = index >= list.size() ? list.size() - 1 : index + max;

            for (; index < endIndex; index++) {
                if (index < list.size()) items.add(list.get(index));
            }
        }

        return items;
    }

    public static class CratePreviewListener implements Listener {

        private final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

        private final @NotNull InventoryManager inventoryManager = this.plugin.getInventoryManager();

        private final @NotNull SettingsManager config = ConfigManager.getConfig();

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            Inventory inventory = event.getInventory();

            if (!(inventory.getHolder(false) instanceof CratePreviewMenu holder)) return;

            event.setCancelled(true);

            Player player = holder.getPlayer();

            ItemStack item = event.getCurrentItem();

            if (item == null || item.getType() == Material.AIR) return;

            if (!item.hasItemMeta()) return;

            Crate crate = this.inventoryManager.getCratePreview(player);

            if (crate == null) return;

            ItemMeta itemMeta = item.getItemMeta();

            PersistentDataContainer container = itemMeta.getPersistentDataContainer();

            if (container.has(PersistentKeys.main_menu_button.getNamespacedKey()) && this.config.getProperty(ConfigKeys.enable_crate_menu)) { // Clicked the menu button.
                if (this.inventoryManager.inCratePreview(player)) {
                    if (holder.overrideMenu()) return;

                    crate.playSound(player, player.getLocation(), "click-sound","ui.button.click", SoundCategory.PLAYERS);

                    if (crate.isPreviewTierToggle() && crate.getCrateType() == CrateType.casino || crate.getCrateType() == CrateType.cosmic) {
                        player.openInventory(crate.getTierPreview(player));

                        return;
                    }

                    this.inventoryManager.removeViewer(player);
                    this.inventoryManager.closeCratePreview(player);

                    CrateMainMenu crateMainMenu = new CrateMainMenu(player, this.config.getProperty(ConfigKeys.inventory_size), this.config.getProperty(ConfigKeys.inventory_name));

                    player.openInventory(crateMainMenu.build().getInventory());
                }

                return;
            }

            if (container.has(PersistentKeys.next_button.getNamespacedKey())) {  // Clicked the next button.
                if (this.inventoryManager.getPage(player) < crate.getMaxPage()) {
                    crate.playSound(player, player.getLocation(), "click-sound","ui.button.click", SoundCategory.PLAYERS);

                    this.inventoryManager.nextPage(player);

                    this.inventoryManager.openCratePreview(player, crate);
                }

                return;
            }

            if (container.has(PersistentKeys.back_button.getNamespacedKey())) {  // Clicked the back button.
                if (this.inventoryManager.getPage(player) > 1 && this.inventoryManager.getPage(player) <= crate.getMaxPage()) {
                    crate.playSound(player, player.getLocation(), "click-sound","ui.button.click", SoundCategory.PLAYERS);

                    this.inventoryManager.backPage(player);

                    this.inventoryManager.openCratePreview(player, crate);
                }
            }
        }
    }
}