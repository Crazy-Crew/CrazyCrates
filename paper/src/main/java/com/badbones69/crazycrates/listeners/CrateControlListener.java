package com.badbones69.crazycrates.listeners;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.enums.CrateType;
import com.badbones69.crazycrates.api.enums.KeyType;
import com.badbones69.crazycrates.api.events.PhysicalCrateKeyCheckEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.CrateLocation;
import com.badbones69.crazycrates.cratetypes.QuickCrate;
import com.badbones69.crazycrates.modules.config.files.Config;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import java.util.HashMap;

public class CrateControlListener implements Listener { // Crate Control

    // A list of crate locations that are in use.
    private final HashMap<Player, Location> inUse = new HashMap<>();

    public void removePlayer(Player player) {
        inUse.remove(player);
    }

    public boolean containsPlayer(Location location) {
        return inUse.containsValue(location);
    }

    public void addPlayer(Player player, Location location) {
        inUse.put(player, location);
    }

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrazyManager crazyManager;
    private final Methods methods;
    private final QuickCrate quickCrate;

    public CrateControlListener(CrazyManager crazyManager, Methods methods, QuickCrate quickCrate) {
        this.crazyManager = crazyManager;
        this.methods = methods;
        this.quickCrate = quickCrate;
    }

    // This event controls when a player tries to click in a GUI based crate type. This will stop them from taking items out of their inventories.
    @EventHandler(ignoreCancelled = true)
    public void onCrateInventoryClick(InventoryClickEvent e) {
        for (Crate crate : crazyManager.getCrates()) {
            if (crate.getCrateType() != CrateType.MENU && crate.isCrateMenu(e.getView())) e.setCancelled(true);
        }
    }

    // This must run as highest, so it doesn't cause other plugins to check
    // the items that were added to the players inventory and replaced the item in the player's hand.
    // This is only an issue with QuickCrate
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCrateOpen(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if (e.getHand() == EquipmentSlot.OFF_HAND) {
            if (crazyManager.isKey(player.getInventory().getItemInOffHand())) {
                e.setCancelled(true);
                player.updateInventory();
            }

            return;
        }

        Block clickedBlock = e.getClickedBlock();

        if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
            // Loops through all loaded physical locations.
            for (CrateLocation loc : crazyManager.getCrateLocations()) {
                // Checks to see if the clicked block is the same as a physical crate.
                if (loc.getLocation().equals(clickedBlock.getLocation())) {
                    // Checks to see if the player is removing a crate location.
                    if (player.getGameMode() == GameMode.CREATIVE && player.isSneaking() && player.hasPermission("crazycrates.admin")) {
                        e.setCancelled(true);
                        //crazyManager.removeCrateLocation(loc.getID());
                        //player.sendMessage(Messages.REMOVED_PHYSICAL_CRATE.getMessage("%ID%", loc.getID(), methods));
                        return;
                    }

                    e.setCancelled(true);

                    if (loc.getCrateType() != CrateType.MENU) {
                        if (loc.getCrate().isPreviewEnabled()) {
                            //previewListener.setPlayerInMenu(player, false);
                            //previewListener.openNewPreview(player, loc.getCrate());
                        } else {
                            //player.sendMessage(Messages.PREVIEW_DISABLED.getMessage(methods));
                        }
                    }
                }
            }
        } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // Checks if the item in their hand is a key and if so it stops them from right-clicking with it.
            ItemStack key = player.getInventory().getItemInMainHand();
            boolean keyInHand = crazyManager.isKey(key);

            if (!keyInHand) keyInHand = crazyManager.isKey(player.getEquipment().getItemInOffHand());

            if (keyInHand) {
                e.setCancelled(true);
                player.updateInventory();
            }

            // Checks to see if the clicked block is a physical crate.
            CrateLocation crateLocation = crazyManager.getCrateLocation(clickedBlock.getLocation());

            if (crateLocation != null && crateLocation.getCrate() != null) {
                Crate crate = crateLocation.getCrate();
                e.setCancelled(true);

                if (crate.getCrateType() == CrateType.MENU) {
                    // This is to stop players in QuadCrate to not be able to try and open a crate set to menu.
                    //if (!crazyManager.isInOpeningList(player)) menuListener.openGUI(player);

                    return;
                }

                PhysicalCrateKeyCheckEvent event = new PhysicalCrateKeyCheckEvent(player, crateLocation);
                plugin.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    boolean hasKey = false;
                    boolean isPhysical = false;
                    boolean useQuickCrateAgain = false;
                    String keyName = crate.getKey().getItemMeta().getDisplayName();
                    keyName = keyName != null ? keyName : crate.getKey().getType().toString();

                    if (crate.getCrateType() != CrateType.CRATE_ON_THE_GO && keyInHand && crazyManager.isKeyFromCrate(key, crate)) {
                        hasKey = true;
                        isPhysical = true;
                    }

                    if (Config.PHYSICAL_CRATE_ACCEPTS_VIRTUAL_KEYS && crazyManager.getVirtualKeys(player, crate) >= 1) hasKey = true;

                    if (hasKey) {
                        // Checks if the player uses the quick crate again.
                        if (crazyManager.isInOpeningList(player) && crazyManager.getOpeningCrate(player).getCrateType() == CrateType.QUICK_CRATE &&
                                inUse.containsKey(player) && inUse.get(player).equals(crateLocation.getLocation())) {
                            useQuickCrateAgain = true;
                        }

                        if (!useQuickCrateAgain) {
                            if (crazyManager.isInOpeningList(player)) {
                                //player.sendMessage(Messages.ALREADY_OPENING_CRATE.getMessage("%Key%", keyName, methods));
                                return;
                            }

                            if (inUse.containsValue(crateLocation.getLocation())) {
                                //player.sendMessage(Messages.QUICK_CRATE_IN_USE.getMessage(methods));
                                return;
                            }
                        }

                        if (!player.getInventory().isEmpty()) {
                            //player.sendMessage(Messages.INVENTORY_FULL.getMessage(methods));
                            return;
                        }

                        if (useQuickCrateAgain) quickCrate.endQuickCrate(player, crateLocation.getLocation());

                        KeyType keyType = isPhysical ? KeyType.PHYSICAL_KEY : KeyType.VIRTUAL_KEY;

                        // Only cosmic crate type uses this method.
                        if (crate.getCrateType() == CrateType.COSMIC) crazyManager.addPlayerKeyType(player, keyType);

                        crazyManager.addPlayerToOpeningList(player, crate);
                        crazyManager.openCrate(player, crate, keyType, crateLocation.getLocation(), false, true);
                    } else {
                        if (crate.getCrateType() != CrateType.CRATE_ON_THE_GO) {
                            if (Config.TOGGLE_CRATE_KNOCKBACK) knockBack(player, clickedBlock.getLocation());

                            if (Config.KEY_SOUND_ENABLED) player.playSound(player.getLocation(), Sound.valueOf(Config.KEY_SOUND_NAME), 1f, 1f);

                           // player.sendMessage(Messages.NO_KEY.getMessage("%Key%", keyName, methods));
                        }
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        if (crazyManager.hasCrateTask(player)) crazyManager.endCrate(player);

        if (crazyManager.hasQuadCrateTask(player)) crazyManager.endQuadCrate(player);

        if (crazyManager.isInOpeningList(player)) crazyManager.removePlayerFromOpeningList(player);
    }

    public void knockBack(Player player, Location location) {
        Vector vector = player.getLocation().toVector().subtract(location.toVector()).normalize().multiply(1).setY(.1);

        if (player.isInsideVehicle() && player.getVehicle() != null) {
            player.getVehicle().setVelocity(vector);
            return;
        }

        player.setVelocity(vector);
    }
}