package com.badbones69.crazycrates.modules.menus;

/*
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.modules.configuration.files.menus.PaperMenuConfig;
import com.badbones69.crazycrates.utilities.AdventureUtils;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.List;

public class CrateMenuHandler {

    private final CrazyManager crazyManager;

    private final AdventureUtils adventureUtils;

    public CrateMenuHandler(CrazyManager crazyManager, AdventureUtils adventureUtils) {
        this.crazyManager = crazyManager;
        this.adventureUtils = adventureUtils;
    }

    private final Gui gui = Gui.gui()
            .title(adventureUtils.parse(PaperMenuConfig.CRATE_MENU_TITLE))
            .type(GuiType.valueOf(PaperMenuConfig.CRATE_MENU_TYPE))
            .rows(PaperMenuConfig.CRATE_MENU_SIZE)
            .disableAllInteractions()
            .create();

    //TODO() This gui shit fucking weird. Sleepy time
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
 */