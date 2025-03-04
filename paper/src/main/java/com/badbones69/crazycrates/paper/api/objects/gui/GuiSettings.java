package com.badbones69.crazycrates.paper.api.objects.gui;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.builders.LegacyItemBuilder;
import com.badbones69.crazycrates.paper.api.enums.FillerType;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import com.badbones69.crazycrates.paper.api.objects.gui.buttons.CrateButton;
import com.badbones69.crazycrates.paper.api.objects.gui.buttons.GuiButton;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import com.ryderbelserion.fusion.paper.builder.gui.interfaces.GuiItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import java.util.HashMap;
import java.util.Map;

public class GuiSettings {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final String title;
    private final int rows;
    private final int slot;

    private final boolean isFillerToggled;
    private final GuiItem fillerStack;
    private final FillerType fillerType;

    private final Map<Integer, GuiButton> buttons = new HashMap<>();

    private final Crate crate;
    private final Prize prize;

    public GuiSettings(final Crate crate, final Prize prize, final YamlConfiguration configuration) {
        this.title = configuration.getString("title", "<red>Do you want to re-spin?");
        this.rows = configuration.getInt("rows", 3);
        this.slot = configuration.getInt("slot", 5);

        this.isFillerToggled = configuration.getBoolean("filler.toggle", false);
        this.fillerType = FillerType.getFromName(configuration.getString("filler.fill-type", "border"));

        this.fillerStack = new LegacyItemBuilder()
                .withType(configuration.getString("filler.toggle.material", "red_stained_glass_pane"))
                .setDisplayName(configuration.getString("filler.toggle.name", " ")).asGuiItem();

        final ConfigurationSection staticButtons = configuration.getConfigurationSection("buttons.static");

        if (staticButtons != null) {
            staticButtons.getKeys(false).forEach(key -> {
                final ConfigurationSection button = staticButtons.getConfigurationSection(key);

                if (button == null) return;

                final int slot = button.getInt("slot");

                if (this.buttons.containsKey(slot)) {
                    if (MiscUtils.isLogging()) {
                        this.plugin.getComponentLogger().warn("Slot {} is taken, Try using another slot as we do not allow duplicates", slot);
                    }

                    return;
                }

                this.buttons.put(slot, new CrateButton(crate, prize, button));
            });
        }

        final ConfigurationSection customButtons = configuration.getConfigurationSection("buttons.custom");

        if (customButtons != null) {
            customButtons.getKeys(false).forEach(key -> {
                final ConfigurationSection button = customButtons.getConfigurationSection(key);

                if (button == null) return;

                final int slot = button.getInt("slot");

                if (this.buttons.containsKey(slot)) {
                    if (MiscUtils.isLogging()) {
                        this.plugin.getComponentLogger().warn("Slot {} is taken, Try using another slot as we do not allow duplicates", slot);
                    }

                    return;
                }

                this.buttons.put(slot, new CrateButton(crate, prize, button));
            });
        }

        this.crate = crate;
        this.prize = prize;
    }

    public final int getSlot() {
        return this.slot;
    }

    public final String getTitle() {
        return this.title;
    }

    public final int getRows() {
        return this.rows;
    }

    public final boolean isFillerToggled() {
        return this.isFillerToggled;
    }

    public final FillerType getFillerType() {
        return this.fillerType;
    }

    public final GuiItem getFillerStack() {
        return this.fillerStack;
    }

    public final Map<Integer, GuiButton> getButtons() {
        return this.buttons;
    }

    public final Crate getCrate() {
        return this.crate;
    }

    public final Prize getPrize() {
        return this.prize;
    }
}