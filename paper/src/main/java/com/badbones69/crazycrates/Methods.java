package com.badbones69.crazycrates;

import com.badbones69.crazycrates.api.utilities.LoggerUtils;
import com.badbones69.crazycrates.api.utilities.handlers.objects.crates.Crate;
import com.badbones69.crazycrates.api.utilities.handlers.objects.ItemBuilder;
import com.badbones69.crazycrates.common.configuration.files.Config;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
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

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final LoggerUtils loggerUtils = plugin.getStarter().getLoggerUtils();

    private final Random random = new Random();

    public String removeColor(String msg) {
        return ChatColor.stripColor(msg);
    }

    public void firework(Location loc, List<Color> colors) {
        Firework firework = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffects(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(colors).trail(false).flicker(false).build());
        fireworkMeta.setPower(0);
        firework.setFireworkMeta(fireworkMeta);

        //fireworkDamageListener.addFirework(firework, plugin);

        detonate(firework);
    }

    public void firework(Location loc) {
        Firework firework = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffects(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(Color.RED).withColor(Color.AQUA).withColor(Color.ORANGE).withColor(Color.YELLOW).trail(false).flicker(false).build());
        fireworkMeta.setPower(0);
        firework.setFireworkMeta(fireworkMeta);

        //fireworkDamageListener.addFirework(firework, plugin);

        detonate(firework);
    }

    private void detonate(Firework firework) {
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, firework::detonate, 2);
    }

    public Player getPlayer(String name) {
        return plugin.getServer().getPlayerExact(name);
    }

    public boolean isOnline(String name, CommandSender sender) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(name)) return true;
        }

        //sender.sendMessage(Messages.NOT_ONLINE.getMessage("%Player%", name, this));
        return false;
    }

    public void removeItem(ItemStack item, Player player) {
        try {
            if (item.getAmount() <= 1) {
                player.getInventory().removeItem(item);
            } else {
                item.setAmount(item.getAmount() - 1);
            }
        } catch (Exception ignored) {}
    }

    public Integer randomNumber(int min, int max) {
        return min + random.nextInt(max - min);
    }

    // TODO() Switch to using PDC
    public boolean isSimilar(ItemStack itemStack, Crate crate) {
        NBTItem nbtItem = new NBTItem(itemStack);

        return nbtItem.hasKey("CrazyCrates-Crate") && crate.getName().equals(nbtItem.getString("CrazyCrates-Crate"));
    }

    public Enchantment getEnchantment(String enchantmentName) {
        HashMap<String, String> enchantments = ItemBuilder.getEnchantmentList();
        enchantmentName = stripEnchantmentName(enchantmentName);

        for (Enchantment enchantment : Enchantment.values()) {
            try {
                if (stripEnchantmentName(enchantment.getKey().getKey()).equalsIgnoreCase(enchantmentName)) return enchantment;

                if (stripEnchantmentName(enchantment.getName()).equalsIgnoreCase(enchantmentName) || (enchantments.get(enchantment.getName()) != null &&
                        stripEnchantmentName(enchantments.get(enchantment.getName())).equalsIgnoreCase(enchantmentName))) return enchantment;
            } catch (Exception ignore) {}
        }

        return null;
    }

    private String stripEnchantmentName(String enchantmentName) {
        return enchantmentName != null ? enchantmentName.replace("-", "").replace("_", "").replace(" ", "") : null;
    }

    public ItemBuilder getRandomPaneColor() {
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

    // TODO: Shrink this code
    public void failedToTakeKey(Player player, Crate crate) {
        failedToTakeKey(player, crate, null);
    }

    public void failedToTakeKey(Player player, Crate crate, Exception e) {
        // TODO: Configurable error message
        //player.sendMessage(getPrefix("&cAn issue has occurred when trying to take a key and so the crate failed to open."));

        if (Config.TOGGLE_VERBOSE) {
            loggerUtils.debug("<red>An error has occurred while trying to take a physical key from a player.</red>");
            loggerUtils.debug("<gold>Player:</gold> <red>" + player.getName() + "</red>");
            loggerUtils.debug("<gold>Gold:</gold> <red>" + crate.getName() + "</red>");
        }

        if (e != null) e.printStackTrace();
    }

    /**
     * @author ElectronicBoy
     *
     * @param inventory - The inventory of the player.
     * @param items - The items in the inventory of the player to check.
     */
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
                    assert itemStack != null;
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
                if (toDelete <= 0) break;
            }
        }

        return leftover;
    }

    private int firstFromInventory(ItemStack item, boolean withAmount, ItemStack[] inventory) {
        if (item == null) return -1;

        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == null) continue;

            if (withAmount ? item.equals(inventory[i]) : item.isSimilar(inventory[i])) return i;
        }

        return -1;
    }
}