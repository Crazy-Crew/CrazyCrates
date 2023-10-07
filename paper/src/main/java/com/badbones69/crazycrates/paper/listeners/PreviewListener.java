package com.badbones69.crazycrates.paper.listeners;

import com.badbones69.crazycrates.paper.api.FileManager.Files;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.ItemBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

public class PreviewListener implements Listener {
    
    private final HashMap<UUID, Integer> playerPage = new HashMap<>();
    private final HashMap<UUID, Crate> playerCrate = new HashMap<>();
    private final HashMap<UUID, Boolean> playerInMenu = new HashMap<>();
    private ItemStack menuButton;
    private ItemBuilder nextButton;
    private ItemBuilder backButton;

    public void loadButtons() {
        FileConfiguration config = Files.CONFIG.getFile();
        String path = "Settings.Preview.Buttons.";
        menuButton = new ItemBuilder()
        .setMaterial(config.getString(path + "Menu.Item", "COMPASS"))
        .setName(config.getString(path + "Menu.Name", "&7&l>> &c&lMenu &7&l<<"))
        .setLore(config.contains(path + "Menu.Lore") ? config.getStringList(path + "Menu.Lore") : Collections.singletonList("&7Return to the menu."))
        .build();
        nextButton = new ItemBuilder()
        .setMaterial(config.getString(path + "Next.Item", "FEATHER"))
        .setName(config.getString(path + "Next.Name", "&6&lNext >>"))
        .setLore(config.contains(path + "Next.Lore") ? config.getStringList(path + "Next.Lore") : Collections.singletonList("&7&lPage: &b%page%"));
        backButton = new ItemBuilder()
        .setMaterial(config.getString(path + "Back.Item", "FEATHER"))
        .setName(config.getString(path + "Back.Name", "&6&l<< Back"))
        .setLore(config.contains(path + "Back.Lore") ? config.getStringList(path + "Back.Lore") : Collections.singletonList("&7&lPage: &b%page%"));
    }
    
    public void openNewPreview(Player player, Crate crate) {
        playerCrate.put(player.getUniqueId(), crate);
        setPage(player, 1);
        player.openInventory(crate.getPreview(player));
    }
    
    public void openPreview(Player player) {
        player.openInventory(playerCrate.get(player.getUniqueId()).getPreview(player));
    }
    
    public void openPreview(Player player, Crate crate) {
        playerCrate.put(player.getUniqueId(), crate);
        player.openInventory(crate.getPreview(player));
    }
    
    public void openPreview(Player player, Crate crate, Integer page) {
        playerCrate.put(player.getUniqueId(), crate);
        player.openInventory(crate.getPreview(player));
    }
    
    public int getPage(Player player) {
        return playerPage.getOrDefault(player.getUniqueId(), 1);
    }
    
    public void setPage(Player player, int pageNumber) {
        int max = playerCrate.get(player.getUniqueId()).getMaxPage();

        if (pageNumber < 1) {
            pageNumber = 1;
        } else if (pageNumber >= max) {
            pageNumber = max;
        }

        playerPage.put(player.getUniqueId(), pageNumber);
    }
    
    public ItemStack getMenuButton() {
        return menuButton;
    }
    
    public ItemStack getNextButton() {
        return getNextButton(null);
    }
    
    public ItemStack getNextButton(Player player) {
        ItemBuilder button = new ItemBuilder(nextButton);

        if (player != null) button.addLorePlaceholder("%Page%", (getPage(player) + 1) + "");

        return button.build();
    }
    
    public ItemStack getBackButton() {
        return getBackButton(null);
    }
    
    public ItemStack getBackButton(Player player) {
        ItemBuilder button = new ItemBuilder(backButton);

        if (player != null) button.addLorePlaceholder("%Page%", (getPage(player) - 1) + "");

        return button.build();
    }
    
    public boolean playerInMenu(Player player) {
        return playerInMenu.getOrDefault(player.getUniqueId(), false);
    }
    
    public void setPlayerInMenu(Player player, boolean inMenu) {
        playerInMenu.put(player.getUniqueId(), inMenu);
    }
    
    @EventHandler
    public void onPlayerClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getClickedInventory() == null && playerCrate.get(player.getUniqueId()) == null) return;

        Crate crate = playerCrate.get(player.getUniqueId());

        if (!crate.isPreview(event.getView())) return;

        event.setCancelled(true);

        if (event.getCurrentItem() == null) return;

        if (event.getRawSlot() == crate.getAbsoluteItemPosition(4)) { // Clicked the menu button.
            //TODO()
            //if (playerInMenu(player)) MenuListener.openGUI(player);
        } else if (event.getRawSlot() == crate.getAbsoluteItemPosition(5)) { // Clicked the next button.

            if (getPage(player) < crate.getMaxPage()) {
                nextPage(player);
                openPreview(player, crate);
            }

        } else if (event.getRawSlot() == crate.getAbsoluteItemPosition(3)) { // Clicked the back button.
            if (getPage(player) > 1 && getPage(player) <= crate.getMaxPage()) {
                backPage(player);
                openPreview(player, crate);
            }
        }
    }
    
    public void nextPage(Player player) {
        setPage(player, getPage(player) + 1);
    }
    
    public void backPage(Player player) {
        setPage(player, getPage(player) - 1);
    }
}