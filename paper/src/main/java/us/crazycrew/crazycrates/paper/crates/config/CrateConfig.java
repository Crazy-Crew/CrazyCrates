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

    public String getCrateType() {
        return getString("Crate.CrateType", "CSGO");
    }

    public String getCrateName() {
        return getString("Crate.CrateName", "");
    }

    public String getPreviewName() {
        return getString("Crate.Preview-Name", "");
    }

    public int getStartingKeys() {
        return getInt("Crate.StartingKeys", 0);
    }

    public int getRequiredKeys() {
        return getInt("Crate.RequiredKeys", 0);
    }

    public int getMaxMassOpen() {
        return getInt("Crate.Max-Mass-Open", 10);
    }

    public boolean isCrateInMenu() {
        return getBoolean("Crate.InGUI", true);
    }

    public int getCrateSlot() {
        return getInt("Crate.Slot", 1);
    }

    public boolean isOpeningBroadcast() {
        return getBoolean("Crate.OpeningBroadCast", false);
    }

    public String getOpeningBroadcast() {
        return getString("Crate.BroadCast", "");
    }

    public List<String> getPrizeMessages() {
        List<String> message = getStringList("Crate.Prize-Message");

        if (message.isEmpty()) return Collections.emptyList();

        return message;
    }

    public String getCrateItem() {
        return getString("Crate.Item", "CHEST");
    }

    public boolean isCrateItemGlowing() {
        return getBoolean("Crate.Glowing");
    }

    public String getCrateItemName() {
        return getString("Crate.Name", "");
    }

    public List<String> getCrateItemLore() {
        List<String> message = getStringList("Crate.Lore");

        if (message.isEmpty()) return Collections.emptyList();

        return message;
    }

    public boolean isPreviewEnabled() {
        return getBoolean("Crate.Preview.Toggle", true);
    }

    public int getPreviewRows() {
        return getInt("Crate.ChestLines", 6);
    }

    public boolean isPreviewGlassEnabled() {
        return getBoolean("Crate.Preview.Glass.Toggle", true);
    }

    public String getPreviewGlassName() {
        return getString("Crate.Preview.Glass.Name", "");
    }

    public String getPreviewGlassType() {
        return getString("Crate.Preview.Glass.Item", "TRIPWIRE_HOOK");
    }

    public String getPhysicalKeyName() {
        return getString("Crate.PhysicalKey.Name", "");
    }

    public List<String> getPhysicalKeyLore() {
        List<String> message = getStringList("Crate.PhysicalKey.Lore");

        if (message.isEmpty()) return Collections.emptyList();

        return message;
    }

    public String getPhysicalKeyItem() {
        return getString("Crate.PhysicalKey.Item");
    }

    public boolean isPhysicalKeyGlowing() {
        return getBoolean("Crate.PhysicalKey.Glowing", false);
    }

    public boolean isHologramEnabled() {
        return getBoolean("Crate.Hologram.Toggle", false);
    }

    public double getHologramHeight() {
        return getDouble("Crate.Hologram.Height", 1.5);
    }

    public List<String> getHologramMessage() {
        List<String> message = getStringList("Crate.Hologram.Message");

        if (message.isEmpty()) return Collections.emptyList();

        return message;
    }
}