package com.badbones69.crazycrates.commands.subs.admin.imports;

import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.objects.ItemBuilder;
import com.badbones69.crazycrates.commands.enums.CrateImportOptions;
import com.badbones69.crazycrates.commands.subs.CommandManager;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CommandImport extends CommandManager {

    @SubCommand("import")
    @Permission(value = "crazycrates.command.admin.import", def = PermissionDefault.OP)
    public void importCrates(CommandSender sender, @Suggestion("import-options") CrateImportOptions crateImportOptions) {
        switch (crateImportOptions.getName()) {
            case "advanced_crates" -> {
                File cratesFolder = new File(plugin.getServer().getPluginsFolder() + "/AdvancedCrates");

                if (cratesFolder.exists()) {
                    File cratesDir = new File(cratesFolder + "/Crates");

                    File myCratesDir = new File(plugin.getDataFolder() + "/crates");

                    for (File file : Objects.requireNonNull(cratesDir.listFiles())) {

                        File oldFile = new File(cratesDir + "/" + file.getName());

                        // Create the new files in our crates directory.
                        File newFile = new File(myCratesDir + "/" + file.getName());

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

                            String crateKeyName = configuration.getString("KeyCrate.Name");
                            List<String> crateKeyLore = configuration.getStringList("KeyCrate.Lores");

                            String crateKeyItem = configuration.getString("KeyCrate.Material");

                            boolean crateKeyGlowing = configuration.getBoolean("KeyCrate.Glow");

                            YamlConfiguration myConfig = YamlConfiguration.loadConfiguration(newFile);

                            configuration.getConfigurationSection("Prizes").getKeys(false).forEach(prize -> {
                                int prizeNumber = 0;
                                
                                String path = "Prizes." + prize;

                                String material = configuration.getString(path + ".Material");
                                boolean glowing = configuration.getBoolean(path +  ".Glow");
                                String name = configuration.getString(path +  ".Name");
                                List<String> lore = configuration.getStringList(path +  ".PreviewLores");

                                List<String> commands = configuration.getStringList(path +  ".Commands");

                                List<String> enchantments = configuration.getStringList(path +  ".Enchantments");

                                String nbt = configuration.getString(path +  ".NBTtags");

                                int chance = (int) configuration.getDouble(path +  ".Chance");

                                int amount = configuration.getInt(path +  ".Amount");

                                prizeNumber++;

                                String myPrize = "Crate.Prizes." + prizeNumber;
                                
                                if (material != null) {
                                    plugin.getLogger().severe(material);

                                    if (nbt != null) {
                                        if (!nbt.isEmpty()) {
                                            HashMap<Enchantment, Integer> hashMap = new HashMap<>();

                                            enchantments.forEach(enchant -> {
                                                String[] split = enchant.split(";");

                                                for (String value : split) {
                                                    boolean exists = isInteger(value);
                                                    String enchantment = "";
                                                    int level = 0;

                                                    if (exists) {
                                                        level = Integer.parseInt(value);
                                                    } else {
                                                        enchantment = value;
                                                    }

                                                    hashMap.put(Enchantment.getByKey(NamespacedKey.fromString(enchantment)), level);
                                                }
                                            });

                                            ItemStack itemStack = new ItemBuilder()
                                                    .setMaterial(material)
                                                    .setName(name)
                                                    .setLore(lore)
                                                    .setAmount(amount)
                                                    .setEnchantments(hashMap)
                                                    .build();

                                            //myConfig.set(myPrize + ".DisplayMaterial", material);
                                        }
                                    }

                                    myConfig.set(myPrize + ".DisplayMaterial", material);
                                }

                                if (String.valueOf(glowing).isEmpty()) {
                                    plugin.getLogger().severe(String.valueOf(glowing));

                                    //myConfig.set(myPrize + ".Glowing", glowing);
                                }

                                if (name != null) {
                                    plugin.getLogger().severe(name);

                                    //myConfig.set(myPrize + ".DisplayName", name);
                                }

                                if (!lore.isEmpty()) {
                                    plugin.getLogger().severe(lore.toString());

                                    //myConfig.set(myPrize + ".DisplayLore", lore);
                                }

                                if (!enchantments.isEmpty()) {
                                    plugin.getLogger().severe(enchantments.toString());

                                    //enchantments.forEach(enchant -> {
                                    //    myConfig.set(myPrize + ".DisplayEnchantments", enchant.replaceAll(";", ":"));
                                    //});
                                }

                                if (!commands.isEmpty()) {
                                    plugin.getLogger().severe(commands.toString());

                                    //myConfig.set(myPrize + ".Commands", commands);
                                }

                                if (!String.valueOf(chance).isEmpty()) {
                                    plugin.getLogger().severe(String.valueOf(chance));

                                    //myConfig.set(myPrize + ".Chance", chance);
                                }

                                if (!String.valueOf(amount).isEmpty()) {
                                    plugin.getLogger().severe(String.valueOf(amount));

                                    //myConfig.set(myPrize + ".DisplayAmount", amount);
                                }

                                //myConfig.set(myPrize + ".MaxRange", 100);
                            });

                            if (crateName != null) {
                                plugin.getLogger().warning(oldFile.getName() + " : Crate Name ->" + crateName);

                                myConfig.set("Crate.CrateName", crateName);
                            } else {
                                myConfig.set("Crate.CrateName", "Default Crate Name");
                            }

                            if (!crateHolo.isEmpty()) {
                                plugin.getLogger().warning(oldFile.getName() + " : Crate Holo ->" + crateHolo);

                                myConfig.set("Crate.Hologram.Message", crateHolo);
                            } else {
                                myConfig.set("Crate.Hologram.Message", List.of("Example Crate Hologram"));
                            }

                            if (!crateLore.isEmpty()) {
                                plugin.getLogger().warning(oldFile.getName() + " : Crate Lore ->" + crateLore);

                                myConfig.set("Crate.Lore", crateLore);
                            } else {
                                myConfig.set("Crate.Lore", List.of("Example Crate Lore"));
                            }

                            if (cratePreviewName != null) {
                                plugin.getLogger().warning(oldFile.getName() + " : Crate Preview Name ->" + cratePreviewName);

                                myConfig.set("Crate.Preview.Name", cratePreviewName);
                            } else {
                                myConfig.set("Crate.Preview.Name", "Example Crate Name");
                            }

                            if (!String.valueOf(cratePreviewEnabled).isEmpty()) {
                                plugin.getLogger().warning(oldFile.getName() + " : Crate Preview Enabled ->" + cratePreviewEnabled);

                                myConfig.set("Crate.Preview.Toggle", cratePreviewEnabled);
                            } else {
                                myConfig.set("Crate.Preview.Toggle", true);
                            }

                            if (crateKeyItem != null) {
                                myConfig.set("Crate.PhysicalKey.Item", crateKeyItem);
                            } else {
                                myConfig.set("Crate.PhysicalKey.Item", "TRIPWIRE_HOOK");
                            }

                            if (crateKeyName != null) {
                                plugin.getLogger().warning(oldFile.getName() + " : Crate Key Name ->" + crateKeyName);

                                myConfig.set("Crate.PhysicalKey.Name", crateKeyName);
                            } else {
                                myConfig.set("Crate.PhysicalKey.Name", "Example Key Name");
                            }

                            if (!crateKeyLore.isEmpty()) {
                                plugin.getLogger().warning(oldFile.getName() + " : Crate Key Lore ->" + crateKeyLore);

                                myConfig.set("Crate.PhysicalKey.Lore", crateKeyLore);
                            } else {
                                myConfig.set("Crate.PhysicalKey.Lore", "Example Key Lore");
                            }

                            if (!String.valueOf(crateKeyGlowing).isEmpty()) {
                                plugin.getLogger().warning(oldFile.getName() + " : Crate Key Glowing ->" + crateKeyGlowing);

                                myConfig.set("Crate.PhysicalKey.Glowing", crateKeyGlowing);
                            } else {
                                myConfig.set("Crate.PhysicalKey.Glowing", false);
                            }

                            // Options with nothing we can convert, or it's just impossible to do.
                            myConfig.set("Crate.Preview.ChestLines", 6);
                            myConfig.set("Crate.Preview.Glass.Toggle", true);
                            myConfig.set("Crate.Preview.Glass.Name", " ");
                            myConfig.set("Crate.Preview.Glass.Item", "PURPLE_STAINED_GLASS_PANE");

                            myConfig.set("Crate.CrateType", "CSGO");
                            myConfig.set("Crate.StartingKeys", 0);
                            myConfig.set("Crate.Max-Mass-Open", 10);
                            myConfig.set("Crate.InGUI", true);
                            myConfig.set("Crate.Slot", 10);
                            myConfig.set("Crate.OpeningBroadCast", true);
                            myConfig.set("Crate.BroadCast", "You should change this inside each crate file. BroadCast Option");
                            myConfig.set("Crate.Item", "ENDER_CHEST");
                            myConfig.set("Crate.Glowing", false);

                            myConfig.set("Crate.Hologram.Toggle", true);
                            myConfig.set("Crate.Hologram.Height", 1.5);

                            if (newFile.exists()) {
                                try {
                                    myConfig.save(newFile);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }

                        Bukkit.getLogger().warning("Set crate slot to 10. You will need to go into each file inside the crates folder and change this.");
                    }

                    return;
                }

                sender.sendMessage("Can't import as " + cratesFolder.getName() + " does not exist.");
            }

            case "specialized_crates" -> {

            }
        }
    }

    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException | NullPointerException e) {
            return false;
        }

        return true;
    }
}