package com.badbones69.crazycrates.api.objects.gui;

import com.badbones69.crazycrates.api.builders.ItemBuilder;
import com.badbones69.crazycrates.api.enums.FillerType;
import com.badbones69.crazycrates.api.objects.Crate;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.GuiItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import java.util.HashMap;
import java.util.Map;

public class GuiSettings {

    private final String title;
    private final int rows;

    private final boolean isFillerToggled;
    private final GuiItem fillerStack;
    private final FillerType fillerType;

    private final Map<Integer, GuiButton> buttons = new HashMap<>();

    public GuiSettings(final Crate crate, final YamlConfiguration configuration) {
        this.title = configuration.getString("title", "<red>Do you want to re-spin?");
        this.rows = configuration.getInt("rows", 3);

        this.isFillerToggled = configuration.getBoolean("toggle", false);
        this.fillerType = FillerType.getFromName(configuration.getString("filler.fill-type", "border"));

        this.fillerStack = new ItemBuilder().withType(configuration.getString("filler.toggle.material", "red_stained_glass_pane")).setDisplayName(configuration.getString("filler.toggle.name", " ")).asGuiItem();

        final ConfigurationSection section = configuration.getConfigurationSection("buttons");

        if (section != null) {
            section.getKeys(false).forEach(key -> {
                final ConfigurationSection button = section.getConfigurationSection(key);

                if (button == null) return;

                final int slot = button.getInt("slot");

                this.buttons.put(slot, new GuiButton(crate, button));
            });
        }
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
}