package me.badbones69.crazycrates;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.FileManager.Files;
import me.badbones69.crazycrates.api.enums.Messages;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.api.objects.ItemBuilder;
import me.badbones69.crazycrates.controllers.FireworkDamageEvent;
import me.badbones69.crazycrates.multisupport.Version;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Methods {
    
    public static HashMap<Player, String> path = new HashMap<>();
    public static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CrazyCrates");
    private static CrazyCrates cc = CrazyCrates.getInstance();
    private static Random random = new Random();
    
    public final static Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");
    
    public static String color(String message) {
        if (Version.isNewer(Version.v1_15_R1)) {
            Matcher matcher = HEX_PATTERN.matcher(message);
            StringBuffer buffer = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of(matcher.group()).toString());
            }
            return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    public static String sanitizeColor(String msg) {
        return sanitizeFormat(color(msg));
    }
    
    public static String removeColor(String msg) {
        return ChatColor.stripColor(msg);
    }
    
    public static HashMap<ItemStack, String> getItems(Player player) {
        HashMap<ItemStack, String> items = new HashMap<>();
        FileConfiguration file = cc.getOpeningCrate(player).getFile();
        for (String reward : file.getConfigurationSection("Crate.Prizes").getKeys(false)) {
            String id = file.getString("Crate.Prizes." + reward + ".DisplayItem");
            String name = file.getString("Crate.Prizes." + reward + ".DisplayName");
            int chance = file.getInt("Crate.Prizes." + reward + ".Chance");
            int max = 99;
            if (file.contains("Crate.Prizes." + reward + ".MaxRange")) {
                max = file.getInt("Crate.Prizes." + reward + ".MaxRange") - 1;
            }
            try {
                ItemStack item = new ItemBuilder().setMaterial(id).setName(name).build();
                int num;
                for (int counter = 1; counter <= 1; counter++) {
                    num = 1 + random.nextInt(max);
                    if (num >= 1 && num <= chance) items.put(item, "Crate.Prizes." + reward);
                }
            } catch (Exception e) {
            }
        }
        return items;
    }
    
    public static void fireWork(Location loc) {
        final Firework fw = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta fm = fw.getFireworkMeta();
        fm.addEffects(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(Color.RED).withColor(Color.AQUA).withColor(Color.ORANGE).withColor(Color.YELLOW).trail(false).flicker(false).build());
        fm.setPower(0);
        fw.setFireworkMeta(fm);
        FireworkDamageEvent.addFirework(fw);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, fw :: detonate, 2);
    }
    
    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    
    public static Player getPlayer(String name) {
        return Bukkit.getPlayerExact(name);
    }
    
    public static boolean isOnline(String name, CommandSender sender) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        sender.sendMessage(Messages.NOT_ONLINE.getMessage("%Player%", name));
        return false;
    }
    
    public static void removeItem(ItemStack item, Player player) {
        try {
            if (item.getAmount() <= 1) {
                player.getInventory().removeItem(item);
            } else {
                item.setAmount(item.getAmount() - 1);
            }
        } catch (Exception e) {
        }
    }
    
    public static boolean permCheck(CommandSender sender, String perm) {
        if (sender instanceof Player) {
            return permCheck((Player) sender, perm);
        } else {
            return true;
        }
    }
    
    public static boolean permCheck(Player player, String perm) {
        if (player.hasPermission("crazycrates." + perm.toLowerCase()) || player.hasPermission("crazycrates.admin")) {
            return true;
        } else {
            player.sendMessage(Messages.NO_PERMISSION.getMessage());
            return false;
        }
    }
    
    public static String getPrefix() {
        return color(Files.CONFIG.getFile().getString("Settings.Prefix"));
    }
    
    public static String getPrefix(String msg) {
        return color(Files.CONFIG.getFile().getString("Settings.Prefix") + msg);
    }
    
    public static List<Location> getLocations(String shem, Location loc) {
        return cc.getNMSSupport().getLocations(new File(plugin.getDataFolder() + "/Schematics/" + shem), loc);
    }
    
    public static boolean isInventoryFull(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }
    
    public static Integer randomNumber(int min, int max) {
        return min + random.nextInt(max - min);
    }
    
    public static boolean isSimilar(Player player, Crate crate) {
        boolean check = isSimilar(cc.getNMSSupport().getItemInMainHand(player), crate);
        if (!check) {
            if (Version.getCurrentVersion().isNewer(Version.v1_8_R3)) {
                check = isSimilar(player.getEquipment().getItemInOffHand(), crate);
            }
        }
        return check;
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
    
    public static void hasUpdate() {
        try {
            HttpURLConnection c = (HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php").openConnection();
            c.setDoOutput(true);
            c.setRequestMethod("POST");
            c.getOutputStream().write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=17599").getBytes(StandardCharsets.UTF_8));
            String oldVersion = plugin.getDescription().getVersion();
            String newVersion = new BufferedReader(new InputStreamReader(c.getInputStream())).readLine().replaceAll("[a-zA-Z ]", "");
            if (!newVersion.equals(oldVersion)) {
                Bukkit.getConsoleSender().sendMessage(getPrefix() + color("&cYour server is running &7v" + oldVersion + "&c and the newest is &7v" + newVersion + "&c."));
            }
        } catch (Exception e) {
        }
    }
    
    public static void hasUpdate(Player player) {
        try {
            HttpURLConnection c = (HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php").openConnection();
            c.setDoOutput(true);
            c.setRequestMethod("POST");
            c.getOutputStream().write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=17599").getBytes(StandardCharsets.UTF_8));
            String oldVersion = plugin.getDescription().getVersion();
            String newVersion = new BufferedReader(new InputStreamReader(c.getInputStream())).readLine().replaceAll("[a-zA-Z ]", "");
            if (!newVersion.equals(oldVersion)) {
                player.sendMessage(getPrefix() + color("&cYour server is running &7v" + oldVersion + "&c and the newest is &7v" + newVersion + "&c."));
            }
        } catch (Exception e) {
        }
    }
    
    public static Set<String> getEnchantments() {
        return getEnchantmentList().keySet();
    }
    
    public static Enchantment getEnchantment(String enchantmentName) {
        HashMap<String, String> enchantments = getEnchantmentList();
        enchantmentName = stripEnchantmentName(enchantmentName);
        for (Enchantment enchantment : Enchantment.values()) {
            try {
                //MC 1.13+ has the correct names.
                if (Version.getCurrentVersion().isNewer(Version.v1_12_R1)) {
                    if (stripEnchantmentName(enchantment.getKey().getKey()).equalsIgnoreCase(enchantmentName)) {
                        return enchantment;
                    }
                }
                if (stripEnchantmentName(enchantment.getName()).equalsIgnoreCase(enchantmentName) || (enchantments.get(enchantment.getName()) != null &&
                stripEnchantmentName(enchantments.get(enchantment.getName())).equalsIgnoreCase(enchantmentName))) {
                    return enchantment;
                }
            } catch (Exception ignore) {//If any null enchantments are found they may cause errors.
            }
        }
        return null;
    }
    
    public static String getEnchantmentName(Enchantment enchantment) {
        HashMap<String, String> enchantments = getEnchantmentList();
        if (enchantments.get(enchantment.getName()) == null) {
            return "None Found";
        }
        return enchantments.get(enchantment.getName());
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
        boolean newMaterial = cc.useNewMaterial();
        List<String> colors = Arrays.asList(
        newMaterial ? "WHITE_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:0",// 0
        newMaterial ? "ORANGE_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:1",// 1
        newMaterial ? "MAGENTA_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:2",// 2
        newMaterial ? "LIGHT_BLUE_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:3",// 3
        newMaterial ? "YELLOW_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:4",// 4
        newMaterial ? "LIME_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:5",// 5
        newMaterial ? "PINK_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:6",// 6
        newMaterial ? "GRAY_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:7",// 7
        //Skipped 8 due to it being basically invisible in a GUI.
        newMaterial ? "CYAN_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:9",// 9
        newMaterial ? "PURPLE_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:10",// 10
        newMaterial ? "BLUE_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:11",// 11
        newMaterial ? "BROWN_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:12",// 12
        newMaterial ? "GREEN_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:13",// 13
        newMaterial ? "RED_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:14",// 14
        newMaterial ? "BLACK_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:15");// 15
        return new ItemBuilder().setMaterial(colors.get(random.nextInt(colors.size())));
    }
    
    public static void failedToTakeKey(Player player, Crate crate) {
        failedToTakeKey(player, crate, null);
    }
    
    public static void failedToTakeKey(Player player, Crate crate, Exception e) {
        Bukkit.getServer().getLogger().warning("[CrazyCrates] An error has occurred while trying to take a physical key from a player");
        Bukkit.getServer().getLogger().warning("Player: " + player.getName());
        Bukkit.getServer().getLogger().warning("Crate: " + crate.getName());
        player.sendMessage(Methods.getPrefix("&cAn issue has occurred when trying to take a key and so the crate failed to open."));
        if (e != null) {
            e.printStackTrace();
        }
    }
    
    public static String sanitizeFormat(String string) {
        return TextComponent.toLegacyText(TextComponent.fromLegacyText(string));
    }

    // Thanks ElectronicBoy
    public static HashMap<Integer, ItemStack> removeItemAnySlot(Inventory inventory, ItemStack... items) {
        Validate.notNull(items, "Items cannot be null");
        HashMap<Integer, ItemStack> leftover = new HashMap<Integer, ItemStack>();

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