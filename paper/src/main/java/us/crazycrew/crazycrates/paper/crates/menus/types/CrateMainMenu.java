package us.crazycrew.crazycrates.paper.crates.menus.types;

import ch.jalu.configme.SettingsManager;
import com.ryderbelserion.cluster.paper.items.NbtBuilder;
import com.ryderbelserion.cluster.paper.items.ParentBuilder;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.components.util.GuiFiller;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.inventory.ItemStack;
import us.crazycrew.crazycrates.common.config.types.menus.MainMenuConfig;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.crates.object.Crate;

import java.util.List;
import java.util.Set;

public class CrateMainMenu {

    private final CrazyCrates plugin;

    private final SettingsManager mainMenuConfig;
    private final Set<Crate> crateConfigs;

    public CrateMainMenu(CrazyCrates plugin) {
        this.plugin = plugin;

        this.mainMenuConfig = this.plugin.getConfigManager().getMainMenuConfig();

        this.crateConfigs = this.plugin.getCrazyHandler().getCrateManager().getCrates();
    }

    private Gui gui;

    public void create() {
        this.gui = Gui.gui()
                .title(this.plugin.getCrazyHandler().parse(this.mainMenuConfig.getProperty(MainMenuConfig.crate_menu_title)))
                .disableAllInteractions()
                .type(GuiType.valueOf(this.mainMenuConfig.getProperty(MainMenuConfig.crate_menu_type)))
                .rows(this.mainMenuConfig.getProperty(MainMenuConfig.crate_menu_rows))
                .create();

        if (this.mainMenuConfig.getProperty(MainMenuConfig.crate_menu_filler_toggle)) {
            String item = this.mainMenuConfig.getProperty(MainMenuConfig.crate_menu_filler_item);
            String name = this.mainMenuConfig.getProperty(MainMenuConfig.crate_menu_filler_name);
            List<String> lore = this.mainMenuConfig.getProperty(MainMenuConfig.crate_menu_filler_lore);

            GuiFiller guiFiller = this.gui.getFiller();

            ItemStack itemStack = ParentBuilder.of(this.plugin).setMaterial(item).setDisplayName(name).setDisplayLore(lore).build();

            GuiItem guiItem = new GuiItem(itemStack);

            guiFiller.fill(guiItem);
        }

        if (this.crateConfigs.isEmpty()) return;

        this.crateConfigs.forEach(crate -> {
            ItemStack crateItem = ParentBuilder.of(this.plugin).setMaterial(crate.getCrateItemType()).setDisplayLore(crate.getCrateItemLore()).setDisplayName(crate.getCrateItemName()).build();
            NbtBuilder nbtItem = new NbtBuilder(this.plugin, crateItem);
            nbtItem.setString("crazycrates_crate", crate.getFileName());

            GuiItem guiItem = ItemBuilder.from(nbtItem.getItemStack()).asGuiItem(event -> {
                NbtBuilder clickedItem = new NbtBuilder(this.plugin, event.getCurrentItem());

                String key = clickedItem.getString("crazycrates_crate");

                if (key.equalsIgnoreCase(crate.getFileName())) {
                    event.getWhoClicked().sendMessage(crate.getCrateName());
                }
            });

            gui.setItem(crate.getCrateMenuSlot(), guiItem);
        });
    }

    public Gui getGui() {
        return this.gui;
    }
}