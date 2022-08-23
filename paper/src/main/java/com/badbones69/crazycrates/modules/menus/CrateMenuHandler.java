package com.badbones69.crazycrates.modules.menus;

import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.modules.config.files.menus.CrateMenuConfig;
import com.badbones69.crazycrates.utilities.AdventureUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.List;

@Singleton
public class CrateMenuHandler {

    @Inject private CrazyManager crazyManager;
    @Inject private AdventureUtils adventureUtils;

    private final Gui gui = Gui.gui()
            .title(adventureUtils.parse(CrateMenuConfig.CRATE_MENU_TITLE))
            .type(CrateMenuConfig.CRATE_MENU_TYPE)
            .rows(CrateMenuConfig.CRATE_MENU_SIZE)
            .disableAllInteractions()
            .create();

    //TODO() This gui shit fucking weird. Sleepy time
    // We gotta just overhaul this, think very hard on it
    // also got to talk to Matt
    public void openGUI(Player player) {

        crazyManager.getCrates().forEach(crate -> {
            FileConfiguration file = crate.getFile();

            if (file == null) return;

            if (!file.getBoolean("Crate.InGUI")) return;

            String path = "Crate.";
            int slot = file.getInt(path + "Slot");

            String material = file.getString(path + "Item");
            String name = file.getString(path + "Name");
            List<String> lore = file.getStringList(path + "Lore");
            //String crateName = crate.getName();

            String playerName = file.getString(path + "Player");
            boolean isGlowing = file.getBoolean(path + "Glowing");

            assert material != null;
            Material matchedMaterial = Material.matchMaterial(material);

            if (matchedMaterial != null) {
                GuiItem guiItem = ItemBuilder.from(matchedMaterial)
                        .name(adventureUtils.parse(name))
                        .lore(adventureUtils.parse(lore.toString()))
                        .setNbt("CrazyCrates-" + crate.getName(), false)
                        .glow(isGlowing)
                        .asGuiItem(event -> {
                            //Crate crate = crazyManager.getCrateFromName("");
                        });

                gui.setItem(slot, guiItem);
            }
        });

        gui.open(player);
    }
}