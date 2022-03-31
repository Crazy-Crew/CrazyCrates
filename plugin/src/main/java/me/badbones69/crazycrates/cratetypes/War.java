package me.badbones69.crazycrates.cratetypes;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.enums.CrateType;
import me.badbones69.crazycrates.api.enums.KeyType;
import me.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.api.objects.ItemBuilder;
import me.badbones69.crazycrates.api.objects.Prize;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class War implements Listener {
    
    private static final String crateNameString = "Crate.CrateName";
    private static final CrazyCrates cc = CrazyCrates.getInstance();
    private static HashMap<ItemStack, String> colorCodes;
    private static final HashMap<Player, Boolean> canPick = new HashMap<>();
    private static final HashMap<Player, Boolean> canClose = new HashMap<>();
    
    public static void openWarCrate(Player player, Crate crate, KeyType keyType, boolean checkHand) {
        String crateName = Methods.sanitizeColor(crate.getFile().getString(crateNameString));
        Inventory inv = Bukkit.createInventory(null, 9, crateName);
        setRandomPrizes(player, inv, crate, crateName);
        InventoryView inventoryView = player.openInventory(inv);
        canPick.put(player, false);
        canClose.put(player, false);
        if (!cc.takeKeys(1, player, crate, keyType, checkHand)) {
            Methods.failedToTakeKey(player, crate);
            cc.removePlayerFromOpeningList(player);
            canClose.remove(player);
            canPick.remove(player);
            return;
        }
        startWar(player, inv, crate, inventoryView.getTitle());
    }
    
    private static void startWar(final Player player, final Inventory inv, final Crate crate, final String inventoryTitle) {
        cc.addCrateTask(player, new BukkitRunnable() {
            int full = 0;
            int open = 0;
            
            @Override
            public void run() {
                if (full < 25) {//When Spinning
                    setRandomPrizes(player, inv, crate, inventoryTitle);
                    player.playSound(player.getLocation(), cc.getSound("BLOCK_LAVA_POP", "LAVA_POP"), 1, 1);
                }
                open++;
                if (open >= 3) {
                    player.openInventory(inv);
                    open = 0;
                }
                full++;
                if (full == 26) {//Finished Rolling
                    player.playSound(player.getLocation(), cc.getSound("BLOCK_LAVA_POP", "LAVA_POP"), 1, 1);
                    setRandomGlass(player, inv, inventoryTitle);
                    canPick.put(player, true);
                }
            }
        }.runTaskTimer(cc.getPlugin(), 1, 3));
    }
    
    private static void setRandomPrizes(Player player, Inventory inv, Crate crate, String inventoryTitle) {
        if (cc.isInOpeningList(player) && inventoryTitle.equalsIgnoreCase(Methods.sanitizeColor(cc.getOpeningCrate(player).getFile().getString(crateNameString)))) {
            for (int i = 0; i < 9; i++) {
                inv.setItem(i, crate.pickPrize(player).getDisplayItem());
            }
        }
    }
    
    private static void setRandomGlass(Player player, Inventory inv, String inventoryTitle) {
        if (cc.isInOpeningList(player) && inventoryTitle.equalsIgnoreCase(Methods.sanitizeColor(cc.getOpeningCrate(player).getFile().getString(crateNameString)))) {
            if (colorCodes == null) {
                colorCodes = getColorCode();
            }
            ItemBuilder itemBuilder = Methods.getRandomPaneColor();
            itemBuilder.setName("&" + colorCodes.get(itemBuilder.build()) + "&l???");
            ItemStack item = itemBuilder.build();
            for (int i = 0; i < 9; i++) {
                inv.setItem(i, item);
            }
        }
    }
    
    private static HashMap<ItemStack, String> getColorCode() {
        HashMap<ItemStack, String> colorCodes = new HashMap<>();
        colorCodes.put(new ItemBuilder().setMaterial("WHITE_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:0").build(), "f");
        colorCodes.put(new ItemBuilder().setMaterial("ORANGE_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:1").build(), "6");
        colorCodes.put(new ItemBuilder().setMaterial("MAGENTA_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:2").build(), "d");
        colorCodes.put(new ItemBuilder().setMaterial("LIGHT_BLUE_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:3").build(), "3");
        colorCodes.put(new ItemBuilder().setMaterial("YELLOW_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:4").build(), "e");
        colorCodes.put(new ItemBuilder().setMaterial("LIME_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:5").build(), "a");
        colorCodes.put(new ItemBuilder().setMaterial("PINK_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:6").build(), "c");
        colorCodes.put(new ItemBuilder().setMaterial("GRAY_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:7").build(), "7");
        colorCodes.put(new ItemBuilder().setMaterial("LIGHT_GRAY_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:8").build(), "7");
        colorCodes.put(new ItemBuilder().setMaterial("CYAN_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:9").build(), "3");
        colorCodes.put(new ItemBuilder().setMaterial("PURPLE_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:10").build(), "5");
        colorCodes.put(new ItemBuilder().setMaterial("BLUE_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:11").build(), "9");
        colorCodes.put(new ItemBuilder().setMaterial("BROWN_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:12").build(), "6");
        colorCodes.put(new ItemBuilder().setMaterial("GREEN_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:13").build(), "2");
        colorCodes.put(new ItemBuilder().setMaterial("RED_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:14").build(), "4");
        colorCodes.put(new ItemBuilder().setMaterial("BLACK_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:15").build(), "8");
        return colorCodes;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        final Player player = (Player) e.getWhoClicked();
        final Inventory inv = e.getInventory();
        if (inv != null && canPick.containsKey(player) && cc.isInOpeningList(player)) {
            Crate crate = cc.getOpeningCrate(player);
            if (crate.getCrateType() == CrateType.WAR && canPick.get(player)) {
                ItemStack item = e.getCurrentItem();
                if (item != null && item.getType().toString().contains("STAINED_GLASS_PANE")) {
                    final int slot = e.getRawSlot();
                    Prize prize = crate.pickPrize(player);
                    inv.setItem(slot, prize.getDisplayItem());
                    if (cc.hasCrateTask(player)) {
                        cc.endCrate(player);
                    }
                    canPick.remove(player);
                    canClose.put(player, true);
                    cc.givePrize(player, prize);
                    if (prize.useFireworks()) {
                        Methods.fireWork(player.getLocation().add(0, 1, 0));
                    }
                    Bukkit.getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), prize));
                    cc.removePlayerFromOpeningList(player);
                    player.playSound(player.getLocation(), cc.getSound("BLOCK_ANVIL_PLACE", "ANVIL_LAND"), 1, 1);
                    //Sets all other non picked prizes to show what they could have been.
                    cc.addCrateTask(player, new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < 9; i++) {
                                if (i != slot) {
                                    inv.setItem(i, crate.pickPrize(player).getDisplayItem());
                                }
                            }
                            if (cc.hasCrateTask(player)) {
                                cc.endCrate(player);
                            }
                            //Removing other items then the prize.
                            cc.addCrateTask(player, new BukkitRunnable() {
                                @Override
                                public void run() {
                                    for (int i = 0; i < 9; i++) {
                                        if (i != slot) {
                                            inv.setItem(i, new ItemStack(Material.AIR));
                                        }
                                    }
                                    if (cc.hasCrateTask(player)) {
                                        cc.endCrate(player);
                                    }
                                    //Closing the inventory when finished.
                                    cc.addCrateTask(player, new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            if (cc.hasCrateTask(player)) {
                                                cc.endCrate(player);
                                            }
                                            player.closeInventory();
                                        }
                                    }.runTaskLater(cc.getPlugin(), 30));
                                }
                            }.runTaskLater(cc.getPlugin(), 30));
                        }
                    }.runTaskLater(cc.getPlugin(), 30));
                }
            }
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        if (canClose.containsKey(player) && canClose.get(player)) {
            for (Crate crate : cc.getCrates()) {
                if (crate.getCrateType() == CrateType.WAR && e.getView().getTitle().equalsIgnoreCase(Methods.sanitizeColor(crate.getFile().getString(crateNameString)))) {
                    canClose.remove(player);
                    if (cc.hasCrateTask(player)) {
                        cc.endCrate(player);
                    }
                }
            }
        }
    }
    
}