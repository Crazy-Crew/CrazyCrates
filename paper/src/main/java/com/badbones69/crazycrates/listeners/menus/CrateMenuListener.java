package com.badbones69.crazycrates.listeners.menus;

import ch.jalu.configme.SettingsManager;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.common.config.types.ConfigKeys;
import com.badbones69.crazycrates.managers.crates.CrateManager;
import com.badbones69.crazycrates.api.objects.Crate;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.CrazyHandler;
import com.badbones69.crazycrates.api.builders.types.CrateMainMenu;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.managers.InventoryManager;
import com.badbones69.crazycrates.api.modules.ModuleHandler;
import com.badbones69.crazycrates.other.MiscUtils;

public class CrateMenuListener extends ModuleHandler {

    @NotNull
    private final InventoryManager inventoryManager = this.plugin.getCrazyHandler().getInventoryManager();

    @NotNull
    private final SettingsManager config = this.plugin.getConfigManager().getConfig();

    @NotNull
    private final CrazyHandler crazyHandler = this.plugin.getCrazyHandler();

    @NotNull
    private final CrateManager crateManager = this.plugin.getCrateManager();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        Inventory inventory = event.getInventory();

        if (!(inventory.getHolder() instanceof CrateMainMenu)) {
            return;
        }

        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();

        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        if (!item.hasItemMeta() && !item.getItemMeta().hasDisplayName()) {
            return;
        }

        NBTItem nbtItem = new NBTItem(item);

        if (!nbtItem.hasNBTData() && !nbtItem.hasTag("CrazyCrates-Crate")) {
            return;
        }

        Crate crate = this.plugin.getCrateManager().getCrateFromName(nbtItem.getString("CrazyCrates-Crate"));

        if (crate == null) {
            return;
        }

        if (event.getAction() == InventoryAction.PICKUP_HALF) { // Right-clicked the item
            if (crate.isPreviewEnabled()) {
                player.closeInventory();
                this.inventoryManager.addViewer(player);
                this.inventoryManager.openNewCratePreview(player, crate);
            } else {
                player.sendMessage(Messages.preview_disabled.getString());
            }

            return;
        }

        if (this.crateManager.isInOpeningList(player)) {
            player.sendMessage(Messages.already_opening_crate.getString());
            return;
        }

        boolean hasKey = false;
        KeyType keyType = KeyType.virtual_key;

        if (this.plugin.getCrazyHandler().getUserManager().getVirtualKeys(player.getUniqueId(), crate.getName()) >= 1) {
            hasKey = true;
        } else {
            if (this.config.getProperty(ConfigKeys.virtual_accepts_physical_keys) && this.crazyHandler.getUserManager().hasPhysicalKey(player.getUniqueId(), crate.getName(), false)) {
                hasKey = true;
                keyType = KeyType.physical_key;
            }
        }

        if (!hasKey) {
            if (this.config.getProperty(ConfigKeys.need_key_sound_toggle)) {
                player.playSound(player.getLocation(), Sound.valueOf(this.config.getProperty(ConfigKeys.need_key_sound)), SoundCategory.PLAYERS, 1f, 1f);
            }

            player.sendMessage(Messages.no_virtual_key.getString());
            return;
        }

        for (String world : this.config.getProperty(ConfigKeys.disabledWorlds)) {
            if (world.equalsIgnoreCase(player.getWorld().getName())) {
                player.sendMessage(Messages.world_disabled.getMessage("%world%", player.getWorld().getName()).toString());
                return;
            }
        }

        if (MiscUtils.isInventoryFull(player)) {
            player.sendMessage(Messages.inventory_not_empty.getString());
            return;
        }

        this.crateManager.openCrate(player, crate, keyType, player.getLocation(), true, false);
    }

    @Override
    public String getModuleName() {
        return "Crate Menu Listener";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void reload() {}
}