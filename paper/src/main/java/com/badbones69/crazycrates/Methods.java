package com.badbones69.crazycrates;

import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.enums.Permissions;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.ItemBuilder;
import com.badbones69.crazycrates.listeners.FireworkDamageListener;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Methods {

    private final CrazyCrates plugin;
    private final FireworkDamageListener fireworkDamageListener;

    public Methods(CrazyCrates plugin, FireworkDamageListener fireworkDamageListener) {
        this.plugin = plugin;

        this.fireworkDamageListener = fireworkDamageListener;
    }

    private static final Random random = new Random();
    
    public static String removeColor(String msg) {
        return ChatColor.stripColor(msg);
    }
    
    public HashMap<ItemStack, String> getItems(Player player, CrazyManager crazyManager) {
        HashMap<ItemStack, String> items = new HashMap<>();
        FileConfiguration file = crazyManager.getOpeningCrate(player).getFile();

        for (String reward : file.getConfigurationSection("Crate.Prizes").getKeys(false)) {
            String id = file.getString("Crate.Prizes." + reward + ".DisplayItem");
            String name = file.getString("Crate.Prizes." + reward + ".DisplayName");
            int chance = file.getInt("Crate.Prizes." + reward + ".Chance");
            int max = 99;

            if (file.contains("Crate.Prizes." + reward + ".MaxRange")) max = file.getInt("Crate.Prizes." + reward + ".MaxRange") - 1;

            try {
                ItemStack item = new ItemBuilder().setMaterial(id).setName(name).build();
                int num;

                for (int counter = 1; counter <= 1; counter++) {
                    num = 1 + random.nextInt(max);

                    if (num >= 1 && num <= chance) items.put(item, "Crate.Prizes." + reward);
                }
            } catch (Exception ignored) {}
        }

        return items;
    }
    
    public void firework(Location loc, List<Color> colors) {
        Firework firework = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffects(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(colors).trail(false).flicker(false).build());
        fireworkMeta.setPower(0);
        firework.setFireworkMeta(fireworkMeta);
        fireworkDamageListener.addFirework(firework, plugin);

        detonate(firework);
    }

    public void firework(Location loc) {
        Firework firework = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffects(FireworkEffect.builder()
                .with(FireworkEffect.Type.BALL_LARGE).withColor(Color.RED).withColor(Color.AQUA).withColor(Color.ORANGE).withColor(Color.YELLOW).trail(false).flicker(false).build());
        fireworkMeta.setPower(0);
        firework.setFireworkMeta(fireworkMeta);
        fireworkDamageListener.addFirework(firework, plugin);

        detonate(firework);
    }

    private void detonate(final Firework firework) {
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, firework::detonate, 2);
    }
    
    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }

        return true;
    }
    
    public Player getPlayer(String name) {
        return plugin.getServer().getPlayerExact(name);
    }
    
    public boolean isOnline(String name, CommandSender sender) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }

        //sender.sendMessage(Messages.NOT_ONLINE.getMessage("%Player%", name));
        return false;
    }
    
    public static void removeItem(ItemStack item, Player player) {
        try {
            if (item.getAmount() <= 1) {
                player.getInventory().removeItem(item);
            } else {
                item.setAmount(item.getAmount() - 1);
            }
        } catch (Exception ignored) {}
    }
    
    public static boolean permCheck(CommandSender sender, Permissions permissions, Boolean tabComplete) {
        if (sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender) return true;

        Player player = (Player) sender;

        if (player.hasPermission(permissions.getPermission("command"))) {
            return true;
        } else {
            if (!tabComplete) {
                //player.sendMessage(Messages.NO_PERMISSION.getMessage());
                return false;
            }

            return false;
        }
    }
    
    public static boolean isInventoryFull(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }
    
    public static Integer randomNumber(int min, int max) {
        return min + random.nextInt(max - min);
    }
    
    public static boolean isSimilar(ItemStack itemStack, Crate crate) {
        NBTItem nbtItem = new NBTItem(itemStack);
        return itemStack.isSimilar(crate.getKey()) || itemStack.isSimilar(crate.getKeyNoNBT()) ||
                itemStack.isSimilar(crate.getAdminKey()) || stripNBT(itemStack).isSimilar(crate.getKeyNoNBT()) ||
                isSimilarCustom(crate.getKeyNoNBT(), itemStack) || (nbtItem.hasKey("CrazyCrates-Crate") && crate.getName().equals(nbtItem.getString("CrazyCrates-Crate")));
    }
    
    private static boolean isSimilarCustom(ItemStack one, ItemStack two) {
        if (one != null && two != null) {
            if (one.getType() == two.getType()) {
                if (one.hasItemMeta() && two.hasItemMeta()) {
                    if (one.getItemMeta().hasDisplayName() && two.getItemMeta().hasDisplayName()) {
                        if (one.getItemMeta().getDisplayName().equalsIgnoreCase(two.getItemMeta().getDisplayName())) {
                            if (one.getItemMeta().hasLore() && two.getItemMeta().hasLore()) {
                                if (one.getItemMeta().getLore().size() == two.getItemMeta().getLore().size()) {
                                    int i = 0;

                                    for (String lore : one.getItemMeta().getLore()) {
                                        if (!lore.equals(two.getItemMeta().getLore().get(i))) {
                                            return false;
                                        }

                                        i++;
                                    }

                                    return true;
                                }
                            } else return !one.getItemMeta().hasLore() && !two.getItemMeta().hasLore();
                        }
                    } else if (!one.getItemMeta().hasDisplayName() && !two.getItemMeta().hasDisplayName()) {
                        if (one.getItemMeta().hasLore() && two.getItemMeta().hasLore()) {
                            if (one.getItemMeta().getLore().size() == two.getItemMeta().getLore().size()) {
                                int i = 0;

                                for (String lore : one.getItemMeta().getLore()) {
                                    if (!lore.equals(two.getItemMeta().getLore().get(i))) {
                                        return false;
                                    }

                                    i++;
                                }

                                return true;
                            } else {
                                return false;
                            }
                        } else return !one.getItemMeta().hasLore() && !two.getItemMeta().hasLore();
                    }
                } else return !one.hasItemMeta() && !two.hasItemMeta();
            }
        }

        return false;
    }
    
    private static ItemStack stripNBT(ItemStack item) {
        try {
            NBTItem nbtItem = new NBTItem(item.clone());

            if (nbtItem.hasNBTData()) {
                if (nbtItem.hasKey("CrazyCrates-Crate")) {
                    nbtItem.removeKey("CrazyCrates-Crate");
                }
            }

            return nbtItem.getItem();
        } catch (Exception e) {
            return item;
        }
    }
    
    public static Enchantment getEnchantment(String enchantmentName) {
        HashMap<String, String> enchantments = getEnchantmentList();
        enchantmentName = stripEnchantmentName(enchantmentName);

        for (Enchantment enchantment : Enchantment.values()) {
            try {
                if (stripEnchantmentName(enchantment.getKey().getKey()).equalsIgnoreCase(enchantmentName)) {
                    return enchantment;
                }

                if (stripEnchantmentName(enchantment.getName()).equalsIgnoreCase(enchantmentName) || (enchantments.get(enchantment.getName()) != null &&
                stripEnchantmentName(enchantments.get(enchantment.getName())).equalsIgnoreCase(enchantmentName))) {
                    return enchantment;
                }
            } catch (Exception ignore) {}
        }

        return null;
    }
    
    private static String stripEnchantmentName(String enchantmentName) {
        return enchantmentName != null ? enchantmentName.replace("-", "").replace("_", "").replace(" ", "") : null;
    }
    
    private static HashMap<String, String> getEnchantmentList() {
        HashMap<String, String> enchantments = new HashMap<>();
        enchantments.put("ARROW_DAMAGE", "Power");
        enchantments.put("ARROW_FIRE", "Flame");
        enchantments.put("ARROW_INFINITE", "Infinity");
        enchantments.put("ARROW_KNOCKBACK", "Punch");
        enchantments.put("DAMAGE_ALL", "Sharpness");
        enchantments.put("DAMAGE_ARTHROPODS", "Bane_Of_Arthropods");
        enchantments.put("DAMAGE_UNDEAD", "Smite");
        enchantments.put("DEPTH_STRIDER", "Depth_Strider");
        enchantments.put("DIG_SPEED", "Efficiency");
        enchantments.put("DURABILITY", "Unbreaking");
        enchantments.put("FIRE_ASPECT", "Fire_Aspect");
        enchantments.put("KNOCKBACK", "KnockBack");
        enchantments.put("LOOT_BONUS_BLOCKS", "Fortune");
        enchantments.put("LOOT_BONUS_MOBS", "Looting");
        enchantments.put("LUCK", "Luck_Of_The_Sea");
        enchantments.put("LURE", "Lure");
        enchantments.put("OXYGEN", "Respiration");
        enchantments.put("PROTECTION_ENVIRONMENTAL", "Protection");
        enchantments.put("PROTECTION_EXPLOSIONS", "Blast_Protection");
        enchantments.put("PROTECTION_FALL", "Feather_Falling");
        enchantments.put("PROTECTION_FIRE", "Fire_Protection");
        enchantments.put("PROTECTION_PROJECTILE", "Projectile_Protection");
        enchantments.put("SILK_TOUCH", "Silk_Touch");
        enchantments.put("THORNS", "Thorns");
        enchantments.put("WATER_WORKER", "Aqua_Affinity");
        enchantments.put("BINDING_CURSE", "Curse_Of_Binding");
        enchantments.put("MENDING", "Mending");
        enchantments.put("FROST_WALKER", "Frost_Walker");
        enchantments.put("VANISHING_CURSE", "Curse_Of_Vanishing");
        enchantments.put("SWEEPING_EDGE", "Sweeping_Edge");
        enchantments.put("RIPTIDE", "Riptide");
        enchantments.put("CHANNELING", "Channeling");
        enchantments.put("IMPALING", "Impaling");
        enchantments.put("LOYALTY", "Loyalty");
        return enchantments;
    }
    
    public static ItemBuilder getRandomPaneColor() {
        List<String> colors = Arrays.asList(
        Material.WHITE_STAINED_GLASS_PANE.toString(),
        Material.ORANGE_STAINED_GLASS_PANE.toString(),
        Material.MAGENTA_STAINED_GLASS_PANE.toString(),
        Material.LIGHT_BLUE_STAINED_GLASS_PANE.toString(),
        Material.YELLOW_STAINED_GLASS_PANE.toString(),
        Material.LIME_STAINED_GLASS_PANE.toString(),
        Material.PINK_STAINED_GLASS_PANE.toString(),
        Material.GRAY_STAINED_GLASS_PANE.toString(),
        Material.CYAN_STAINED_GLASS_PANE.toString(),
        Material.PURPLE_STAINED_GLASS_PANE.toString(),
        Material.BLUE_STAINED_GLASS_PANE.toString(),
        Material.BROWN_STAINED_GLASS_PANE.toString(),
        Material.GREEN_STAINED_GLASS_PANE.toString(),
        Material.RED_STAINED_GLASS_PANE.toString(),
        Material.BLACK_STAINED_GLASS_PANE.toString(),
        Material.LIGHT_GRAY_STAINED_GLASS_PANE.toString());
        return new ItemBuilder().setMaterial(colors.get(random.nextInt(colors.size())));
    }
    
    public void failedToTakeKey(Player player, Crate crate) {
        failedToTakeKey(player, crate, null);
    }
    
    public void failedToTakeKey(Player player, Crate crate, Exception e) {
        plugin.getServer().getLogger().warning("An error has occurred while trying to take a physical key from a player");
        plugin.getServer().getLogger().warning("Player: " + player.getName());
        plugin.getServer().getLogger().warning("Crate: " + crate.getName());

        //player.sendMessage(Methods.getPrefix("&cAn issue has occurred when trying to take a key and so the crate failed to open."));

        if (e != null) e.printStackTrace();
    }
    
    // Thanks ElectronicBoy
    public HashMap<Integer, ItemStack> removeItemAnySlot(Inventory inventory, ItemStack... items) {
        Validate.notNull(items, "Items cannot be null");
        HashMap<Integer, ItemStack> leftover = new HashMap<>();
        
        // TODO: optimization
        
        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            int toDelete = item.getAmount();
            
            while (true) {
                // Paper start - Allow searching entire contents
                ItemStack[] toSearch = inventory.getContents();
                int first = firstFromInventory(item, false, toSearch);
                // Paper end
                
                // Drat! we don't have this type in the inventory
                if (first == -1) {
                    item.setAmount(toDelete);
                    leftover.put(i, item);
                    break;
                } else {
                    ItemStack itemStack = inventory.getItem(first);
                    int amount = itemStack.getAmount();
                    
                    if (amount <= toDelete) {
                        toDelete -= amount;
                        // clear the slot, all used up
                        inventory.clear(first);
                    } else {
                        // split the stack and store
                        itemStack.setAmount(amount - toDelete);
                        inventory.setItem(first, itemStack);
                        toDelete = 0;
                    }
                }
                
                // Bail when done
                if (toDelete <= 0) {
                    break;
                }
            }
        }

        return leftover;
    }
    
    private static int firstFromInventory(ItemStack item, boolean withAmount, ItemStack[] inventory) {
        if (item == null) {
            return -1;
        }
        
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == null) continue;
            
            if (withAmount ? item.equals(inventory[i]) : item.isSimilar(inventory[i])) {
                return i;
            }
        }

        return -1;
    }
}