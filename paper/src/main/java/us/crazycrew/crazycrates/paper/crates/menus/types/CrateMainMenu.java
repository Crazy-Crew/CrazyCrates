package us.crazycrew.crazycrates.paper.crates.menus.types;

import com.ryderbelserion.cluster.paper.items.NbtBuilder;
import com.ryderbelserion.cluster.paper.items.ParentBuilder;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.inventory.ItemStack;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.crates.object.Crate;
import java.util.Set;

public class CrateMainMenu {

    private final CrazyCrates plugin;

    private final Set<Crate> crateConfigs;

    public CrateMainMenu(CrazyCrates plugin) {
        this.plugin = plugin;

        this.crateConfigs = this.plugin.getCrazyHandler().getCrateManager().getCrates();
    }

    private Gui gui;

    public void create() {
        this.gui = Gui.gui()
                .title(this.plugin.getCrazyHandler().parse("<red>Main Menu"))
                .disableAllInteractions()
                .rows(6)
                .create();

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