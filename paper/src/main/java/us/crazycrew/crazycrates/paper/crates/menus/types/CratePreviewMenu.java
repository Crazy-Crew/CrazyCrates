package us.crazycrew.crazycrates.paper.crates.menus.types;

import ch.jalu.configme.SettingsManager;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.inventory.ItemStack;
import us.crazycrew.crazycrates.common.config.types.menus.PreviewMenuConfig;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.crates.object.Crate;

public class CratePreviewMenu {

    private final CrazyCrates plugin;

    private final Crate crate;

    private final SettingsManager previewMenuConfig;

    public CratePreviewMenu(CrazyCrates plugin, Crate crate) {
        this.plugin = plugin;

        this.crate = crate;

        this.previewMenuConfig = this.plugin.getConfigManager().getPreviewMenuConfig();
    }

    private Gui gui;

    //TODO() Add the ability to add dummy items.
    //TODO() Add an nbt tag to the prizes so the dummy items do nothing.
    //TODO() Add the ability to run commands/messages on dummy items for whatever reason.
    public void create() {
        this.gui = Gui.gui()
                .title(this.plugin.getCrazyHandler().parse(this.previewMenuConfig.getProperty(PreviewMenuConfig.crate_preview_menu_title).replaceAll("\\{crate}", crate.getCrateName())))
                .disableAllInteractions()
                .rows(6)
                .create();

        //TODO() Temporary buttons.
        //this.gui.setItem(6, 3, ItemBuilder.from(Material.PAPER).setName("Previous").asGuiItem(event -> this.gui.previous()));
        //this.gui.setItem(6, 7, ItemBuilder.from(Material.PAPER).setName("Next").asGuiItem(event -> this.gui.next()));

        this.crate.getPrizes().forEach(prize -> {
            ItemStack itemStack = prize.getDisplayItem();

            int slot = prize.getSlot();

            GuiItem guiItem = ItemBuilder.from(itemStack).asGuiItem();

            this.gui.setItem(slot, guiItem);
        });
    }

    public Gui getGui() {
        return this.gui;
    }
}