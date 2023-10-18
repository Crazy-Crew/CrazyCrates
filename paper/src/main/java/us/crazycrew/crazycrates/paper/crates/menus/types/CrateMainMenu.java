package us.crazycrew.crazycrates.paper.crates.menus.types;

import ch.jalu.configme.SettingsManager;
import com.ryderbelserion.cluster.paper.items.NbtBuilder;
import com.ryderbelserion.cluster.paper.items.ParentBuilder;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.components.util.GuiFiller;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
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

    private final Player player;

    public CrateMainMenu(CrazyCrates plugin, Player player) {
        this.plugin = plugin;

        this.player = player;

        this.mainMenuConfig = this.plugin.getConfigManager().getMainMenuConfig();

        this.crateConfigs = this.plugin.getCrazyHandler().getCrateManager().getCrates();

        this.plugin.getCrazyHandler().getGuiManager().addPlayer(this.player.getUniqueId(), this);
    }

    private Gui gui;

    public void create() {
        this.gui = Gui.gui()
                .title(this.plugin.getCrazyHandler().parse(this.mainMenuConfig.getProperty(MainMenuConfig.crate_menu_title)))
                .disableAllInteractions()
                .type(GuiType.valueOf(this.mainMenuConfig.getProperty(MainMenuConfig.crate_menu_type)))
                .rows(this.mainMenuConfig.getProperty(MainMenuConfig.crate_menu_rows))
                .create();

        this.gui.setCloseGuiAction(event -> this.plugin.getCrazyHandler().getGuiManager().removePlayer(event.getPlayer().getUniqueId()));

        if (this.mainMenuConfig.getProperty(MainMenuConfig.crate_menu_filler_toggle)) {
            if (!this.mainMenuConfig.getProperty(MainMenuConfig.crate_menu_filler_single_item)) {
                List<String> items = this.mainMenuConfig.getProperty(MainMenuConfig.crate_menu_filler_items);

                items.forEach(line -> {
                    String[] split = line.split(", ");

                    int row = 0;
                    int column = 0;

                    com.ryderbelserion.cluster.paper.items.ItemBuilder itemBuilder = ParentBuilder.of(this.plugin);

                    for (String option : split) {
                        if (option.contains("item:")) {
                            itemBuilder.setMaterial(option.replace("item:", ""));
                        }

                        if (option.contains("name:")) {
                            itemBuilder.setDisplayName(option.replace("name:", "").replaceAll("\\{player}", this.player.getName()));
                        }

                        if (option.contains("lore:")) {
                            option = option.replace("lore:", "");

                            String[] options = option.split(",");

                            for (String value : options) {
                                itemBuilder.addDisplayLore(value.replaceAll("\\{player}", this.player.getName()));
                            }
                        }

                        if (option.contains("glowing:")) {
                            itemBuilder.setGlowing(Boolean.parseBoolean(option.replace("glowing:", "")));
                        }

                        if (option.contains("player:")) {
                            itemBuilder.setPlayer(option.replaceAll("\\{player}", player.getName()));
                        }

                        if (option.contains("row:")) {
                            row = Integer.parseInt(option.replace("row:", ""));
                        }

                        if (option.contains("column:")) {
                            column = Integer.parseInt(option.replace("column:", ""));
                        }

                        if (option.contains("unbreakable-item")) {
                            itemBuilder.setUnbreakable(Boolean.parseBoolean(option.replace("unbreakable-item:", "")));
                        }

                        if (option.contains("hide-item-flags")) {
                            itemBuilder.hideItemFlags(Boolean.parseBoolean(option.replace("hide-item-flags", "")));
                        }
                    }

                    ItemStack itemStack = itemBuilder.build();

                    GuiItem guiItem = new GuiItem(itemStack);

                    this.gui.setItem(row, column, guiItem);
                });
            } else {
                String item = this.mainMenuConfig.getProperty(MainMenuConfig.crate_menu_filler_item);
                String name = this.mainMenuConfig.getProperty(MainMenuConfig.crate_menu_filler_name);
                List<String> lore = this.mainMenuConfig.getProperty(MainMenuConfig.crate_menu_filler_lore);

                GuiFiller guiFiller = this.gui.getFiller();

                ItemStack itemStack = ParentBuilder.of(this.plugin).setMaterial(item).setDisplayName(name).setDisplayLore(lore).build();

                GuiItem guiItem = new GuiItem(itemStack);

                guiFiller.fill(guiItem);
            }
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
                    // Create the object.
                    CratePreviewMenu cratePreviewMenu = new CratePreviewMenu(this.plugin, crate);

                    // Create the menu.
                    cratePreviewMenu.create();

                    // Open the menu.
                    cratePreviewMenu.getGui().open(event.getWhoClicked());
                }
            });

            this.gui.setItem(crate.getCrateMenuRow(), crate.getCrateMenuColumn(), guiItem);
        });
    }

    public Gui getGui() {
        return this.gui;
    }
}