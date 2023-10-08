package com.badbones69.crazycrates.paper.listeners.gui;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.Methods;
import com.badbones69.crazycrates.paper.api.CrazyManager;
import com.badbones69.crazycrates.paper.api.crates.CrateMainMenu;
import com.badbones69.crazycrates.paper.api.users.BukkitUserManager;
import org.bukkit.SoundCategory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import us.crazycrew.crazycrates.common.config.ConfigManager;
import us.crazycrew.crazycrates.common.config.types.Config;
import us.crazycrew.crazycrates.paper.api.plugin.CrazyHandler;
import java.util.ArrayList;

public class CrateMenuListener implements Listener {

    private final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);
    private final @NotNull CrazyHandler crazyHandler = this.plugin.getCrazyHandler();
    private final @NotNull BukkitUserManager userManager = this.crazyHandler.getUserManager();
    private final @NotNull Methods methods = this.crazyHandler.getMethods();
    private final @NotNull CrazyManager crazyManager = this.crazyHandler.getCrazyManager();

    private final @NotNull ConfigManager configManager = this.crazyHandler.getConfigManager();
    private final @NotNull SettingsManager config = this.configManager.getConfig();

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();

        if (inventory == null) return;

        if (inventory.getHolder() instanceof CrateMainMenu) {
            ItemStack itemStack = event.getCurrentItem();
            Player player = (Player) event.getWhoClicked();

            event.setCancelled(true);

            this.crazyManager.getCrates().forEach(crate -> {
                if (crate.getCrateType() != CrateType.MENU && crate.isCrateMenu(event.getView())) return;
            });

            event.setCancelled(true);

            if (itemStack == null) return;

            NBTItem nbtItem = new NBTItem(itemStack);

            if (!nbtItem.hasNBTData() && !nbtItem.hasTag("CrazyCrates-Crate")) return;

            Crate crate = this.crazyManager.getCrateFromName(nbtItem.getString("CrazyCrates-Crate"));

            if (crate == null) return;

            if (event.getAction() == InventoryAction.PICKUP_HALF) {
                if (crate.isPreviewEnabled()) {
                    player.closeInventory();

                    this.crazyHandler.getMenuManager().setPlayerInMenu(player, true);
                    this.crazyHandler.getMenuManager().openNewPreview(player, crate);
                } else {
                    //TODO() Update message enum.
                    //player.sendMessage(Messages.PREVIEW_DISABLED.getMessage());
                }

                return;
            }

            if (this.crazyManager.isInOpeningList(player)) {
                //TODO() Update message enum.
                //player.sendMessage(Messages.CRATE_ALREADY_OPENED.getMessage());
                return;
            }

            boolean hasKey = false;
            KeyType keyType = KeyType.VIRTUAL_KEY;

            if (this.userManager.getVirtualKeys(player.getUniqueId(), crate.getName()) >= 1) {
                hasKey = true;
            } else {
                if (this.config.getProperty(Config.virtual_accepts_physical_keys) && this.crazyManager.hasPhysicalKey(player, crate, false)) {
                    hasKey = true;
                    keyType = KeyType.PHYSICAL_KEY;
                }
            }

            if (!hasKey) {
                if (this.config.getProperty(Config.key_sound_toggle)) {
                    Sound sound = Sound.valueOf(this.config.getProperty(Config.key_sound_name));

                    player.playSound(player.getLocation(), sound, SoundCategory.PLAYERS, 1f, 1f);
                }

                //TODO() Update message enum.
                //player.sendMessage(Messages.NO_VIRTUAL_KEY.getMessage());
                return;
            }

            for (String world : getDisabledWorlds()) {
                if (world.equalsIgnoreCase(player.getWorld().getName())) {
                    //TODO() Update message enum.
                    //player.sendMessage(Messages.WORLD_DISABLED.getMessage("{world}", player.getWorld().getName()));
                    return;
                }
            }

            if (this.methods.isInventoryFull(player)) {
                //TODO() Update message enum.
                //player.sendMessage(Messages.INVENTORY_FULL.getMessage());
                return;
            }

            this.crazyManager.openCrate(player, crate, keyType, player.getLocation(), true, false);
        }
    }

    private ArrayList<String> getDisabledWorlds() {
        return new ArrayList<>(this.config.getProperty(Config.disabled_worlds));
    }
}