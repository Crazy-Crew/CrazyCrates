package com.badbones69.crazycrates.paper;

import com.badbones69.crazycrates.api.enums.Permissions;
import com.badbones69.crazycrates.paper.api.CrazyManager;
import com.badbones69.crazycrates.paper.api.FileManager;
import com.badbones69.crazycrates.paper.api.enums.settings.Messages;
import com.badbones69.crazycrates.paper.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.ItemBuilder;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import com.badbones69.crazycrates.api.enums.types.CrateType;
import com.badbones69.crazycrates.paper.listeners.FireworkDamageListener;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.regex.Matcher.quoteReplacement;

@SuppressWarnings("deprecation")
public class Methods {

    private static final CrazyCrates plugin = CrazyCrates.getPlugin();

    private static final CrazyManager crazyManager = plugin.getStarter().getCrazyManager();

    public final static Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F\\d]{6}");

    public static String color(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of(matcher.group()).toString());
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    public static void broadCastMessage(FileConfiguration crateFile, Player player) {
        String crateBroadcast = crateFile.getString("Crate.BroadCast");
        String crateBroadcastBooleanExists = crateFile.getString("Crate.OpeningBroadCast");
        boolean crateBroadcastBoolean = crateFile.getBoolean("Crate.OpeningBroadCast");
        if (crateBroadcastBoolean && crateBroadcastBooleanExists != null && crateBroadcast != null) {
            if (crateBroadcast.isEmpty()) return;
            plugin.getServer().broadcastMessage(color(crateBroadcast.replaceAll("%prefix%", quoteReplacement(getPrefix())).replaceAll("%player%", player.getName()).replaceAll("%Prefix%", quoteReplacement(getPrefix())).replaceAll("%Player%", player.getName())));
        }
    }

    public static void sendMessage(CommandSender commandSender, String message, boolean prefixToggle) {
        if (message == null || message.isEmpty()) return;

        String prefix = getPrefix();

        if (commandSender instanceof Player player) {
            if (!prefix.isEmpty() && prefixToggle) player.sendMessage(color(message.replaceAll("%prefix%", quoteReplacement(prefix))).replaceAll("%Prefix%", quoteReplacement(prefix))); else player.sendMessage(color(message));

            return;
        }

        if (!prefix.isEmpty() && prefixToggle) commandSender.sendMessage(color(message.replaceAll("%prefix%", quoteReplacement(prefix))).replaceAll("%Prefix%", quoteReplacement(prefix))); else commandSender.sendMessage(color(message));
    }

    public static void sendCommand(String command) {
        ConsoleCommandSender console = plugin.getServer().getConsoleSender();

        plugin.getServer().dispatchCommand(console, command);
    }

    public static String sanitizeColor(String msg) {
        return sanitizeFormat(color(msg));
    }

    public static String removeColor(String msg) {
        return ChatColor.stripColor(msg);
    }

    public static HashMap<ItemStack, String> getItems(Player player) {
        HashMap<ItemStack, String> items = new HashMap<>();
        FileConfiguration file = crazyManager.getOpeningCrate(player).getFile();

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
                    num = 1 + new Random().nextInt(max);

                    if (num <= chance) items.put(item, "Crate.Prizes." + reward);
                }
            } catch (Exception ignored) {}
        }

        return items;
    }

    public static void firework(Location loc) {
        final Firework fw = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta fm = fw.getFireworkMeta();
        fm.addEffects(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(Color.RED).withColor(Color.AQUA).withColor(Color.ORANGE).withColor(Color.YELLOW).trail(false).flicker(false).build());
        fm.setPower(0);
        fw.setFireworkMeta(fm);
        FireworkDamageListener.addFirework(fw);

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, fw :: detonate, 2);
    }

    public static void firework(Location loc, Color color) {
        final Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fm = fw.getFireworkMeta();
        fm.addEffects(FireworkEffect.builder().with(FireworkEffect.Type.BALL).withColor(color).withColor(color).trail(false).flicker(false).build());
        fm.setPower(0);
        fw.setFireworkMeta(fm);
        FireworkDamageListener.addFirework(fw);

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, fw :: detonate, 2);
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
        return plugin.getServer().getPlayerExact(name);
    }

    public static boolean isOnline(String name, CommandSender sender) {

        for (Player player : plugin.getServer().getOnlinePlayers()) {
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
        } catch (Exception ignored) {}
    }

    public static boolean permCheck(CommandSender sender, Permissions permissions, boolean tabComplete) {
        if (sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender) return true;

        Player player = (Player) sender;

        if (player.hasPermission(permissions.getPermission("command"))) {
            return true;
        } else {
            if (!tabComplete) {
                player.sendMessage(Messages.NO_PERMISSION.getMessage());
                return false;
            }

            return false;
        }
    }

    public static String getPrefix() {
        return color(FileManager.Files.CONFIG.getFile().getString("Settings.Prefix"));
    }

    public static String getPrefix(String msg) {
        return color(FileManager.Files.CONFIG.getFile().getString("Settings.Prefix") + msg);
    }

    public static boolean isInventoryFull(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }

    public static Integer randomNumber(int min, int max) {
        return min + new Random().nextInt(max - min);
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
        return new ItemBuilder().setMaterial(colors.get(new Random().nextInt(colors.size())));
    }

    /**
     * Decides when the crate should start to slow down.
     */
    public static ArrayList<Integer> slowSpin() {
        ArrayList<Integer> slow = new ArrayList<>();
        int full = 46;
        int cut = 9;

        for (int i = 46; cut > 0; full--) {
            if (full <= i - cut || full >= i - cut) {
                slow.add(i);
                i -= cut;
                cut--;
            }
        }

        return slow;
    }

    /**
     * Picks the prize for the player.
     * @param player - The player who the prize is for.
     * @param crate - The crate the player is opening.
     * @param prize - The prize the player is being given.
     */
    public static void pickPrize(Player player, Crate crate, Prize prize) {
        if (prize != null) {
            crazyManager.givePrize(player, prize, crate);

            if (prize.useFireworks()) firework(player.getLocation().add(0, 1, 0));

            plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), prize));
        } else {
            player.sendMessage(getPrefix("&cNo prize was found, please report this issue if you think this is an error."));
        }
    }

    public static void checkPrize(Prize prize, CrazyManager crazyManager, CrazyCrates plugin, Player player, Crate crate) {
        if (prize != null) {
            crazyManager.givePrize(player, prize, crate);

            if (prize.useFireworks()) Methods.firework(player.getLocation().add(0, 1, 0));

            plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), prize));
        } else {
            player.sendMessage(Methods.getPrefix("&cNo prize was found, please report this issue if you think this is an error."));
        }
    }

    public static void failedToTakeKey(CommandSender player, Crate crate) {
        plugin.getServer().getLogger().warning("An error has occurred while trying to take a physical key from a player");
        plugin.getServer().getLogger().warning("Player: " + player.getName());
        plugin.getServer().getLogger().warning("Crate: " + crate.getName());

        player.sendMessage(Methods.getPrefix("&cAn issue has occurred when trying to take a key."));
        player.sendMessage(Methods.getPrefix("&cCommon reasons includes not having enough keys."));
    }

    public static String sanitizeFormat(String string) {
        return TextComponent.toLegacyText(TextComponent.fromLegacyText(string));
    }

    // Thanks ElectronicBoy
    public static HashMap<Integer, ItemStack> removeItemAnySlot(Inventory inventory, ItemStack... items) {
        if (items != null) {
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
        } else {
            plugin.getLogger().info("Items cannot be null.");
        }

        return null;
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
