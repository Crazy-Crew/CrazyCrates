package us.crazycrew.crazycrates.paper.api.users.guis;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.ItemBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.common.config.types.Config;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.CrazyHandler;
import us.crazycrew.crazycrates.paper.utils.MsgUtils;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class InventoryManager {

    @NotNull
    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    @NotNull
    private final CrazyHandler crazyHandler = this.plugin.getCrazyHandler();

    @NotNull
    private final SettingsManager config = this.plugin.getConfigManager().getConfig();

    private ItemStack menuButton;
    private ItemBuilder nextButton;
    private ItemBuilder backButton;

    public void loadButtons() {
        this.menuButton = new ItemBuilder()
                .setMaterial(this.config.getProperty(Config.menu_button_item))
                .setName(this.config.getProperty(Config.menu_button_name))
                .setLore(this.config.getProperty(Config.menu_button_lore))
                .build();

        this.nextButton = new ItemBuilder()
                .setMaterial(this.config.getProperty(Config.next_button_item))
                .setName(this.config.getProperty(Config.next_button_name))
                .setLore(this.config.getProperty(Config.next_button_lore));
        this.backButton = new ItemBuilder()
                .setMaterial(this.config.getProperty(Config.back_button_item))
                .setName(this.config.getProperty(Config.back_button_name))
                .setLore(this.config.getProperty(Config.back_button_lore));
    }

    public ItemStack getMenuButton() {
        return this.menuButton;
    }

    public ItemStack getNextButton(Player player) {
        ItemBuilder button = new ItemBuilder(this.nextButton);

        if (player != null) button.addLorePlaceholder("%Page%", (getPage(player) + 1) + "");

        return button.build();
    }

    public ItemStack getBackButton(Player player) {
        ItemBuilder button = new ItemBuilder(this.backButton);

        if (player != null) button.addLorePlaceholder("%Page%", (getPage(player) - 1) + "");

        return button.build();
    }

    public void openGUI(Player player) {
        int size = this.config.getProperty(Config.inventory_size);
        Inventory inv = player.getServer().createInventory(null, size, MsgUtils.sanitizeColor(this.config.getProperty(Config.inventory_name)));

        if (this.config.getProperty(Config.filler_toggle)) {
            String id = this.config.getProperty(Config.filler_item);
            String name = this.config.getProperty(Config.filler_name);
            List<String> lore = this.config.getProperty(Config.filler_lore);
            ItemStack item = new ItemBuilder().setMaterial(id).setName(name).setLore(lore).build();

            for (int i = 0; i < size; i++) {
                inv.setItem(i, item.clone());
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

                            option = getCrates(player, option);

                            item.setName(option.replaceAll("%player%", player.getName()));
                        }

                        if (option.contains("Lore:")) {
                            option = option.replace("Lore:", "");
                            String[] lore = option.split(",");

                            for (String line : lore) {
                                option = getCrates(player, option);

                                item.addLore(option.replaceAll("%player%", player.getName()));
                            }
                        }

                        if (option.contains("Glowing:")) item.setGlow(Boolean.parseBoolean(option.replace("Glowing:", "")));

                        if (option.contains("Player:")) item.setPlayerName(option.replaceAll("%player%", player.getName()));

                        if (option.contains("Slot:")) slot = Integer.parseInt(option.replace("Slot:", ""));

                        if (option.contains("Unbreakable-Item")) item.setUnbreakable(Boolean.parseBoolean(option.replace("Unbreakable-Item:", "")));

                        if (option.contains("Hide-Item-Flags")) item.hideItemFlags(Boolean.parseBoolean(option.replace("Hide-Item-Flags:", "")));
                    }

                    if (slot > size) continue;

                    slot--;
                    inv.setItem(slot, item.build());
                }
            }
        }

        for (Crate crate : this.plugin.getCrateManager().getCrates()) {
            FileConfiguration file = crate.getFile();

            if (file != null) {
                if (file.getBoolean("Crate.InGUI")) {
                    String path = "Crate.";
                    int slot = file.getInt(path + "Slot");

                    if (slot > size) continue;

                    slot--;
                    inv.setItem(slot, new ItemBuilder()
                            .setMaterial(file.getString(path + "Item"))
                            .setName(file.getString(path + "Name"))
                            .setLore(file.getStringList(path + "Lore"))
                            .setCrateName(crate.getName())
                            .setPlayerName(file.getString(path + "Player"))
                            .setGlow(file.getBoolean(path + "Glowing"))
                            .addLorePlaceholder("%Keys%", NumberFormat.getNumberInstance().format(this.crazyHandler.getUserManager().getVirtualKeys(player.getUniqueId(), crate.getName())))
                            .addLorePlaceholder("%Keys_Physical%", NumberFormat.getNumberInstance().format(this.crazyHandler.getUserManager().getPhysicalKeys(player.getUniqueId(), crate.getName())))
                            .addLorePlaceholder("%Keys_Total%", NumberFormat.getNumberInstance().format(this.crazyHandler.getUserManager().getTotalKeys(player.getUniqueId(), crate.getName())))
                            .addLorePlaceholder("%crate_opened%", NumberFormat.getNumberInstance().format(this.crazyHandler.getUserManager().getCrateOpened(player.getUniqueId(), crate.getName())))
                            .addLorePlaceholder("%Player%", player.getName())
                            .build());
                }
            }
        }

        player.openInventory(inv);
    }

    private String getCrates(Player player, String option) {
        for (Crate crate : this.plugin.getCrateManager().getCrates()) {
            if (crate.getCrateType() != CrateType.menu) {
                option = option.replaceAll("%" + crate.getName().toLowerCase() + "%", this.crazyHandler.getUserManager().getVirtualKeys(player.getUniqueId(), crate.getName()) + "")
                        .replaceAll("%" + crate.getName().toLowerCase() + "_physical%", this.crazyHandler.getUserManager().getPhysicalKeys(player.getUniqueId(), crate.getName()) + "")
                        .replaceAll("%" + crate.getName().toLowerCase() + "_total%", this.crazyHandler.getUserManager().getTotalKeys(player.getUniqueId(), crate.getName()) + "")
                        .replaceAll("%" + crate.getName().toLowerCase() + "_opened%", this.crazyHandler.getUserManager().getCrateOpened(player.getUniqueId(), crate.getName()) + "");
            }
        }

        return option;
    }

    private final HashMap<UUID, Crate> crateViewers = new HashMap<>();

    public void openNewCratePreview(Player player, Crate crate) {
        this.crateViewers.put(player.getUniqueId(), crate);

        setPage(player, 1);
        player.openInventory(crate.getPreview(player));
    }

    public void addCrateViewer(Player player, Crate crate) {
        this.crateViewers.put(player.getUniqueId(), crate);
    }

    public void openCratePreview(Player player, Crate crate) {
        this.crateViewers.put(player.getUniqueId(), crate);

        player.openInventory(crate.getPreview(player));
    }

    public void closeCratePreview(Player player) {
        this.pageViewers.remove(player.getUniqueId());
        this.viewers.remove(player.getUniqueId());
        this.crateViewers.remove(player.getUniqueId());
        player.closeInventory();
    }

    public Crate getCratePreview(Player player) {
        return this.crateViewers.get(player.getUniqueId());
    }

    public void removeCrateViewer(Player player) {
        this.crateViewers.remove(player.getUniqueId());
    }

    public void removePageViewer(Player player) {
        this.pageViewers.remove(player.getUniqueId());
    }

    public boolean inCratePreview(Player player) {
        return this.crateViewers.containsKey(player.getUniqueId());
    }

    private final HashMap<UUID, Integer> pageViewers = new HashMap<>();

    public void nextPage(Player player) {
        setPage(player, getPage(player) + 1);
    }

    public void backPage(Player player) {
        setPage(player, getPage(player) - 1);
    }

    public int getPage(Player player) {
        return this.pageViewers.getOrDefault(player.getUniqueId(), 1);
    }

    public void setPage(Player player, int page) {
        int max = this.crateViewers.get(player.getUniqueId()).getMaxPage();

        if (page < 1) {
            page = 1;
        } else if (page >= max) {
            page = max;
        }

        this.pageViewers.put(player.getUniqueId(), page);
    }

    private final ArrayList<UUID> viewers = new ArrayList<>();

    public void addViewer(Player player) {
        this.viewers.add(player.getUniqueId());
    }

    public void removeViewer(Player player) {
        this.viewers.remove(player.getUniqueId());
    }

    public void purge() {
        this.viewers.clear();
    }

    public List<UUID> getViewers() {
        return Collections.unmodifiableList(this.viewers);
    }
}