package com.badbones69.crazycrates.api.builders.types;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.api.builders.InventoryBuilder;
import com.badbones69.crazycrates.tasks.PaginationManager;
import com.badbones69.crazycrates.api.enums.PersistentKeys;
import com.badbones69.crazycrates.api.objects.Crate;
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

    private final PaginationManager paginationManager = this.plugin.getPaginationManager();

    private @NotNull final SettingsManager config = ConfigManager.getConfig();

    public CrateTierMenu(@NotNull final Player player, @NotNull final Crate crate) {
        super(player, crate.getPreviewName(), crate.getPreviewTierMaxSlots(), crate, crate.getTiers());
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

        final Crate crate = holder.getCrate();

        final ItemMeta itemMeta = item.getItemMeta();

        final PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        if (this.config.getProperty(ConfigKeys.enable_crate_menu) && container.has(PersistentKeys.main_menu_button.getNamespacedKey())) {
            if (holder.overrideMenu()) return;

            crate.playSound(player, player.getLocation(), "click-sound","ui.button.click", Sound.Source.PLAYER);

            this.paginationManager.buildMainMenu(player, this.config);

            return;
        }

        if (container.has(PersistentKeys.crate_tier.getNamespacedKey())) {
            crate.playSound(player, player.getLocation(), "click-sound", "ui.button.click", Sound.Source.PLAYER);

            this.paginationManager.buildInventory(player, crate, 0, crate.getTier(container.get(PersistentKeys.crate_tier.getNamespacedKey(), PersistentDataType.STRING)));
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

        if (this.config.getProperty(ConfigKeys.enable_crate_menu)) inventory.setItem(crate.getAbsolutePreviewItemPosition(4), this.paginationManager.getMenuButton(player));
    }
}