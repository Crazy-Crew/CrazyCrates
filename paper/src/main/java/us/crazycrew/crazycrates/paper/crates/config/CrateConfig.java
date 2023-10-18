package us.crazycrew.crazycrates.paper.crates.config;

import com.ryderbelserion.cluster.paper.items.ItemBuilder;
import com.ryderbelserion.cluster.paper.items.ParentBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.crates.object.Prize;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CrateConfig extends YamlConfiguration {

    private final CrazyCrates plugin;
    private final File file;

    public CrateConfig(CrazyCrates plugin, File file) {
        this.plugin = plugin;

        this.file = file;
    }

    public void load() throws IOException, InvalidConfigurationException {
        load(this.file);
    }

    public boolean isEnabled() {
        return getBoolean("crate.enabled", true);
    }

    public File getFile() {
        return this.file;
    }

    public String getCrateType() {
        return getString("crate.type", "CSGO");
    }

    public String getCrateName() {
        return getString("crate.name", "");
    }

    public int getStartingKeys() {
        return getInt("crate.starting-keys", 0);
    }

    public int getRequiredKeys() {
        return getInt("crate.required-keys", 0);
    }

    public int getMaxMassOpen() {
        return getInt("crate.max-mass-open", 10);
    }

    public boolean isCrateInMenu() {
        return getBoolean("crate.gui.toggle", true);
    }

    public int getCrateRow() {
        return getInt("crate.gui.row", 1);
    }

    public int getCrateColumn() {
        return getInt("crate.gui.column", 1);
    }

    public String getCrateItem() {
        return getString("crate.gui.item", "CHEST");
    }

    public boolean isCrateItemGlowing() {
        return getBoolean("crate.gui.glowing");
    }

    public String getCrateItemName() {
        return getString("crate.gui.name", "");
    }

    public List<String> getCrateItemLore() {
        List<String> message = getStringList("crate.gui.lore");

        if (message.isEmpty()) return Collections.emptyList();

        return message;
    }

    public boolean isOpeningBroadcast() {
        return getBoolean("crate.broadcast.toggle", false);
    }

    public String getOpeningBroadcast() {
        return getString("crate.broadcast.message", "");
    }

    public boolean isPrizeMessageEnabled() {
        return getBoolean("crate.prize-message.toggle", false);
    }

    public List<String> getPrizeMessages() {
        List<String> message = getStringList("crate.prize-message.message");

        if (message.isEmpty()) return Collections.emptyList();

        return message;
    }


    public boolean isPreviewEnabled() {
        return getBoolean("crate.preview.toggle", true);
    }

    public String getPreviewName() {
        return getString("crate.preview.name", "");
    }

    public int getPreviewRows() {
        return getInt("crate.preview.rows", 6);
    }

    public boolean isPreviewGlassEnabled() {
        return getBoolean("crate.preview.glass.toggle", true);
    }

    public String getPreviewGlassName() {
        return getString("crate.preview.glass.name", "");
    }

    public String getPreviewGlassType() {
        return getString("crate.preview.glass.item", "GRAY_STAINED_GLASS_PANE");
    }

    public String getPhysicalKeyName() {
        return getString("crate.physical-key.name", "");
    }

    public List<String> getPhysicalKeyLore() {
        List<String> message = getStringList("crate.physical-key.lore");

        if (message.isEmpty()) return Collections.emptyList();

        return message;
    }

    public String getPhysicalKeyItem() {
        return getString("crate.physical-key.item");
    }

    public boolean isPhysicalKeyGlowing() {
        return getBoolean("crate.physical-key.glowing", false);
    }

    public boolean isHologramEnabled() {
        return getBoolean("crate.hologram.toggle", false);
    }

    public double getHologramHeight() {
        return getDouble("crate.hologram.height", 1.5);
    }

    public List<String> getHologramMessage() {
        List<String> message = getStringList("crate.hologram.message");

        if (message.isEmpty()) return Collections.emptyList();

        return message;
    }

    public List<Prize> getPrizes() {
        List<Prize> prizes = new ArrayList<>();

        ConfigurationSection section = getConfigurationSection("crate.prizes");

        if (section == null) return prizes;

        Set<String> keys = section.getKeys(false);

        for (String key : keys) {
            String path = "crate.prizes." + key + ".";

            // These values are not optional when building the display item.
            String displayItem = getString(path + "display-item");

            Material material;

            if (displayItem != null && !displayItem.isBlank()) {
                material = Material.matchMaterial(displayItem);
            } else {
                List.of(
                        "An issue with " + key + "'s material has been found.",
                        displayItem + " is not a valid material."
                ).forEach(line -> this.plugin.getLogger().warning(line));

                material = Material.STONE;
            }

            String displayName = getString(path + "display-name", material != null ? material.name() : "<red>An error has occurred with " + key + "'s material.");

            int displayAmount = getInt(path + "display-amount", 1);

            List<String> displayLore = !getStringList(path + "display-lore").isEmpty() ? getStringList(path + "display-lore") : Collections.emptyList();

            // These values are considered optional and can be removed from the configuration. We will add contain checks for these.
            String displayPlayer = getString(path + "player");

            // These values are not optional for how the crate should function.
            int maxRange = getInt(path + "max-range");
            int chance = getInt(path + "chance");

            int slot = getInt(path + "slot");

            // These values aren't optional, but I plan to only add a warning if both `items` and `commands` are empty.
            List<String> items = getStringList(path + "items");
            List<String> commands = getStringList(path + "commands");

            // Messages is completely optional, We will add a contains check to this and an isEmpty check just in case.
            List<String> messages = getStringList(path + "messages");

            // This section is optional, We will add a contains check and an isEmpty check just in case.
            List<String> blacklistedPermissions = getStringList(path + "blacklisted-permissions");

            boolean isAlternativePrizeEnabled = getBoolean(path + "alternative-prize.toggle", false);

            // These values aren't optional, but I plan to only add a warning if both `items` and `commands` are empty.
            List<String> alternativeItems = getStringList(path + "alternative-prize.items");
            List<String> alternativeCommands = getStringList(path + "alternative-prize.commands");

            // Messages is completely optional, We will add a contains check to this and an isEmpty check just in case.
            List<String> alternativeMessages = getStringList(path + "alternative-prize.messages");

            ItemBuilder builder = ParentBuilder.of(this.plugin).setMaterial(material).setDisplayName(displayName).setAmount(displayAmount).setDisplayLore(displayLore);

            Prize alternativePrize = null;

            if (contains(path + "alternative-prize")) {
                alternativePrize = new Prize(alternativeItems, alternativeCommands, alternativeMessages, isAlternativePrizeEnabled);
            }

            Prize prize = new Prize(builder.build(), maxRange, chance, slot, items, commands, messages, displayPlayer, blacklistedPermissions, alternativePrize);

            prizes.add(prize);
        }

        return prizes;
    }
}