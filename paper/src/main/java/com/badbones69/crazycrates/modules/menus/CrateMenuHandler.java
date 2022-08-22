package com.badbones69.crazycrates.modules.menus;

import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.modules.config.files.menus.CrateMenuConfig;
import com.google.inject.Inject;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class CrateMenuHandler {

    @Inject private CrazyManager crazyManager;
    @Inject private Methods methods;

    private final Gui gui = Gui.gui()
            .title(Component.text(""))
            .type(CrateMenuConfig.CRATE_MENU_TYPE)
            .rows(CrateMenuConfig.CRATE_MENU_SIZE)
            .disableAllInteractions()
            .create();

    GuiItem guiItem = ItemBuilder.from(Material.ITEM_FRAME).asGuiItem(event -> {

    });

    public void openGUI(Player player) {
        gui.open(player);
    }
}