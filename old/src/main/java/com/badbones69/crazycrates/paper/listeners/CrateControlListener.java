package com.badbones69.crazycrates.paper.listeners;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.paper.CrazyCratesOld;
import com.badbones69.crazycrates.paper.Methods;
import com.badbones69.crazycrates.paper.api.CrazyManager;
import com.badbones69.crazycrates.paper.api.events.PhysicalCrateKeyCheckEvent;
import com.badbones69.crazycrates.paper.api.managers.MenuManager;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.CrateLocation;
import us.crazycrew.crazycrates.paper.api.plugin.users.BukkitUserManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import us.crazycrew.crazycrates.common.api.enums.Permissions;
import us.crazycrew.crazycrates.common.config.ConfigManager;
import us.crazycrew.crazycrates.common.config.types.Config;
import us.crazycrew.crazycrates.common.config.types.menus.MainMenuConfig;
import us.crazycrew.crazycrates.paper.api.plugin.CrazyHandler;
import java.util.HashMap;
import java.util.UUID;

public class CrateControlListener implements Listener { // Crate Control

    private final @NotNull CrazyCratesOld plugin = JavaPlugin.getPlugin(CrazyCratesOld.class);
    private final @NotNull CrazyHandler crazyHandler = this.plugin.getCrazyHandler();
    private final @NotNull BukkitUserManager userManager = this.crazyHandler.getUserManager();
    private final @NotNull Methods methods = this.crazyHandler.getMethods();
    private final @NotNull CrazyManager crazyManager = this.crazyHandler.getCrazyManager();
    private final @NotNull MenuManager menuManager = this.crazyHandler.getMenuManager();

    private final @NotNull ConfigManager configManager = this.crazyHandler.getConfigManager();
    private final @NotNull SettingsManager config = this.configManager.getConfig();
    private final @NotNull SettingsManager menuConfig = this.configManager.getMainMenuConfig();
    
    // A list of crate locations that are in use.
    private final HashMap<UUID, Location> inUse = new HashMap<>();
    
    // This event controls when a player tries to click in a GUI based crate type. This will stop them from taking items out of their inventories.
    @EventHandler
    public void onCrateInventoryClick(InventoryClickEvent event) {
        for (Crate crate : this.crazyManager.getCrates()) {
            if (crate.getCrateType() != CrateType.MENU && crate.isCrateMenu(event.getView())) event.setCancelled(true);
        }
    }
    
    // This must run as highest, so it doesn't cause other plugins to check
    // the items that were added to the players inventory and replaced the item in the player's hand.
    // This is only an issue with QuickCrate
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCrateOpen(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            if (this.crazyManager.isKey(player.getInventory().getItemInOffHand())) {
                event.setCancelled(true);
                player.updateInventory();
            }

            return;
        }

        Block clickedBlock = event.getClickedBlock();

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            // Loops through all loaded physical locations.
            for (CrateLocation loc : this.crazyManager.getCrateLocations()) {
                // Checks to see if the clicked block is the same as a physical crate.
                if (loc.getLocation().equals(clickedBlock.getLocation())) {
                    // Checks to see if the player is removing a crate location.
                    if (player.getGameMode() == GameMode.CREATIVE && player.isSneaking() && player.hasPermission("crazycrates.admin")) {
                        event.setCancelled(true);
                        this.crazyManager.removeCrateLocation(loc.getID());
                        //TODO() Update message enum.
                        //player.sendMessage(Messages.REMOVED_PHYSICAL_CRATE.getMessage("{id}", loc.getID()));
                        return;
                    }

                    event.setCancelled(true);

                    if (loc.getCrateType() != CrateType.MENU) {
                        if (loc.getCrate().isPreviewEnabled()) {
                            this.menuManager.setPlayerInMenu(player, false);
                            this.menuManager.openNewPreview(player, loc.getCrate());
                        } else {
                            //TODO() Update message enum.
                            //player.sendMessage(Messages.PREVIEW_DISABLED.getMessage());
                        }
                    }
                }
            }
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // Checks if the item in their hand is a key and if so it stops them from right-clicking with it.
            ItemStack key = player.getInventory().getItemInMainHand();
            boolean keyInHand = this.crazyManager.isKey(key);

            if (!keyInHand) keyInHand = this.crazyManager.isKey(player.getEquipment().getItemInOffHand());

            if (keyInHand) {
                event.setCancelled(true);
                player.updateInventory();
            }

            //Checks to see if the clicked block is a physical crate.
            CrateLocation crateLocation = this.crazyManager.getCrateLocation(clickedBlock.getLocation());

            if (crateLocation != null && crateLocation.getCrate() != null) {
                Crate crate = crateLocation.getCrate();
                event.setCancelled(true);

                if (crate.getCrateType() == CrateType.MENU) {
                    boolean openMenu = this.menuConfig.getProperty(MainMenuConfig.crate_menu_toggle);

                    if (!this.crazyManager.isInOpeningList(player) && openMenu) this.menuManager.openMainMenu(player);

                    return;
                }

                PhysicalCrateKeyCheckEvent physicalCrateKeyCheckEvent = new PhysicalCrateKeyCheckEvent(player.getUniqueId(), crateLocation);
                this.plugin.getServer().getPluginManager().callEvent(physicalCrateKeyCheckEvent);

                if (physicalCrateKeyCheckEvent.isCancelled()) {
                    //TODO() Log if it's cancelled.
                    return;
                }

                boolean hasKey = false;
                boolean isPhysical = false;
                boolean useQuickCrateAgain = false;
                String keyName = crate.getKey().getItemMeta().getDisplayName();

                int requiredKeys = this.crazyManager.getCrateFromName(crate.getName()).getRequiredKeys();

                int totalKeys = this.userManager.getTotalKeys(player.getUniqueId(), crate.getName());

                if (requiredKeys > 0 && totalKeys < requiredKeys) {
                    //TODO() Update message enum.
                    //player.sendMessage(Messages.REQUIRED_KEYS.getMessage().replaceAll("\\{key-amount}", String.valueOf(requiredKeys)).replaceAll("\\{crate}", crate.getPreviewName()).replaceAll("\\{amount}", String.valueOf(totalKeys)));
                    return;
                }

                if (crate.getCrateType() != CrateType.CRATE_ON_THE_GO && keyInHand && this.crazyManager.isKeyFromCrate(key, crate) && this.config.getProperty(Config.physical_accepts_physical)) {
                    hasKey = true;
                    isPhysical = true;
                }

                if (this.config.getProperty(Config.physical_accepts_virtual) && this.userManager.getVirtualKeys(player.getUniqueId(), crate.getName()) >= 1)
                    hasKey = true;

                if (hasKey) {
                    // Checks if the player uses the quick crate again.
                    if (this.crazyManager.isInOpeningList(player) && this.crazyManager.getOpeningCrate(player).getCrateType() == CrateType.QUICK_CRATE && this.inUse.containsKey(player.getUniqueId()) && this.inUse.get(player.getUniqueId()).equals(crateLocation.getLocation())) {
                        useQuickCrateAgain = true;
                    }

                    if (!useQuickCrateAgain) {
                        if (this.crazyManager.isInOpeningList(player)) {
                            //TODO() Update message enum.
                            //player.sendMessage(Messages.ALREADY_OPENING_CRATE.getMessage("{key}", keyName));
                            return;
                        }

                        if (this.inUse.containsValue(crateLocation.getLocation())) {
                            //TODO() Update message enum.
                            //player.sendMessage(Messages.QUICK_CRATE_IN_USE.getMessage());
                            return;
                        }
                    }

                    if (this.methods.isInventoryFull(player)) {
                        //TODO() Update message enum.
                        //player.sendMessage(Messages.INVENTORY_FULL.getMessage());
                        return;
                    }

                    //TODO() Update static method.
                    //if (useQuickCrateAgain) QuickCrate.endQuickCrate(player, crateLocation.getLocation(), crate, this.crazyManager.getHologramController(), true);

                    KeyType keyType = isPhysical ? KeyType.PHYSICAL_KEY : KeyType.VIRTUAL_KEY;

                    // Only cosmic crate type uses this method.
                    if (crate.getCrateType() == CrateType.COSMIC) this.crazyManager.addPlayerKeyType(player, keyType);

                    this.crazyManager.addPlayerToOpeningList(player, crate);
                    this.crazyManager.openCrate(player, crate, keyType, crateLocation.getLocation(), false, true);
                } else {
                    if (crate.getCrateType() != CrateType.CRATE_ON_THE_GO) {
                        if (this.config.getProperty(Config.crate_knock_back))
                            knockBack(player, clickedBlock.getLocation());

                        if (this.config.getProperty(Config.key_sound_toggle)) {
                            Sound sound = Sound.valueOf(this.config.getProperty(Config.key_sound_name));

                            player.playSound(player.getLocation(), sound, SoundCategory.PLAYERS, 1f, 1f);
                        }

                        //TODO() Update message enum.
                        //player.sendMessage(Messages.NO_KEY.getMessage("{key}", keyName));
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onAdminMenuClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!event.getView().getTitle().equals(this.methods.sanitizeColor("&4&lAdmin Keys"))) return;

        event.setCancelled(true);

        if (!this.methods.permCheck(player, Permissions.crazy_crates_admin_access, false)) {
            player.closeInventory();
            return;
        }

        if (event.getCurrentItem() == null) return;

        ItemStack item = event.getCurrentItem();

        if (!this.crazyManager.isKey(item)) return;

        Crate crate = this.crazyManager.getCrateFromKey(item);

        if (event.getAction() == InventoryAction.PICKUP_ALL) {
            player.getInventory().addItem(crate.getKey());
        } else if (event.getAction() == InventoryAction.PICKUP_HALF) {
            this.userManager.addKeys(1, player.getUniqueId(), crate.getName(), KeyType.VIRTUAL_KEY);
            String name = null;
            ItemStack key = crate.getKey();

            if (key.hasItemMeta() && key.getItemMeta().hasDisplayName()) name = key.getItemMeta().getDisplayName();

            //TODO() Add message for this.
            //player.sendMessage(this.methods.getPrefix() + LegacyUtils.color(("&a&l+1 " + (name != null ? name : crate.getName()))));
        }
    }
    
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (this.crazyManager.hasCrateTask(player)) this.crazyManager.endCrate(player);

        if (this.crazyManager.hasQuadCrateTask(player)) this.crazyManager.endQuadCrate(player);

        if (this.crazyManager.isInOpeningList(player)) this.crazyManager.removePlayerFromOpeningList(player);
    }
    
    private void knockBack(Player player, Location location) {
        Vector vector = player.getLocation().toVector().subtract(location.toVector()).normalize().multiply(1).setY(.1);

        if (player.isInsideVehicle() && player.getVehicle() != null) {
            player.getVehicle().setVelocity(vector);
            return;
        }

        player.setVelocity(vector);
    }
}