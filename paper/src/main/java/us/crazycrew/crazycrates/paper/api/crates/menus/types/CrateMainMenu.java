package us.crazycrew.crazycrates.paper.api.crates.menus.types;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.ItemBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.common.config.ConfigManager;
import us.crazycrew.crazycrates.common.config.types.Config;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.api.crates.menus.InventoryBuilder;
import java.text.NumberFormat;
import java.util.List;

public class CrateMainMenu extends InventoryBuilder {

    @NotNull
    private final ConfigManager configManager = getPlugin().getConfigManager();

    @NotNull
    private final SettingsManager config = this.configManager.getConfig();

    public CrateMainMenu(CrazyCrates plugin, Player player, int size, String title) {
        super(plugin, player, size, title);
    }

    @Override
    public InventoryBuilder build() {
        Inventory inventory = getInventory();

        if (this.config.getProperty(Config.filler_toggle)) {
            String id = this.config.getProperty(Config.filler_item);
            String name = this.config.getProperty(Config.filler_name);
            List<String> lore = this.config.getProperty(Config.filler_lore);
            ItemStack item = new ItemBuilder().setMaterial(id).setName(name).setLore(lore).build();

            for (int i = 0; i < getSize(); i++) {
                inventory.setItem(i, item.clone());
            }
        }

        if (this.config.getProperty(Config.gui_customizer_toggle)) {
            List<String> customizer = this.config.getProperty(Config.gui_customizer);

            if (!customizer.isEmpty()) {
                for (String custom : customizer) {
                    int slot = 0;
                    ItemBuilder item = new ItemBuilder();
                    String[] split = custom.split(", ");

                    for (String option : split) {

                        if (option.contains("Item:")) item.setMaterial(option.replace("Item:", ""));

                        if (option.contains("Name:")) {
                            option = option.replace("Name:", "");

                            option = getCrates(option);

                            item.setName(option.replaceAll("%player%", getPlayer().getName()));
                        }

                        if (option.contains("Lore:")) {
                            option = option.replace("Lore:", "");
                            String[] lore = option.split(",");

                            for (String line : lore) {
                                option = getCrates(option);

                                item.addLore(option.replaceAll("%player%", getPlayer().getName()));
                            }
                        }

                        if (option.contains("Glowing:")) item.setGlow(Boolean.parseBoolean(option.replace("Glowing:", "")));

                        if (option.contains("Player:")) item.setPlayerName(option.replaceAll("%player%", getPlayer().getName()));

                        if (option.contains("Slot:")) slot = Integer.parseInt(option.replace("Slot:", ""));

                        if (option.contains("Unbreakable-Item")) item.setUnbreakable(Boolean.parseBoolean(option.replace("Unbreakable-Item:", "")));

                        if (option.contains("Hide-Item-Flags")) item.hideItemFlags(Boolean.parseBoolean(option.replace("Hide-Item-Flags:", "")));
                    }

                    if (slot > getSize()) continue;

                    slot--;
                    inventory.setItem(slot, item.build());
                }
            }
        }

        for (Crate crate : getPlugin().getCrateManager().getCrates()) {
            FileConfiguration file = crate.getFile();

            if (file != null) {
                if (file.getBoolean("Crate.InGUI")) {
                    String path = "Crate.";
                    int slot = file.getInt(path + "Slot");

                    if (slot > getSize()) continue;

                    slot--;
                    inventory.setItem(slot, new ItemBuilder()
                            .setMaterial(file.getString(path + "Item"))
                            .setName(file.getString(path + "Name"))
                            .setLore(file.getStringList(path + "Lore"))
                            .setCrateName(crate.getName())
                            .setPlayerName(file.getString(path + "Player"))
                            .setGlow(file.getBoolean(path + "Glowing"))
                            .addLorePlaceholder("%Keys%", NumberFormat.getNumberInstance().format(getPlugin().getCrazyHandler().getUserManager().getVirtualKeys(getPlayer().getUniqueId(), crate.getName())))
                            .addLorePlaceholder("%Keys_Physical%", NumberFormat.getNumberInstance().format(getPlugin().getCrazyHandler().getUserManager().getPhysicalKeys(getPlayer().getUniqueId(), crate.getName())))
                            .addLorePlaceholder("%Keys_Total%", NumberFormat.getNumberInstance().format(getPlugin().getCrazyHandler().getUserManager().getTotalKeys(getPlayer().getUniqueId(), crate.getName())))
                            .addLorePlaceholder("%crate_opened%", NumberFormat.getNumberInstance().format(getPlugin().getCrazyHandler().getUserManager().getCrateOpened(getPlayer().getUniqueId(), crate.getName())))
                            .addLorePlaceholder("%Player%", getPlayer().getName())
                            .build());
                }
            }
        }

        return this;
    }

    private String getCrates(String option) {
        for (Crate crate : getPlugin().getCrateManager().getCrates()) {
            if (crate.getCrateType() != CrateType.menu) {
                option = option.replaceAll("%" + crate.getName().toLowerCase() + "%", getPlugin().getCrazyHandler().getUserManager().getVirtualKeys(getPlayer().getUniqueId(), crate.getName()) + "")
                        .replaceAll("%" + crate.getName().toLowerCase() + "_physical%", getPlugin().getCrazyHandler().getUserManager().getPhysicalKeys(getPlayer().getUniqueId(), crate.getName()) + "")
                        .replaceAll("%" + crate.getName().toLowerCase() + "_total%", getPlugin().getCrazyHandler().getUserManager().getTotalKeys(getPlayer().getUniqueId(), crate.getName()) + "")
                        .replaceAll("%" + crate.getName().toLowerCase() + "_opened%", getPlugin().getCrazyHandler().getUserManager().getCrateOpened(getPlayer().getUniqueId(), crate.getName()) + "");
            }
        }

        return option;
    }
}