package us.crazycrew.crazycrates.paper.crates.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class CrateConfig extends YamlConfiguration {

    private final File file;

    public CrateConfig(File file) {
        this.file = file;
    }

    public void load() throws IOException, InvalidConfigurationException {
        load(this.file);
    }

    public boolean isEnabled() {
        return getBoolean("crate.enabled", true);
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

    public int getCrateSlot() {
        return getInt("crate.gui.slot", 1);
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
}