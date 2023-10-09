package us.crazycrew.crazycrates.paper.misc;

import com.ryderbelserion.cluster.bukkit.utils.LegacyLogger;
import com.ryderbelserion.cluster.bukkit.utils.LegacyUtils;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataContainer;
import us.crazycrew.crazycrates.common.api.enums.Permissions;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.api.enums.PersistentKey;
import java.util.HashMap;
import java.util.Random;

public class Methods {

    private final CrazyCrates plugin;

    public Methods(CrazyCrates plugin) {
        this.plugin = plugin;
    }

    public void broadcastMessage(Player player, FileConfiguration crate) {
        String value = crate.getString("Crate.BroadCast");

        boolean toggle = crate.getBoolean("Crate.OpeningBroadCast");

        if (toggle && value != null) {
            if (value.isBlank()) return;

            Server server = this.plugin.getServer();

            String message = LegacyUtils.color(value
                    .replaceAll("\\{prefix}", "")
                    .replaceAll("\\{player}", player.getName()));

            //noinspection deprecation
            server.broadcastMessage(message);
        }
    }

    public void sendMessage(CommandSender sender, String message, boolean toggle) {
        if (message == null || message.isBlank() || !toggle) return;

        String newMessage = LegacyUtils.color(message.replaceAll("\\{prefix}", "your-prefix"));

        if (sender instanceof Player player) {
            player.sendMessage(newMessage);

            return;
        }

        sender.sendMessage(newMessage);
    }

    public void sendCommand(String command) {
        Server server = this.plugin.getServer();

        ConsoleCommandSender console = server.getConsoleSender();

        server.dispatchCommand(console, command);
    }

    public void spawnFirework(Location location, Color color) {
        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);

        FireworkMeta fireworkMeta = firework.getFireworkMeta();

        FireworkEffect effect = FireworkEffect.builder()
                .with(FireworkEffect.Type.BALL)
                .withColor(color)
                .trail(false)
                .flicker(false)
                .build();

        fireworkMeta.addEffects(effect);
        fireworkMeta.setPower(0);

        firework.setFireworkMeta(fireworkMeta);

        setEntityData(firework, PersistentKey.no_firework_damage);

        Server server = this.plugin.getServer();

        server.getScheduler().scheduleSyncDelayedTask(this.plugin, firework::detonate, 3);
    }

    /**
     * Add a persistent tag to an entity.
     */
    @SuppressWarnings("unchecked")
    public void setEntityData(Entity entity, PersistentKey key) {
        PersistentDataContainer entityData = entity.getPersistentDataContainer();

        entityData.set(key.getNamespacedKey(), key.getType(), true);
    }

    /**
     * Checks if the player's inventory is empty by checking if the first empty slot is -1
     *
     * @return true or false
     */
    public boolean isInventoryEmpty(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }

    /**
     * Remove or subtract an item from a player's inventory.
     */
    public void removeItemStack(Player player, ItemStack item) {
        if (item.getAmount() <= 1) {
            player.getInventory().removeItem(item);
            return;
        }

        item.setAmount(item.getAmount() - 1);
    }

    /**
     * Remove multiple items from a player's inventory.
     */
    public void removeItemStack(Player player, ItemStack... items) {
        if (items == null) {
            LegacyLogger.warn("Items cannot be null.");
            return;
        }

        Inventory inventory = player.getInventory();

        HashMap<Integer, ItemStack> stuckItems = new HashMap<>();
        ItemStack[] toSearch = inventory.getContents();

        for (ItemStack item : items) {
            int toDelete = item.getAmount();

            while (toDelete > 0) {
                int first = getFirstItem(item, false, toSearch);

                // Drat! we don't have this type in the inventory
                if (first == -1) {
                    ItemStack itemClone = item.clone();
                    itemClone.setAmount(toDelete);
                    // Clear the slot, all used up
                    stuckItems.put(stuckItems.size(), itemClone);
                    break;
                } else {
                    ItemStack itemStack = toSearch[first];
                    int amount = itemStack.getAmount();

                    if (amount <= toDelete) {
                        toSearch[first] = null;
                        toDelete -= amount;
                    } else {
                        // Split the stack and store
                        itemStack.setAmount(amount - toDelete);
                        toSearch[first] = itemStack;
                        toDelete = 0;
                    }
                }
            }
        }
    }

    /**
     * Gets the first item amount in an inventory.
     *
     * @return -1 or item amount.
     */
    @SuppressWarnings("SameParameterValue")
    private int getFirstItem(ItemStack item, boolean getAmount, ItemStack[] inventory) {
        if (item == null) return -1;

        for (int i = 0; i < inventory.length; i++) {
            ItemStack inventoryItem = inventory[i];

            if (inventoryItem != null) {
                if ((getAmount && item.equals(inventoryItem)) || (!getAmount && item.isSimilar(inventoryItem))) return i;
            }
        }

        return -1;
    }

    /**
     * Check if the player has a permission with a switch for tab filtering. Ignore if console command sender or remote console command sender.
     *
     * @return true or false
     */
    public boolean runPermissionCheck(CommandSender sender, Permissions permissions, boolean tab) {
        if (sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender) return true;

        Player player = (Player) sender;

        if (player.hasPermission(permissions.getPermission())) {
            return true;
        } else if (!tab) {
            // TODO() Update message enum.
            //player.sendMessage(Messages.NO_PERMISSION.getMessage());
            return false;
        }

        return false;
    }

    /**
     * Creates a random number between max and min.
     *
     * @return a randomized number
     */
    public int randomNumber(int min, int max) {
        return min + new Random().nextInt(max - min);
    }
}