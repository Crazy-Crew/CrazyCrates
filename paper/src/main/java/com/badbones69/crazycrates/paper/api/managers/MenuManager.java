package com.badbones69.crazycrates.paper.api.managers;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.CrazyManager;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.ItemBuilder;
import com.badbones69.crazycrates.paper.api.users.BukkitUserManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.common.config.ConfigManager;
import us.crazycrew.crazycrates.common.config.types.Config;
import us.crazycrew.crazycrates.common.config.types.menus.CrateMainMenu;
import us.crazycrew.crazycrates.common.config.types.menus.CratePreviewMenu;
import us.crazycrew.crazycrates.paper.api.plugin.CrazyHandler;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MenuManager {

    private final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);
    private final @NotNull CrazyHandler crazyHandler = this.plugin.getCrazyHandler();
    private final @NotNull CrazyManager crazyManager = this.crazyHandler.getCrazyManager();
    private final @NotNull BukkitUserManager userManager = this.crazyHandler.getUserManager();

    private final @NotNull ConfigManager configManager = this.crazyHandler.getConfigManager();
    private final @NotNull SettingsManager config = this.configManager.getConfig();
    private final @NotNull SettingsManager mainMenuConfig = this.configManager.getMainMenuConfig();
    private final @NotNull SettingsManager previewMenuConfig = this.configManager.getPreviewMenuConfig();

    private final HashMap<UUID, Integer> playerPage = new HashMap<>();
    private final HashMap<UUID, Crate> playerCrate = new HashMap<>();
    private final HashMap<UUID, Boolean> playerInMenu = new HashMap<>();
    private ItemStack menuButton;
    private ItemBuilder nextButton;
    private ItemBuilder backButton;

    public Map<UUID, Boolean> getPlayerInMenu() {
        return Collections.unmodifiableMap(this.playerInMenu);
    }

    public Map<UUID, Crate> getPlayerCrate() {
        return Collections.unmodifiableMap(this.playerCrate);
    }

    public Map<UUID, Integer> getPlayerPage() {
        return Collections.unmodifiableMap(this.playerPage);
    }

    public void loadButtons() {
        this.menuButton = new ItemBuilder()
                .setMaterial(this.previewMenuConfig.getProperty(CratePreviewMenu.crate_preview_menu_button_material))
                .setName(this.previewMenuConfig.getProperty(CratePreviewMenu.crate_preview_menu_button_name))
                .setLore(this.previewMenuConfig.getProperty(CratePreviewMenu.crate_preview_menu_button_lore))
                .build();
        this.nextButton = new ItemBuilder()
                .setMaterial(this.previewMenuConfig.getProperty(CratePreviewMenu.crate_preview_next_button_material))
                .setName(this.previewMenuConfig.getProperty(CratePreviewMenu.crate_preview_next_button_name))
                .setLore(this.previewMenuConfig.getProperty(CratePreviewMenu.crate_preview_next_button_lore));
        this.backButton = new ItemBuilder()
                .setMaterial(this.previewMenuConfig.getProperty(CratePreviewMenu.crate_preview_back_button_material))
                .setName(this.previewMenuConfig.getProperty(CratePreviewMenu.crate_preview_back_button_name))
                .setLore(this.previewMenuConfig.getProperty(CratePreviewMenu.crate_preview_back_button_lore));
    }

    public void openNewPreview(Player player, Crate crate) {
        this.playerCrate.put(player.getUniqueId(), crate);
        setPage(player.getUniqueId(), 1);

        player.openInventory(crate.getPreview(player));
    }

    public void openPreview(Player player, Crate crate) {
        UUID uuid = player.getUniqueId();

        this.playerCrate.put(uuid, crate);

        player.openInventory(crate.getPreview(player));
    }

    public int getPage(UUID uuid) {
        return this.playerPage.getOrDefault(uuid, 1);
    }

    public void setPage(UUID uuid, int pageNumber) {
        int max = this.playerCrate.get(uuid).getMaxPage();

        if (pageNumber < 1) {
            pageNumber = 1;
        } else if (pageNumber >= max) {
            pageNumber = max;
        }

        this.playerPage.put(uuid, pageNumber);
    }

    public ItemStack getMenuButton() {
        return this.menuButton;
    }

    public ItemStack getNextButton() {
        return getNextButton(null);
    }

    public ItemStack getNextButton(UUID uuid) {
        ItemBuilder button = new ItemBuilder(this.nextButton);

        button.addLorePlaceholder("{page}", (getPage(uuid) + 1) + "");

        return button.build();
    }

    public ItemStack getBackButton() {
        return getBackButton(null);
    }

    public ItemStack getBackButton(UUID uuid) {
        ItemBuilder button = new ItemBuilder(this.backButton);
        
        button.addLorePlaceholder("{page}", (getPage(uuid) - 1) + "");

        return button.build();
    }

    public boolean playerInMenu(Player player) {
        return this.playerInMenu.getOrDefault(player.getUniqueId(), false);
    }

    public void setPlayerInMenu(Player player, boolean inMenu) {
        this.playerInMenu.put(player.getUniqueId(), inMenu);
    }

    public void openMainMenu(Player player) {
        int size = this.mainMenuConfig.getProperty(CrateMainMenu.crate_menu_size);
        Inventory inv = this.plugin.getServer().createInventory(null, size, this.mainMenuConfig.getProperty(CrateMainMenu.crate_menu_title));

        if (!this.mainMenuConfig.getProperty(CrateMainMenu.crate_menu_filler_toggle)) {
            String id = this.mainMenuConfig.getProperty(CrateMainMenu.crate_menu_filler_item);
            String name = this.mainMenuConfig.getProperty(CrateMainMenu.crate_menu_filler_name);
            List<String> lore = this.mainMenuConfig.getProperty(CrateMainMenu.crate_menu_filler_lore);
            ItemStack item = new ItemBuilder().setMaterial(id).setName(name).setLore(lore).build();

            for (int i = 0; i < size; i++) {
                inv.setItem(i, item.clone());
            }
        }

        UUID uuid = player.getUniqueId();

        if (this.config.getProperty(Config.customizer_toggle)) {
            for (String custom : this.config.getProperty(Config.customizer_item_list)) {
                int slot = 0;
                ItemBuilder item = new ItemBuilder();
                String[] split = custom.split(", ");

                for (String option : split) {

                    if (option.contains("Item:")) item.setMaterial(option.replace("Item:", ""));

                    if (option.contains("Name:")) {
                        option = option.replace("Name:", "");

                        option = getCrates(player, option);

                        item.setName(option.replaceAll("\\{player}", player.getName()));
                    }

                    if (option.contains("Lore:")) {
                        option = option.replace("Lore:", "");
                        String[] d = option.split(",");

                        for (String ignored : d) {
                            option = getCrates(player, option);

                            item.addLore(option.replaceAll("\\{player}", player.getName()));
                        }
                    }

                    if (option.contains("Glowing:")) item.setGlow(Boolean.parseBoolean(option.replace("Glowing:", "")));

                    if (option.contains("Player:")) item.setPlayerName(option.replaceAll("\\{player}", player.getName()));

                    if (option.contains("Slot:")) slot = Integer.parseInt(option.replace("Slot:", ""));

                    if (option.contains("Unbreakable-Item"))
                        item.setUnbreakable(Boolean.parseBoolean(option.replace("Unbreakable-Item:", "")));

                    if (option.contains("Hide-Item-Flags"))
                        item.hideItemFlags(Boolean.parseBoolean(option.replace("Hide-Item-Flags:", "")));
                }

                if (slot > size) continue;

                slot--;
                inv.setItem(slot, item.build());
            }
        }

        for (Crate crate : this.crazyManager.getCrates()) {
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
                            .addLorePlaceholder("{keys}", NumberFormat.getNumberInstance().format(this.userManager.getVirtualKeys(uuid, crate.getName())))
                            .addLorePlaceholder("{keys_physical}", NumberFormat.getNumberInstance().format(this.userManager.getPhysicalKeys(uuid, crate.getName())))
                            .addLorePlaceholder("{keys_total}", NumberFormat.getNumberInstance().format(this.userManager.getTotalKeys(uuid, crate.getName())))
                            .addLorePlaceholder("{player}", player.getName())
                            .build());
                }
            }
        }

        player.openInventory(inv);
    }

    private String getCrates(Player player, String option) {
        UUID uuid = player.getUniqueId();

        for (Crate crate : this.crazyManager.getCrates()) {
            if (crate.getCrateType() != CrateType.MENU) {
                option = option.replaceAll("%" + crate.getName().toLowerCase() + "%", this.userManager.getVirtualKeys(uuid, crate.getName()) + "")
                        .replaceAll("%" + crate.getName().toLowerCase() + "_physical%", this.userManager.getPhysicalKeys(uuid, crate.getName()) + "")
                        .replaceAll("%" + crate.getName().toLowerCase() + "_total%", this.userManager.getTotalKeys(uuid, crate.getName()) + "");
            }
        }

        return option;
    }
}