package com.badbones69.crazycrates.paper.listeners;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.Methods;
import com.badbones69.crazycrates.paper.api.CrazyManager;
import com.badbones69.crazycrates.paper.api.FileManager.Files;
import com.badbones69.crazycrates.paper.api.enums.settings.Messages;
import com.badbones69.crazycrates.paper.api.events.PhysicalCrateKeyCheckEvent;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.CrateLocation;
import com.badbones69.crazycrates.paper.cratetypes.QuickCrate;
import com.ryderbelserion.cluster.bukkit.utils.LegacyUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import us.crazycrew.crazycrates.common.api.enums.Permissions;
import us.crazycrew.crazycrates.paper.api.plugin.CrazyHandler;
import java.util.HashMap;
import java.util.UUID;

public class CrateControlListener implements Listener { // Crate Control
    
    // A list of crate locations that are in use.
    private final HashMap<UUID, Location> inUse = new HashMap<>();

    @NotNull
    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);
    private final @NotNull CrazyHandler crazyHandler = this.plugin.getCrazyHandler();
    private final @NotNull Methods methods = this.crazyHandler.getMethods();

    private final @NotNull CrazyManager crazyManager = this.plugin.getStarter().getCrazyManager();
    
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
        FileConfiguration config = Files.CONFIG.getFile();

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
                        player.sendMessage(Messages.REMOVED_PHYSICAL_CRATE.getMessage("%ID%", loc.getID()));
                        return;
                    }

                    event.setCancelled(true);

                    if (loc.getCrateType() != CrateType.MENU) {
                        if (loc.getCrate().isPreviewEnabled()) {
                            //PreviewListener.setPlayerInMenu(player, false);
                            //PreviewListener.openNewPreview(player, loc.getCrate());
                        } else {
                            player.sendMessage(Messages.PREVIEW_DISABLED.getMessage());
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
                    boolean openMenu = config.getBoolean("Settings.Enable-Crate-Menu");

                    //This is to stop players in QuadCrate to not be able to try and open a crate set to menu.
                    //TODO() Update static method
                    //if (!this.crazyManager.isInOpeningList(player) && openMenu) MenuListener.openGUI(player);

                    return;
                }

                PhysicalCrateKeyCheckEvent physicalCrateKeyCheckEvent = new PhysicalCrateKeyCheckEvent(player, crateLocation);
                player.getServer().getPluginManager().callEvent(physicalCrateKeyCheckEvent);

                if (!physicalCrateKeyCheckEvent.isCancelled()) {
                    boolean hasKey = false;
                    boolean isPhysical = false;
                    boolean useQuickCrateAgain = false;
                    String keyName = crate.getKey().getItemMeta().getDisplayName();
                    keyName = keyName != null ? keyName : crate.getKey().getType().toString();

                    int requiredKeys = this.crazyManager.getCrateFromName(crate.getName()).getRequiredKeys();

                    int totalKeys = this.crazyManager.getTotalKeys(player, crate);

                    if (requiredKeys > 0 && totalKeys < requiredKeys) {
                        player.sendMessage(Messages.REQUIRED_KEYS.getMessage().replaceAll("%key-amount%", String.valueOf(requiredKeys)).replaceAll("%crate%", crate.getPreviewName()).replaceAll("%amount%", String.valueOf(totalKeys)));
                        return;
                    }

                    if (crate.getCrateType() != CrateType.CRATE_ON_THE_GO && keyInHand && this.crazyManager.isKeyFromCrate(key, crate) && config.getBoolean("Settings.Physical-Accepts-Physical-Keys")) {
                        hasKey = true;
                        isPhysical = true;
                    }

                    if (config.getBoolean("Settings.Physical-Accepts-Virtual-Keys") && this.crazyManager.getVirtualKeys(player, crate) >= 1) hasKey = true;

                    if (hasKey) {
                        // Checks if the player uses the quick crate again.
                        if (this.crazyManager.isInOpeningList(player) && this.crazyManager.getOpeningCrate(player).getCrateType() == CrateType.QUICK_CRATE && this.inUse.containsKey(player.getUniqueId()) && this.inUse.get(player.getUniqueId()).equals(crateLocation.getLocation())) {
                            useQuickCrateAgain = true;
                        }

                        if (!useQuickCrateAgain) {
                            if (this.crazyManager.isInOpeningList(player)) {
                                player.sendMessage(Messages.ALREADY_OPENING_CRATE.getMessage("%Key%", keyName));
                                return;
                            }

                            if (this.inUse.containsValue(crateLocation.getLocation())) {
                                player.sendMessage(Messages.QUICK_CRATE_IN_USE.getMessage());
                                return;
                            }
                        }

                        if (this.methods.isInventoryFull(player)) {
                            player.sendMessage(Messages.INVENTORY_FULL.getMessage());
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
                            if (config.getBoolean("Settings.KnockBack")) knockBack(player, clickedBlock.getLocation());

                            if (config.contains("Settings.Need-Key-Sound")) {
                                Sound sound = Sound.valueOf(config.getString("Settings.Need-Key-Sound"));

                                player.playSound(player.getLocation(), sound, SoundCategory.PLAYERS, 1f, 1f);
                            }

                            player.sendMessage(Messages.NO_KEY.getMessage("%Key%", keyName));
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onAdminMenuClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        Player player = (Player) event.getWhoClicked();

        if (!event.getView().getTitle().equals(this.methods.sanitizeColor("&4&lAdmin Keys"))) return;

        event.setCancelled(true);

        if (!this.methods.permCheck(player, Permissions.CRAZY_CRATES_ADMIN_ACCESS, false)) {
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
            this.crazyManager.addKeys(1, player, crate, KeyType.VIRTUAL_KEY);
            String name = null;
            ItemStack key = crate.getKey();

            if (key.hasItemMeta() && key.getItemMeta().hasDisplayName()) name = key.getItemMeta().getDisplayName();

            player.sendMessage(this.methods.getPrefix() + LegacyUtils.color(("&a&l+1 " + (name != null ? name : crate.getName()))));
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