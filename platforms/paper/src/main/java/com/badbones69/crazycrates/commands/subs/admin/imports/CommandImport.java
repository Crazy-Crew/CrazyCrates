package com.badbones69.crazycrates.commands.subs.admin.imports;

import com.badbones69.crazycrates.api.objects.ItemBuilder;
import com.badbones69.crazycrates.commands.enums.CrateImportOptions;
import com.badbones69.crazycrates.commands.subs.CommandManager;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionDefault;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandImport extends CommandManager {

    @SubCommand("import")
    @Permission(value = "crazycrates.command.admin.import", def = PermissionDefault.OP)
    public void importCrates(CommandSender sender, @Suggestion("import-options") CrateImportOptions crateImportOptions) {
        switch (crateImportOptions.getName()) {
            case "advanced_crates" -> {
                File advancedCratesDir = new File(plugin.getServer().getPluginsFolder() + "/AdvancedCrates");

                File advancedCratesOldDir = new File(advancedCratesDir + "/Crates");
                File crazyCratesDirectory = new File(plugin.getDataFolder() + "/crates");

                if (advancedCratesDir.exists()) {
                    for (File file : Objects.requireNonNull(advancedCratesOldDir.listFiles())) {

                        // The file to import.
                        File oldFile = new File(advancedCratesOldDir + "/" + file.getName());

                        // Create the new files in our crates directory.
                        File newFile = new File(crazyCratesDirectory + "/" + file.getName());

                        if (!newFile.exists()) {
                            try {
                                newFile.createNewFile();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        if (oldFile.exists()) {
                            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(oldFile);

                            // This is the fancy name of the crate.
                            String crateName = configuration.getString("Name");

                            List<String> crateHolo = configuration.getStringList("RewardHologram");

                            // This is the lores for the crate lore in /crates
                            List<String> crateLore = configuration.getStringList("Lores");

                            String cratePreviewName = configuration.getString("RewardsPreviewGUITitle");
                            boolean cratePreviewEnabled = configuration.getBoolean("RewardPreviewEnabled");

                            // Key shit.
                            String crateKeyName = configuration.getString("KeyCrate.Name");
                            List<String> crateKeyLore = configuration.getStringList("KeyCrate.Lores");
                            String crateKeyItem = configuration.getString("KeyCrate.Material");
                            boolean crateKeyGlowing = configuration.getBoolean("KeyCrate.Glow");
                            String crateKeyModelData = configuration.getString("KeyCrate.CustomModelData");

                            YamlConfiguration crazyConfig = YamlConfiguration.loadConfiguration(newFile);

                            configuration.getConfigurationSection("Prizes").getKeys(false).forEach(prize -> {
                                int prizeNumber = 0;

                                String path = "Prizes." + prize;

                                String material = configuration.getString(path + ".Material");
                                String name = configuration.getString(path +  ".Name");
                                String nbt = configuration.getString(path +  ".NBTtags");
                                String base64 = configuration.getString(path + ".Base64Texture");
                                int customModelData = configuration.getInt(path + ".CustomModelData");

                                List<String> blackListedPerms = configuration.getStringList(path + ".BlacklistedPerms");

                                List<String> playerMessages = configuration.getStringList(path + ".MessagesToPlayer");

                                List<String> broadcasts = configuration.getStringList(path + ".BroadcastMessagesToPlayers");

                                List<String> commands = configuration.getStringList(path +  ".Commands");

                                List<String> enchantments = configuration.getStringList(path +  ".Enchantments");

                                List<String> itemFlags = configuration.getStringList(path + ".ItemFlags");
                                List<String> lore = configuration.getStringList(path +  ".PreviewLores");

                                int chance = (int) configuration.getDouble(path +  ".Chance");

                                int amount = configuration.getInt(path +  ".Amount");

                                boolean glowing = configuration.getBoolean(path +  ".Glow");

                                prizeNumber++;

                                String crazyPrize = "Crate.Prizes." + prizeNumber;

                                if (configuration.contains(path + ".Material")) {
                                    if (material != null) {
                                        ItemBuilder itemStack = new ItemBuilder()
                                                .setMaterial(material)
                                                .setName(name)
                                                .setLore(lore)
                                                .setAmount(amount);

                                        if (configuration.contains(path + ".NBTtags")) {
                                            if (nbt != null) {

                                            }
                                        } else {
                                            itemStack.setEnchantments(getEnchantments(enchantments));
                                        }

                                        if (configuration.contains(path + ".CustomModelData")) itemStack.setCustomModelData(customModelData);

                                        //addEditorItem(
                                        //        configuration,
                                        //        String.valueOf(prizeNumber),
                                        //        itemStack.build(),
                                        //        chance,
                                        //        base64,
                                        //        customModelData);

                                        //Player player = (Player) sender;

                                        //player.getWorld().dropItem(player.getLocation(), itemStack.build());
                                    }
                                }

                                // Lists
                                if (configuration.contains(path + ".ItemFlags") && !itemFlags.isEmpty()) {
                                    //crazyConfig.set(crazyPrize + ".Flags", itemFlags);
                                }

                                if (configuration.contains(path + ".BlacklistedPerms") && !blackListedPerms.isEmpty()) {
                                    //crazyConfig.set(crazyPrize + ".BlackListed-Permissions", blackListedPerms);
                                }

                                //if (configuration.contains(path + ".Enchantments") && !enchantments.isEmpty()) {
                                    //enchantments.forEach(enchant -> {
                                    //    crazyConfig.set(crazyPrize + ".DisplayEnchantments", enchant.replaceAll(";", ":"));
                                    //});
                                //}

                                if (configuration.contains(path + ".Commands") && !commands.isEmpty()) {
                                    //crazyConfig.set(crazyPrize + ".Commands", commands);
                                }

                                if (configuration.contains(path + ".MessagesToPlayer") && !lore.isEmpty()) {
                                    //crazyConfig.set(crazyPrize + ".DisplayLore", lore);
                                }

                                if (configuration.contains(path + ".MessagesToPlayer") && !playerMessages.isEmpty()) {
                                    //crazyConfig.set(crazyPrize + ".Messages", playerMessages);
                                }

                                if (configuration.contains(path + ".BroadcastMessagesToPlayers") && !broadcasts.isEmpty()) {
                                    //broadcasts.forEach(line -> crazyConfig.set(crazyPrize + ".Commands", "broadcast " + line));
                                }

                                // Booleans
                                if (configuration.contains(path + ".Glow") && String.valueOf(glowing).isEmpty()) {
                                    //crazyConfig.set(crazyPrize + ".Glowing", glowing);
                                }

                                // Strings
                                //if (name != null) {
                                //    //crazyConfig.set(crazyPrize + ".DisplayName", name);
                                //}

                                //if (base64 != null) {
                                //    crazyConfig.set(crazyPrize + ".Player", base64);

                                //    crazyConfig.set(crazyPrize + ".DisplayItem", "PLAYER_HEAD");
                                //} else {
                                //    if (configuration.contains(path + ".CustomModelData") != null) {
                                //        crazyConfig.set(crazyPrize + ".DisplayItem", material + "#" + customModelData);
                                //    } else {
                                //        crazyConfig.set(crazyPrize + ".DisplayItem", material);
                                //    }
                                //}

                                // Integers
                                //if (configuration.contains(path + ".Chance") != null) {
                                //    //crazyConfig.set(crazyPrize + ".Chance", chance);
                                //}

                                //if (configuration.contains(path + ".Amount") != null) {
                                //    //crazyConfig.set(crazyPrize + ".DisplayAmount", amount);
                                //}

                                //crazyConfig.set(crazyPrize + ".MaxRange", 100);
                            });

                            crazyConfig.set("Crate.CrateName", Objects.requireNonNullElse(crateName, "Default Crate Name"));

                            if (configuration.contains("RewardHologram") && !crateHolo.isEmpty()) {
                                crazyConfig.set("Crate.Hologram.Message", crateHolo);
                            } else {
                                crazyConfig.set("Crate.Hologram.Message", List.of("Example Crate Hologram"));
                            }

                            if (configuration.contains("Lores") && !crateLore.isEmpty()) {
                                crazyConfig.set("Crate.Lore", crateLore);
                            } else {
                                crazyConfig.set("Crate.Lore", List.of("Example Crate Lore"));
                            }

                            crazyConfig.set("Crate.Preview.Name", Objects.requireNonNullElse(cratePreviewName, "Example Crate Name"));

                            if (configuration.contains("RewardPreviewEnabled")) {
                                crazyConfig.set("Crate.Preview.Toggle", cratePreviewEnabled);
                            } else {
                                crazyConfig.set("Crate.Preview.Toggle", true);
                            }

                            crazyConfig.set("Crate.PhysicalKey.Item", Objects.requireNonNullElse(crateKeyItem, "TRIPWIRE_HOOK"));

                            crazyConfig.set("Crate.PhysicalKey.Name", Objects.requireNonNullElse(crateKeyName, "Example Key Name"));

                            if (configuration.contains("KeyCrate.Lores") && !crateKeyLore.isEmpty()) {
                                crazyConfig.set("Crate.PhysicalKey.Lore", crateKeyLore);
                            } else {
                                crazyConfig.set("Crate.PhysicalKey.Lore", "Example Key Lore");
                            }

                            if (configuration.contains("KeyCrate.Glow")) {
                                crazyConfig.set("Crate.PhysicalKey.Glowing", crateKeyGlowing);
                            } else {
                                crazyConfig.set("Crate.PhysicalKey.Glowing", false);
                            }

                            // Options with nothing we can convert, or it's just impossible to do.
                            crazyConfig.set("Crate.Preview.ChestLines", 6);
                            crazyConfig.set("Crate.Preview.Glass.Toggle", true);
                            crazyConfig.set("Crate.Preview.Glass.Name", " ");
                            crazyConfig.set("Crate.Preview.Glass.Item", "PURPLE_STAINED_GLASS_PANE");

                            crazyConfig.set("Crate.CrateType", "CSGO");
                            crazyConfig.set("Crate.StartingKeys", 0);
                            crazyConfig.set("Crate.Max-Mass-Open", 10);
                            crazyConfig.set("Crate.InGUI", true);
                            crazyConfig.set("Crate.Slot", 10);
                            crazyConfig.set("Crate.OpeningBroadCast", true);
                            crazyConfig.set("Crate.BroadCast", "You should change this inside each crate file. BroadCast Option");
                            crazyConfig.set("Crate.Item", "ENDER_CHEST");
                            crazyConfig.set("Crate.Glowing", false);

                            crazyConfig.set("Crate.Hologram.Toggle", true);
                            crazyConfig.set("Crate.Hologram.Height", 1.5);

                            if (newFile.exists()) {
                                try {
                                    crazyConfig.save(newFile);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }

                    return;
                }

                sender.sendMessage("Can't import as " + advancedCratesDir.getName() + " does not exist.");
            }

            case "specialized_crates" -> {

            }
        }
    }
    
    private HashMap<Enchantment, Integer> getEnchantments(List<String> enchantments) {
        HashMap<Enchantment, Integer> hashMap = new HashMap<>();

        enchantments.forEach(enchant -> {
            String enchantName = enchant.substring(0, enchant.indexOf(";"));
            int level = Integer.parseInt(enchant.substring(enchant.indexOf(';') + 1));

            switch (enchantName.toLowerCase()) {
                case "protection_projectile" -> enchantName = "projectile_protection";
                case "protection_environmental" -> enchantName = "protection";
                case "protection_fall" -> enchantName = "feather_falling";
                case "protection_explosions" -> enchantName = "blast_protection";
                case "protection_fire" -> enchantName = "fire_protection";
                case "durability" -> enchantName = "unbreaking";
                case "damage_all" -> enchantName = "sharpness";
                case "arrow_infinite" -> enchantName = "infinity";
                case "arrow_damage" -> enchantName = "power";
                case "arrow_knockback" -> enchantName = "punch";
                case "loot_bonus_blocks" -> enchantName = "fortune";
                case "loot_bonus_mobs" -> enchantName = "looting";
                case "damage_undead" -> enchantName = "smite";
                case "damage_arthropods" -> enchantName = "bane_of_arthropods";
                case "dig_speed" -> enchantName = "efficiency";
                case "luck" -> enchantName = "luck_of_the_sea";
                case "water_worker" -> enchantName = "aqua_affinity";
                case "oxygen" -> enchantName = "respiration";
                case "sweeping_edge" -> enchantName = "sweeping";
            }

            NamespacedKey key = NamespacedKey.fromString(enchantName);

            if (key != null) hashMap.put(Enchantment.getByKey(key), level);
        });
        
        return hashMap;
    }

    private void addEditorItem(YamlConfiguration file, String prize, ItemStack item, int chance, String base64, int customModelData) {
        ArrayList<ItemStack> items = new ArrayList<>();
        items.add(item);
        String path = "Crate.Prizes." + prize;

        if (!file.contains(path)) {

            if (item.hasItemMeta()) {
                if (item.getItemMeta().hasDisplayName()) file.set(path + ".DisplayName", item.getItemMeta().getDisplayName());
                if (item.getItemMeta().hasLore()) file.set(path + ".Lore", item.getItemMeta().getLore());
            }

            NBTItem nbtItem = new NBTItem(item);

            if (nbtItem.hasNBTData()) {
                if (nbtItem.hasKey("Unbreakable") && nbtItem.getBoolean("Unbreakable")) file.set(path + ".Unbreakable", true);
            }

            List<String> enchantments = new ArrayList<>();

            for (Enchantment enchantment : item.getEnchantments().keySet()) {
                enchantments.add((enchantment.getKey().getKey() + ":" + item.getEnchantmentLevel(enchantment)));
            }

            if (!enchantments.isEmpty()) file.set(path + ".DisplayEnchantments", enchantments);

            if (base64 != null && !base64.isEmpty()) {
                file.set(prize + ".Player", base64);

                file.set(prize + ".DisplayItem", "PLAYER_HEAD");
            } else {
                if (file.contains(path + ".CustomModelData")) {
                    file.set(prize + ".DisplayItem", item.getType().name() + "#" + customModelData);
                } else {
                    file.set(prize + ".DisplayItem", item.getType().name());
                }
            }

            file.set(path + ".DisplayAmount", item.getAmount());
            file.set(path + ".MaxRange", 100);
            file.set(path + ".Chance", chance);
        } else {
            // Must be checked as getList will return null if nothing is found.
            if (file.contains(path + ".Editor-Items")) file.getList(path + ".Editor-Items").forEach(listItem -> items.add((ItemStack) listItem));
        }

        file.set(path + ".Editor-Items", items);
    }
}