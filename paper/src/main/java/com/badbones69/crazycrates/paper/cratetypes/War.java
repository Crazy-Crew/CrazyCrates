package com.badbones69.crazycrates.paper.cratetypes;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.Methods;
import com.badbones69.crazycrates.paper.api.CrazyManager;
import org.bukkit.SoundCategory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.paper.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.ItemBuilder;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import us.crazycrew.crazycrates.paper.api.plugin.CrazyHandler;
import java.util.HashMap;
import java.util.UUID;

public class War implements Listener {

    private final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);
    private final @NotNull CrazyHandler crazyHandler = this.plugin.getCrazyHandler();
    private final @NotNull Methods methods = this.crazyHandler.getMethods();

    private final @NotNull CrazyManager crazyManager = this.plugin.getStarter().getCrazyManager();

    
    private final String crateNameString = "Crate.CrateName";
    private HashMap<ItemStack, String> colorCodes;
    private final HashMap<UUID, Boolean> canPick = new HashMap<>();
    private final HashMap<UUID, Boolean> canClose = new HashMap<>();
    
    public void openWarCrate(Player player, Crate crate, KeyType keyType, boolean checkHand) {
        String crateName = this.methods.sanitizeColor(crate.getFile().getString(crateNameString));
        Inventory inventory = this.plugin.getServer().createInventory(null, 9, crateName);
        setRandomPrizes(player, inventory, crate, crateName);
        InventoryView inventoryView = player.openInventory(inventory);
        this.canPick.put(player.getUniqueId(), false);
        this.canClose.put(player.getUniqueId(), false);

        if (!this.crazyManager.takeKeys(1, player, crate, keyType, checkHand)) {
            this.methods.failedToTakeKey(player, crate);
            this.crazyManager.removePlayerFromOpeningList(player);
            this.canClose.remove(player.getUniqueId());
            this.canPick.remove(player.getUniqueId());
            return;
        }

        startWar(player, inventory, crate, inventoryView.getTitle());
    }
    
    private void startWar(final Player player, final Inventory inv, final Crate crate, final String inventoryTitle) {
        this.crazyManager.addCrateTask(player, new BukkitRunnable() {
            int full = 0;
            int open = 0;
            
            @Override
            public void run() {
                if (full < 25) {
                    setRandomPrizes(player, inv, crate, inventoryTitle);
                    player.playSound(player.getLocation(), Sound.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 1f, 1f);
                }

                open++;

                if (open >= 3) {
                    player.openInventory(inv);
                    open = 0;
                }

                full++;

                if (full == 26) { // Finished Rolling
                    player.playSound(player.getLocation(), Sound.BLOCK_LAVA_POP, SoundCategory.BLOCKS,1f, 1f);
                    setRandomGlass(player, inv, inventoryTitle);
                    canPick.put(player.getUniqueId(), true);
                }
            }
        }.runTaskTimer(plugin, 1, 3));
    }
    
    private void setRandomPrizes(Player player, Inventory inventory, Crate crate, String inventoryTitle) {
        if (!crazyManager.isInOpeningList(player) && !inventoryTitle.equalsIgnoreCase(this.methods.sanitizeColor(crazyManager.getOpeningCrate(player).getFile().getString(crateNameString)))) return;

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, crate.pickPrize(player).getDisplayItem());
        }
    }
    
    private void setRandomGlass(Player player, Inventory inv, String inventoryTitle) {
        if (crazyManager.isInOpeningList(player) && !inventoryTitle.equalsIgnoreCase(this.methods.sanitizeColor(crazyManager.getOpeningCrate(player).getFile().getString(crateNameString)))) return;
        if (crazyManager.isInOpeningList(player) && inventoryTitle.equalsIgnoreCase(this.methods.sanitizeColor(crazyManager.getOpeningCrate(player).getFile().getString(crateNameString)))) {

            if (colorCodes == null) colorCodes = getColorCode();

            ItemBuilder itemBuilder = Methods.getRandomPaneColor();
            itemBuilder.setName("&" + colorCodes.get(itemBuilder.build()) + "&l???");
            ItemStack item = itemBuilder.build();

            for (int i = 0; i < 9; i++) {
                inv.setItem(i, item);
            }
        }
    }
    
    private HashMap<ItemStack, String> getColorCode() {
        HashMap<ItemStack, String> colorCodes = new HashMap<>();

        colorCodes.put(new ItemBuilder().setMaterial(Material.WHITE_STAINED_GLASS_PANE).build(), "f");
        colorCodes.put(new ItemBuilder().setMaterial(Material.ORANGE_STAINED_GLASS_PANE).build(), "6");
        colorCodes.put(new ItemBuilder().setMaterial(Material.MAGENTA_STAINED_GLASS_PANE).build(), "d");
        colorCodes.put(new ItemBuilder().setMaterial(Material.LIGHT_BLUE_STAINED_GLASS_PANE).build(), "3");
        colorCodes.put(new ItemBuilder().setMaterial(Material.YELLOW_STAINED_GLASS_PANE).build(), "e");
        colorCodes.put(new ItemBuilder().setMaterial(Material.LIME_STAINED_GLASS_PANE).build(), "a");
        colorCodes.put(new ItemBuilder().setMaterial(Material.PINK_STAINED_GLASS_PANE).build(), "c");
        colorCodes.put(new ItemBuilder().setMaterial(Material.GRAY_STAINED_GLASS_PANE).build(), "7");
        colorCodes.put(new ItemBuilder().setMaterial(Material.LIGHT_GRAY_STAINED_GLASS_PANE).build(), "7");
        colorCodes.put(new ItemBuilder().setMaterial(Material.CYAN_STAINED_GLASS_PANE).build(), "3");
        colorCodes.put(new ItemBuilder().setMaterial(Material.PURPLE_STAINED_GLASS_PANE).build(), "5");
        colorCodes.put(new ItemBuilder().setMaterial(Material.BLUE_STAINED_GLASS_PANE).build(), "9");
        colorCodes.put(new ItemBuilder().setMaterial(Material.BROWN_STAINED_GLASS_PANE).build(), "6");
        colorCodes.put(new ItemBuilder().setMaterial(Material.GREEN_STAINED_GLASS_PANE).build(), "2");
        colorCodes.put(new ItemBuilder().setMaterial(Material.RED_STAINED_GLASS_PANE).build(), "4");
        colorCodes.put(new ItemBuilder().setMaterial(Material.BLACK_STAINED_GLASS_PANE).build(), "8");

        return colorCodes;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        final Player player = (Player) e.getWhoClicked();
        final Inventory inv = e.getInventory();

        if (inv != null && canPick.containsKey(player) && crazyManager.isInOpeningList(player)) {
            Crate crate = crazyManager.getOpeningCrate(player);

            if (crate.getCrateType() == CrateType.WAR && canPick.get(player)) {
                ItemStack item = e.getCurrentItem();

                if (item != null && item.getType().toString().contains(Material.GLASS_PANE.toString())) {
                    final int slot = e.getRawSlot();
                    Prize prize = crate.pickPrize(player);
                    inv.setItem(slot, prize.getDisplayItem());

                    if (crazyManager.hasCrateTask(player)) crazyManager.endCrate(player);

                    canPick.remove(player);
                    canClose.put(player, true);
                    crazyManager.givePrize(player, prize, crate);

                    if (prize.useFireworks()) this.methods.firework(player.getLocation().add(0, 1, 0));

                    plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), prize));
                    crazyManager.removePlayerFromOpeningList(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
                    // Sets all other non-picked prizes to show what they could have been.

                    crazyManager.addCrateTask(player, new BukkitRunnable() {
                        @Override
                        public void run() {

                            for (int i = 0; i < 9; i++) {
                                if (i != slot) inv.setItem(i, crate.pickPrize(player).getDisplayItem());
                            }

                            if (crazyManager.hasCrateTask(player)) crazyManager.endCrate(player);

                            // Removing other items then the prize.
                            crazyManager.addCrateTask(player, new BukkitRunnable() {
                                @Override
                                public void run() {

                                    for (int i = 0; i < 9; i++) {
                                        if (i != slot) inv.setItem(i, new ItemStack(Material.AIR));
                                    }

                                    if (crazyManager.hasCrateTask(player)) crazyManager.endCrate(player);

                                    // Closing the inventory when finished.
                                    crazyManager.addCrateTask(player, new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            if (crazyManager.hasCrateTask(player)) crazyManager.endCrate(player);

                                            player.closeInventory();
                                        }
                                    }.runTaskLater(plugin, 30));
                                }
                            }.runTaskLater(plugin, 30));
                        }
                    }.runTaskLater(plugin, 30));
                }
            }
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();

        if (canClose.containsKey(player) && canClose.get(player)) {
            for (Crate crate : crazyManager.getCrates()) {
                if (crate.getCrateType() == CrateType.WAR && e.getView().getTitle().equalsIgnoreCase(this.methods.sanitizeColor(crate.getFile().getString(crateNameString)))) {
                    canClose.remove(player);

                    if (crazyManager.hasCrateTask(player)) crazyManager.endCrate(player);
                }
            }
        }
    }
}