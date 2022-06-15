package me.badbones69.crazycrates.gui;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.FileManager;
import me.badbones69.crazycrates.api.enums.KeyType;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.api.objects.CrateLocation;
import me.badbones69.crazycrates.api.objects.ItemBuilder;
import me.badbones69.crazycrates.cratetypes.QuickCrate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MassOpenGUI implements Listener {
    private static final boolean DEFAULT_AUTOCLOSE = true;
    private static final String SETTINGS_PATH = "Settings.MassOpenGUI.";

    private static final CrazyCrates cc = CrazyCrates.getInstance();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || !(event.getClickedInventory().getHolder() instanceof MassOpenInstance)) {
            return;
        }

        final Player player = (Player) event.getWhoClicked();
        final MassOpenInstance instance = ((MassOpenInstance) event.getClickedInventory().getHolder());

        event.setCancelled(true);
        final int slot = event.getSlot();
        if (slot >= 12 && slot <= 14) {
            final int openCount;
            switch (slot) {
                case 12:
                    openCount = 1;
                    break;
                case 13:
                    openCount = 10;
                    break;
                case 14:
                    openCount = 64;
                    break;
                default:
                    throw new IllegalStateException();
            }

            if (instance.getKeyCount(player) < openCount) {
                instance.update(player);
                return;
            }

            final int physicalKeyCount = Math.min(cc.getPhysicalKeys(player, instance.crate), openCount);
            if (physicalKeyCount > 0) {
                QuickCrate.openCrate(player, instance.crateLocation.getLocation(), instance.crate, KeyType.PHYSICAL_KEY, physicalKeyCount);
            }
            if (openCount > physicalKeyCount) {
                final int remainingKeyCount = Math.min(cc.getVirtualKeys(player, instance.crate), openCount - physicalKeyCount);
                QuickCrate.openCrate(player, instance.crateLocation.getLocation(), instance.crate, KeyType.VIRTUAL_KEY, remainingKeyCount);
            }
            if (instance.shouldAutoClose(player)) {
                player.closeInventory();
            } else {
                instance.update(player);
            }
        } else if (slot == 22) {
            final boolean newState = !instance.shouldAutoClose(player);
            final String key = "AutoClose." + player.getUniqueId();
            if (newState != DEFAULT_AUTOCLOSE) {
                FileManager.Files.DATA.getFile().set(key, newState);
            } else {
                // don't let the file grow unnecessarily if player goes back to default
                FileManager.Files.DATA.getFile().set(key, null);
            }
            FileManager.Files.DATA.saveFile();
            instance.update(player);
        }
    }

    public static void open(Player player, Crate crate, CrateLocation crateLocation) {
        final FileConfiguration config = FileManager.Files.CONFIG.getFile();

        final MassOpenInstance instance = new MassOpenInstance(crate, crateLocation);
        final Inventory inventory = Bukkit.createInventory(instance, 3 * 9,
                Methods.sanitizeColor(config.getString(SETTINGS_PATH + "Title")));
        instance.inventory = inventory;

        inventory.setItem(4, new ItemBuilder()
                .setMaterial(config.getString(SETTINGS_PATH + "Information.Item"))
                .setDamage(config.getInt(SETTINGS_PATH + "Information.Damage", 0))
                .setName(config.getString(SETTINGS_PATH + "Information.Name"))
                .setLore(config.getStringList(SETTINGS_PATH + "Information.Lore"))
                .build());
        instance.update(player);

        player.openInventory(inventory);
    }

    private static ItemStack makeOpenItem(int count, boolean enough) {
        final FileConfiguration config = FileManager.Files.CONFIG.getFile();
        final String path = SETTINGS_PATH + "OpenItem." + (enough ? "Enough" : "NotEnough") + ".";
        return new ItemBuilder()
                .setMaterial(config.getString(path + "Item", "INK_SACK"))
                .setDamage(config.getInt(path + "Damage", 0))
                .setName(config.getString(path + "Name"))
                .setLore(config.getStringList(path + "Lore"))
                .setAmount(count)
                .addNamePlaceholder("%Count%", Integer.toString(count))
                .addLorePlaceholder("%Count%", Integer.toString(count))
                .build();
    }

    private static ItemStack makeAutoClose(boolean enabled) {
        final FileConfiguration config = FileManager.Files.CONFIG.getFile();
        final String path = SETTINGS_PATH + "AutoCloseToggle." + (enabled ? "Enabled" : "Disabled") + ".";
        return new ItemBuilder()
                .setMaterial(config.getString(path + "Item", "STONE"))
                .setDamage(config.getInt(path + "Damage", 0))
                .setName(config.getString(path + "Name"))
                .setLore(config.getStringList(path + "Lore"))
                .build();
    }

    private static class MassOpenInstance implements InventoryHolder {
        public final Crate crate;
        public final CrateLocation crateLocation;

        private Inventory inventory;

        public MassOpenInstance(Crate crate, CrateLocation crateLocation) {
            this.crate = crate;
            this.crateLocation = crateLocation;
        }

        public boolean shouldAutoClose(Player player) {
            return FileManager.Files.DATA.getFile().getBoolean("AutoClose." + player.getUniqueId(), DEFAULT_AUTOCLOSE);
        }

        public int getKeyCount(Player player) {
            return cc.getPhysicalKeys(player, crate) + cc.getVirtualKeys(player, crate);
        }

        public void update(Player player) {
            final int keyCount = getKeyCount(player);

            inventory.setItem(12, makeOpenItem(1, keyCount >= 1));
            inventory.setItem(13, makeOpenItem(10, keyCount >= 10));
            inventory.setItem(14, makeOpenItem(64, keyCount >= 64));

            inventory.setItem(22, makeAutoClose(shouldAutoClose(player)));
        }

        @NotNull
        @Override
        public Inventory getInventory() {
            return inventory;
        }
    }
}
