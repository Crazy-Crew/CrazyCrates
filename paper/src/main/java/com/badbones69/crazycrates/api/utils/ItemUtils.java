package com.badbones69.crazycrates.api.utils;

import com.badbones69.crazycrates.CrazyCratesPaper;
import com.badbones69.crazycrates.api.enums.PersistentKeys;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ItemUtils {

    private static final CrazyCratesPaper plugin = JavaPlugin.getPlugin(CrazyCratesPaper.class);

    @NotNull
    private static final CrateManager crateManager = plugin.getCrateManager();

    public static void removeItem(ItemStack item, Player player) {
        try {
            if (item.getAmount() <= 1) {
                player.getInventory().removeItem(item);
            } else {
                item.setAmount(item.getAmount() - 1);
            }
        } catch (Exception ignored) {}
    }

    public static boolean isSimilar(ItemStack itemStack, Crate crate) {
        if (MiscUtils.legacyChecks()) {
            return itemStack.isSimilar(crate.getKeyNoNBT())
                    || itemStack.isSimilar(crate.getKeyNoNBT())
                    || isSimilarCustom(crate.getKeyNoNBT(), itemStack)
                    || crateManager.isKeyFromCrate(itemStack, crate);
        }

        return crateManager.isKeyFromCrate(itemStack, crate);
    }

    private static boolean isSimilarCustom(ItemStack one, ItemStack two) {
        if (one != null && two != null) {
            if (one.getType() == two.getType()) {
                if (one.hasItemMeta() && two.hasItemMeta()) {
                    ItemMeta itemMetaOne = one.getItemMeta();
                    ItemMeta itemMetaTwo = two.getItemMeta();

                    if (itemMetaOne.hasDisplayName() && itemMetaTwo.hasDisplayName()) {
                        if (itemMetaOne.getDisplayName().equalsIgnoreCase(itemMetaTwo.getDisplayName())) {
                            if (itemMetaOne.hasLore() && itemMetaTwo.hasLore()) {
                                if (itemMetaOne.getLore().size() == itemMetaTwo.getLore().size()) {
                                    int i = 0;

                                    for (String lore : itemMetaOne.getLore()) {
                                        if (!lore.equals(itemMetaTwo.getLore().get(i))) {
                                            return false;
                                        }

                                        i++;
                                    }

                                    return true;
                                }
                            } else {
                                return !itemMetaOne.hasLore() && !itemMetaTwo.hasLore();
                            }
                        }
                    } else if (!itemMetaOne.hasDisplayName() && !itemMetaTwo.hasDisplayName()) {
                        if (itemMetaOne.hasLore() && itemMetaTwo.hasLore()) {
                            if (itemMetaOne.getLore().size() == itemMetaTwo.getLore().size()) {
                                int i = 0;

                                for (String lore : itemMetaOne.getLore()) {
                                    if (!lore.equals(itemMetaTwo.getLore().get(i))) {
                                        return false;
                                    }

                                    i++;
                                }

                                return true;
                            } else {
                                return false;
                            }
                        } else {
                            return !itemMetaOne.hasLore() && !itemMetaTwo.hasLore();
                        }
                    }
                } else {
                    return !one.hasItemMeta() && !two.hasItemMeta();
                }
            }
        }

        return false;
    }

    public static String getKey(ItemMeta itemMeta) {
        return itemMeta.getPersistentDataContainer().get(PersistentKeys.crate_key.getNamespacedKey(), PersistentDataType.STRING);
    }
}