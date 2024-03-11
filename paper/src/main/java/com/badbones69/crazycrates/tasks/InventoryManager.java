package com.badbones69.crazycrates.tasks;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.api.enums.PersistentKeys;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.builders.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.platform.config.ConfigManager;
import us.crazycrew.crazycrates.platform.config.impl.ConfigKeys;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unchecked")
public class InventoryManager {

    @NotNull
    private final SettingsManager config = ConfigManager.getConfig();

    private ItemBuilder menuButton;
    private ItemBuilder nextButton;
    private ItemBuilder backButton;

    public void loadButtons() {
        this.menuButton = new ItemBuilder()
                .setMaterial(this.config.getProperty(ConfigKeys.menu_button_item))
                .setName(this.config.getProperty(ConfigKeys.menu_button_name))
                .setLore(this.config.getProperty(ConfigKeys.menu_button_lore));

        ItemMeta menuMeta = this.menuButton.getItemMeta();

        PersistentDataContainer menuContainer = menuMeta.getPersistentDataContainer();

        PersistentKeys main_menu_button = PersistentKeys.main_menu_button;

        menuContainer.set(main_menu_button.getNamespacedKey(), main_menu_button.getType(), "none");

        this.menuButton.setItemMeta(menuMeta);

        this.nextButton = new ItemBuilder()
                .setMaterial(this.config.getProperty(ConfigKeys.next_button_item))
                .setName(this.config.getProperty(ConfigKeys.next_button_name))
                .setLore(this.config.getProperty(ConfigKeys.next_button_lore));

        ItemMeta nextMeta = this.nextButton.getItemMeta();

        PersistentDataContainer nextContainer = nextMeta.getPersistentDataContainer();

        PersistentKeys next_button = PersistentKeys.next_button;

        nextContainer.set(next_button.getNamespacedKey(), next_button.getType(), "none");

        this.nextButton.setItemMeta(nextMeta);

        this.backButton = new ItemBuilder()
                .setMaterial(this.config.getProperty(ConfigKeys.back_button_item))
                .setName(this.config.getProperty(ConfigKeys.back_button_name))
                .setLore(this.config.getProperty(ConfigKeys.back_button_lore));

        ItemMeta backMeta = this.backButton.getItemMeta();

        PersistentDataContainer backContainer = backMeta.getPersistentDataContainer();

        PersistentKeys back_button = PersistentKeys.back_button;

        backContainer.set(back_button.getNamespacedKey(), back_button.getType(), "none");

        this.backButton.setItemMeta(backMeta);
    }

    public ItemStack getMenuButton(Player player) {
        return this.menuButton.setTarget(player).build();
    }

    public ItemStack getNextButton(Player player) {
        ItemBuilder button = new ItemBuilder(this.nextButton);

        if (player != null) {
            button.addLorePlaceholder("{page}", (getPage(player) + 1) + "");
        }

        return button.setTarget(player).build();
    }

    public ItemStack getBackButton(Player player) {
        ItemBuilder button = new ItemBuilder(this.backButton);

        if (player != null) {
            button.addLorePlaceholder("{page}", (getPage(player) - 1) + "");
        }

        return button.setTarget(player).build();
    }

    private final Map<UUID, Crate> crateViewers = new HashMap<>();

    public void openNewCratePreview(Player player, Crate crate, boolean isTierPreview) {
        this.crateViewers.put(player.getUniqueId(), crate);

        if (isTierPreview) {
            player.openInventory(crate.getTierPreview(player));
            return;
        }

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

    private final Map<UUID, Integer> pageViewers = new HashMap<>();

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

    private final List<UUID> viewers = new ArrayList<>();

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