package com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.types.plugins;

import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.api.enums.other.keys.FileKeys;
import com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.ICrateMigrator;
import com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.enums.MigrationType;
import com.ryderbelserion.fusion.api.enums.FileType;
import com.ryderbelserion.fusion.core.utils.AdvUtils;
import com.ryderbelserion.fusion.paper.files.LegacyCustomFile;
import com.ryderbelserion.fusion.paper.utils.ItemUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentcrates.CratesAPI;
import su.nightexpress.excellentcrates.crate.impl.Crate;
import su.nightexpress.excellentcrates.data.impl.CrateUser;
import su.nightexpress.excellentcrates.key.CrateKey;
import su.nightexpress.nightcore.config.FileConfig;
import us.crazycrew.crazycrates.api.users.UserManager;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ExcellentCratesMigrator extends ICrateMigrator {

    private final boolean ignoreCrates;

    public ExcellentCratesMigrator(final CommandSender sender, boolean ignoreCrates) {
        super(sender, MigrationType.EXCELLENT_CRATES);

        this.ignoreCrates = ignoreCrates;
    }

    @Override
    public void run() {
        final List<String> failed = new ArrayList<>();
        final List<String> success = new ArrayList<>();

        if (this.ignoreCrates) {
            final UserManager userManager = this.plugin.getUserManager();

            for (final CrateUser user : CratesAPI.getUserManager().getAllUsers()) {
                final String name = user.getName();

                try {
                    final UUID uuid = user.getId();

                    user.getKeysMap().forEach((key, amount) -> {
                        if (amount > 0) {
                            final YamlConfiguration data = FileKeys.data.getConfiguration();

                            final int keys = userManager.getVirtualKeys(uuid, crateName);

                            if (!data.contains("Players." + uuid + ".Name")) data.set("Players." + uuid + ".Name", name);

                            data.set("Players." + uuid + "." + key, (Math.max((keys + amount), 0)));

                            FileKeys.data.save();
                        }
                    });

                    success.add("<green>⤷ " + name);
                } catch (Exception exception) {
                    failed.add("<red>⤷ " + name);
                }
            }

            Messages.successfully_migrated_users.sendMessage(this.sender, new HashMap<>() {{
                put("{succeeded_amount}", String.valueOf(success.size()));
                put("{failed_amount}", String.valueOf(failed.size()));
                put("{type}", type.getName());
                put("{time}", time());
            }});

            return;
        }

        final File directory = getCratesDirectory();

        if (!directory.exists()) {
            directory.mkdirs();
        }

        final File crateDirectory = CratesAPI.PLUGIN.getDataFolder();

        YamlConfiguration locationData = FileKeys.locations.getConfiguration();

        final @NotNull Collection<Crate> crates = CratesAPI.getCrateManager().getCrates();

        if (crates.isEmpty()) {
            Messages.migration_no_crates_available.sendMessage(sender);

            return;
        }

        for (final Crate crate : crates) {
            final String crateName = crate.getFile().getName();
            final String strippedName = crateName.replace(".yml", "");

            final File crateFile = new File(directory, crateName);

            if (crateFile.exists()) {
                this.plugin.getComponentLogger().warn("Crate {} already exists in {}.", crateName, directory.getName());

                failed.add("<red>⤷ " + crateName);

                return;
            }

            try {
                crateFile.createNewFile();
            } catch (IOException exception) {
                this.plugin.getComponentLogger().warn("Failed to create crate file {} in {}.", crateName, directory.getName(), exception);

                failed.add("<red>⤷ " + crateName);
            }

            final LegacyCustomFile customFile = new LegacyCustomFile(FileType.YAML, crateFile, true).load();

            final YamlConfiguration configuration = customFile.getConfiguration();

            if (configuration == null) return;

            set(configuration, "Crate.CrateType", "CSGO");

            final ConfigurationSection root = configuration.getConfigurationSection("Crate");

            if (root == null) return;

            final FileConfig crateConfig = crate.getConfig();

            final List<String> locations = crateConfig.getStringList("Block.Locations");

            if (!locations.isEmpty()) {
                crateConfig.getStringList("Block.Locations").forEach(location -> {
                    String id = "1"; // Location ID

                    for (int i = 1; locationData.contains("Locations." + i); i++) {
                        id = (i + 1) + "";
                    }

                    String[] splitter = location.split(",");

                    String arg5 = splitter[5];
                    String arg0 = splitter[0];
                    String arg1 = splitter[1];
                    String arg2 = splitter[2];

                    locationData.set("Locations." + id + ".Crate", strippedName);
                    locationData.set("Locations." + id + ".World", arg5);
                    locationData.set("Locations." + id + ".X", (int) Double.parseDouble(arg0));
                    locationData.set("Locations." + id + ".Y", (int) Double.parseDouble(arg1));
                    locationData.set("Locations." + id + ".Z", (int) Double.parseDouble(arg2));

                    FileKeys.locations.save();
                });
            }

            set(root, "Glowing", false);

            final String name = crate.getPreviewConfig();

            final File file = new File(new File(crateDirectory, "menu"), name == null ? "default.yml" : name + ".yml");

            if (file.exists()) {
                final YamlConfiguration menuFile = YamlConfiguration.loadConfiguration(file);

                final String previewName = menuFile.getString("Crate.Name", "<bold><#9af7ff>%crate%</bold>").replace("%crate_name%", "%crate%").replace("%crate%", strippedName);

                final List<String> previewLore = new ArrayList<>();

                menuFile.getStringList("Crate.Lore").forEach(line -> previewLore.add(line.replaceAll("<l", "<").replaceAll("</l", "</")));

                set(root, "Name", AdvUtils.convert(previewName));
                set(root, "Lore", AdvUtils.convert(previewLore));

                final org.bukkit.configuration.ConfigurationSection section = menuFile.getConfigurationSection("Crate.Slots");

                if (section != null) {
                    for (String key : section.getKeys(false)) {
                        final int slot = section.getInt(key);

                        if (key.equalsIgnoreCase(crateName.replace(".yml", ""))) {
                            set(root, "InGUI", slot > 0);
                            set(root, "Slot", slot);

                            break;
                        }
                    }
                }
            }

            set(root, "Preview.Toggle", true);
            set(root, "Preview.ChestLines", 6);
            set(root, "Preview.Name", AdvUtils.convert(crate.getName()));
            set(root, "Preview.Glass.Toggle", true);
            set(root, "Preview.Glass.Name", " ");
            set(root, "Preview.Glass.Item", "gray_stained_glass_pane");
            set(root, "Preview.Glass.Custom-Model-Data", "");

            set(root, "Preview.Glass.Model.Namespace", "");
            set(root, "Preview.Glass.Model.Id", "");

            set(root, "StartingKeys", 0);
            set(root, "RequiredKeys", 0);
            set(root, "Max-Mass-Open", 10);

            set(root, "OpeningBroadCast", false);
            set(root, "BroadCast", "%prefix%<bold><gold>%player%</bold><reset> <gray>is opening a <bold><green>%crate%.</bold>".replace("%crate%", AdvUtils.convert(crate.getName())));

            set(root, "opening-command.toggle", false);
            set(root, "opening-command.commands", List.of("put your command here."));

            set(root, "sound.cycle-sound.toggle", false);
            set(root, "sound.cycle-sound.value", "block.note_block_xylophone");
            set(root, "sound.cycle-sound.volume", 1.0);
            set(root, "sound.cycle-sound.pitch", 1.0);

            set(root, "sound.click-sound.toggle", false);
            set(root, "sound.click-sound.value", "block.note_block_xylophone");
            set(root, "sound.click-sound.volume", 1.0);
            set(root, "sound.click-sound.pitch", 1.0);

            set(root, "sound.stop-sound.toggle", false);
            set(root, "sound.stop-sound.value", "block.note_block_xylophone");
            set(root, "sound.stop-sound.volume", 1.0);
            set(root, "sound.stop-sound.pitch", 1.0);

            set(root, "Prize-Message", List.of("<gray>You have won <red>%reward% <gray>from <red>%crate%."));

            final ItemStack crateItem = crate.getItem();

            set(root, "Item", crateConfig.getString("Item.Material", "player_head").toLowerCase());

            final String itemName = crateConfig.getString("Item.Name", "");

            set(root, "Preview-Name", AdvUtils.convert(itemName.isEmpty() ? crateConfig.getString("Name", "%crate%").replace("%crate%", strippedName) : itemName + " Preview"));

            if (crateItem.hasItemMeta()) {
                final ItemMeta itemMeta = crateItem.getItemMeta();

                set(root, "Custom-Model-Data", itemMeta.hasCustomModelData() ? itemMeta.getCustomModelData() : -1);
            }

            set(root, "Settings.Knockback", crate.isPushbackEnabled());

            set(root, "Hologram.Toggle", crate.isHologramEnabled());
            set(root, "Hologram.Height", crate.getHologramYOffset());
            set(root, "Hologram.Range", 8);
            set(root, "Hologram.Update-Interval", -1);
            set(root, "Hologram.Color", "transparent");

            List<String> hologramText = new ArrayList<>();
            crate.getHologramText().forEach(line -> {
                final String filtered = line.replace(
                        "%excellentcrates_keys_" + strippedName + "%",
                        "%crazycrates_" + strippedName + "%"
                ).replace(
                        "%crate%",
                        crate.getName()
                );

                hologramText.add(AdvUtils.convert(filtered));
            });

            set(root, "Hologram.Message", hologramText);

            final Optional<CrateKey> value = crate.getKeys().stream().findFirst();

            if (value.isPresent()) {
                final CrateKey key = value.get();

                final FileConfig config = key.getConfig();

                final ItemStack itemStack = key.getItem();

                set(root, "PhysicalKey.Data", ItemUtils.toBase64(itemStack));

                set(root, "PhysicalKey.Name", AdvUtils.convert(key.getName().replace("#", "#&")));
                set(root, "PhysicalKey.Item", itemStack.getType().getKey().getKey());

                set(root, "PhysicalKey.Lore", AdvUtils.convert(key.getConfig().getStringList("Lore")));

                set(root, "PhysicalKey.Glowing", config.contains("Item.Enchants"));
            }

            crate.getRewards().forEach(reward -> {
                // Get the id i.e. '1':
                final String id = reward.getId();

                final ItemStack itemStack = reward.getPreview();

                if (itemStack.hasItemMeta()) {
                    final ItemMeta itemMeta = itemStack.getItemMeta();

                    if (itemMeta.hasLore()) {
                        final List<Component> lore = itemMeta.lore();

                        if (lore != null) {
                            set(root, "Prizes." + id + ".DisplayLore", AdvUtils.fromComponent(lore));
                        }
                    }
                }

                set(root, "Prizes." + id + ".DisplayName", AdvUtils.convert(reward.getName().replace("#", "#&")));

                set(root, "Prizes." + id + ".Commands", reward.getCommands());

                set(root, "Prizes." + id + ".Settings.Broadcast.Toggle", reward.isBroadcast());
                set(root, "Prizes." + id + ".Settings.Broadcast.Messages", List.of());
                set(root, "Prizes." + id + ".Settings.Broadcast.Permission", "your_permission");

                set(root, "Prizes." + id + ".BlackListed-Permissions", new ArrayList<>(reward.getIgnoredForPermissions()));

                set(root, "Prizes." + id + ".Weight", (int) reward.getWeight());

                set(root, "Prizes." + id + ".DisplayItem", itemStack.getType().getKey().getKey());

                set(root, "Prizes." + id + ".DisplayAmount", itemStack.getAmount());

                List<String> enchantments = new ArrayList<>();

                for (Map.Entry<Enchantment, Integer> enchantment : itemStack.getEnchantments().entrySet()) {
                    enchantments.add(enchantment.getKey().getKey().getKey() + ":" + enchantment.getValue());
                }

                if (!enchantments.isEmpty()) set(root, "Prizes." + id + ".DisplayEnchantments", enchantments);

                final ConfigurationSection section = root.getConfigurationSection("Prizes");

                if (section == null) return;

                final ConfigurationSection prizeSection = section.getConfigurationSection(id);

                if (prizeSection == null) return;

                /*final boolean useNewItemEditor = this.config.getProperty(ConfigKeys.use_new_item_editor);

                reward.getItems().forEach(key -> {
                    if (useNewItemEditor) {
                        final String base64 = PaperMethods.toBase64(key);

                        if (prizeSection.contains("Items")) {
                            final List<String> items = prizeSection.getStringList("Items");

                        items.add("Data: " + base64);

                            set(prizeSection, "Items", items);
                        } else {
                            set(prizeSection, "Items", new ArrayList<>() {{
                                add("Data: " + base64);
                            }});
                        }
                    } else {
                        final List<ItemStack> editorItems = new ArrayList<>();

                        if (prizeSection.contains("Editor-Items")) {
                            final List<?> editors = prizeSection.getList("Editor-Items");

                            if (editors != null) {
                                editors.forEach(item -> editorItems.add((ItemStack) item));
                            }
                        }

                        editorItems.add(key);

                        set(prizeSection, "Editor-Items", editorItems);
                    }
                });*/
            });

            this.fileManager.addFile(customFile.save());

            success.add("<green>⤷ " + crateName);
        }

        // reload crates
        this.crateManager.loadHolograms();
        this.crateManager.loadCrates();

        final int convertedCrates = success.size();
        final int failedCrates = failed.size();

        sendMessage(new ArrayList<>(failedCrates + convertedCrates) {{
            addAll(failed);
            addAll(success);
        }}, convertedCrates, failedCrates);
    }

    @Override
    public <T> void set(final ConfigurationSection section, final String path, T value) {
        section.set(path, value);
    }

    @Override
    public final File getCratesDirectory() {
        return new File(this.plugin.getDataFolder(), "crates");
    }
}