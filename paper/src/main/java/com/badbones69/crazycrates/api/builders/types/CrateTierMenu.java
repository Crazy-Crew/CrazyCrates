package com.badbones69.crazycrates.api.builders.types;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.api.builders.InventoryBuilder;
import com.badbones69.crazycrates.api.enums.PersistentKeys;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Tier;
import com.badbones69.crazycrates.config.ConfigManager;
import com.badbones69.crazycrates.config.impl.ConfigKeys;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.List;

public class CrateTierMenu extends InventoryBuilder {

    private @NotNull final SettingsManager config = ConfigManager.getConfig();

    public CrateTierMenu(@NotNull final Player player, @NotNull final String title, final int size, @NotNull final Crate crate, @NotNull final List<Tier> tiers) {
        super(player, title, size, crate, tiers);
    }

    public CrateTierMenu() {}

    @Override
    public InventoryBuilder build() {
        setDefaultItems();

        return this;
    }

    @Override
    public void run(InventoryClickEvent event) {
        final Inventory inventory = event.getInventory();

        if (!(inventory.getHolder(false) instanceof CrateTierMenu holder)) return;

        event.setCancelled(true);

        final Player player = holder.getPlayer();

        final ItemStack item = event.getCurrentItem();

        if (item == null || item.getType() == Material.AIR) return;

        if (!item.hasItemMeta()) return;

        final Crate crate = this.inventoryManager.getCratePreview(player);

        if (crate == null) return;

        final ItemMeta itemMeta = item.getItemMeta();

        final PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        if (container.has(PersistentKeys.main_menu_button.getNamespacedKey()) && this.config.getProperty(ConfigKeys.enable_crate_menu)) {
            if (this.inventoryManager.inCratePreview(player)) {
                if (holder.overrideMenu()) return;

                crate.playSound(player, player.getLocation(), "click-sound", "ui.button.click", Sound.Source.PLAYER);

                this.inventoryManager.removeViewer(player);
                this.inventoryManager.closeCratePreview(player);

                final CrateMainMenu crateMainMenu = new CrateMainMenu(player, this.config.getProperty(ConfigKeys.inventory_name), this.config.getProperty(ConfigKeys.inventory_size));

                player.openInventory(crateMainMenu.build().getInventory());
            }

            return;
        }

        if (container.has(PersistentKeys.preview_tier_button.getNamespacedKey())) {
            crate.playSound(player, player.getLocation(), "click-sound", "ui.button.click", Sound.Source.PLAYER);

            final String tierName = container.get(PersistentKeys.preview_tier_button.getNamespacedKey(), PersistentDataType.STRING);

            final Tier tier = crate.getTier(tierName);

            final Inventory cratePreviewMenu = crate.getPreview(player, this.inventoryManager.getPage(player), true, tier);

            player.openInventory(cratePreviewMenu);
        }
    }

    private void setDefaultItems() {
        final Inventory inventory = getInventory();
        final Player player = getPlayer();
        final Crate crate = getCrate();

        getTiers().forEach(tier -> inventory.setItem(tier.getSlot(), tier.getTierItem(player)));

        if (crate.isPreviewTierBorderToggle()) {
            final List<Integer> borderItems = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8);

            final ItemStack itemStack = crate.getPreviewTierBorderItem().setPlayer(player).getStack();

            for (int item : borderItems) { // Top border slots
                inventory.setItem(item, itemStack);
            }

            borderItems.replaceAll(crate::getAbsolutePreviewItemPosition);

            for (int item : borderItems) { // Bottom border slots
                inventory.setItem(item, itemStack);
            }
        }

        if (this.inventoryManager.inCratePreview(player) && this.config.getProperty(ConfigKeys.enable_crate_menu)) inventory.setItem(crate.getAbsolutePreviewItemPosition(4), this.inventoryManager.getMenuButton(player));
    }
}