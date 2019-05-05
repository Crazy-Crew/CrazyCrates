package me.badbones69.crazycrates.controllers;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.api.objects.ItemBuilder;
import me.badbones69.crazycrates.controllers.FileManager.Files;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class Preview implements Listener {
	
	private CrazyCrates cc = CrazyCrates.getInstance();
	private static HashMap<UUID, Integer> playerPage = new HashMap<>();
	private static HashMap<UUID, Crate> playerCrate = new HashMap<>();
	private static HashMap<UUID, Material> playerColor = new HashMap<>();
	private static HashMap<UUID, Boolean> playerInMenu = new HashMap<>();
	
	@EventHandler
	public void onPlayerClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		Inventory inventory = e.getInventory();
		if(inventory != null) {
			Crate crate = playerCrate.get(player.getUniqueId());
			if(inventory.getName().equals(crate.getPreviewName()) || inventory.getName().equals(crate.getCrateInventoryName())) {
				e.setCancelled(true);
				ItemStack item = e.getCurrentItem();
				if(item != null) {
					if(e.getRawSlot() == 49) {// Clicked the menu button.
						if(playerInMenu(player)) {
							GUIMenu.openGUI(player);
						}
					}else if(e.getRawSlot() == 50) {// Clicked the next button.
						if(getPage(player) < crate.getMaxPage()) {
							nextPage(player);
							openPreview(player, crate);
						}
					}else if(e.getRawSlot() == 48) {// Clicked the back button.
						if(getPage(player) > 1 && getPage(player) <= crate.getMaxPage()) {
							backPage(player);
							openPreview(player, crate);
						}
					}
				}
			}
		}
	}
	
	public static void openNewPreview(Player player, Crate crate) {
		playerCrate.put(player.getUniqueId(), crate);
		pickNewPaneColor(player);
		setPage(player, 1);
		player.openInventory(crate.getPreview(player));
	}
	
	public static void openPreview(Player player) {
		player.openInventory(playerCrate.get(player.getUniqueId()).getPreview(player));
	}
	
	public static void openPreview(Player player, Crate crate) {
		playerCrate.put(player.getUniqueId(), crate);
		player.openInventory(crate.getPreview(player));
	}
	
	public static void openPreview(Player player, Crate crate, Integer page) {
		playerCrate.put(player.getUniqueId(), crate);
		player.openInventory(crate.getPreview(player));
	}
	
	public static int getPage(Player player) {
		if(playerPage.containsKey(player.getUniqueId())) {
			return playerPage.get(player.getUniqueId());
		}
		return 1;
	}
	
	public static void setPage(Player player, int pageNumber) {
		int max = playerCrate.get(player.getUniqueId()).getMaxPage();
		if(pageNumber < 1) {
			pageNumber = 1;
		}else if(pageNumber >= max) {
			pageNumber = max;
		}
		playerPage.put(player.getUniqueId(), pageNumber);
	}
	
	public void nextPage(Player player) {
		setPage(player, getPage(player) + 1);
	}
	
	public void backPage(Player player) {
		setPage(player, getPage(player) - 1);
	}
	
	public static ItemStack getMenuButton() {
		FileConfiguration config = Files.CONFIG.getFile();
		String path = "Settings.Preview.Buttons.Menu";
		if(config.contains(path)) {
			return new ItemBuilder()
			.setMaterial(config.getString(path + ".Item"))
			.setName(config.getString(path + ".Name"))
			.setLore(config.getStringList(path + ".Lore"))
			.build();
		}else {
			return new ItemBuilder()
			.setMaterial(Material.COMPASS)
			.setName("&7&l>> &c&lMenu 77&l<<")
			.addLore("&7Return to the menu.")
			.build();
		}
	}
	
	public static ItemStack getNextButton() {
		FileConfiguration config = Files.CONFIG.getFile();
		String path = "Settings.Preview.Buttons.Next";
		if(config.contains(path)) {
			return new ItemBuilder()
			.setMaterial(config.getString(path + ".Item"))
			.setName(config.getString(path + ".Name"))
			.setLore(config.getStringList(path + ".Lore"))
			.addLorePlaceholder("%page%", "2")
			.addLorePlaceholder("%Page%", "2")
			.build();
		}else {
			return new ItemBuilder()
			.setMaterial(Material.FEATHER)
			.setName("&6&lNext >>")
			.addLore("&7&lPage: &b%page%")
			.addLorePlaceholder("%page%", "2")
			.addLorePlaceholder("%Page%", "2")
			.build();
		}
	}
	
	public static ItemStack getNextButton(Player player) {
		FileConfiguration config = Files.CONFIG.getFile();
		String path = "Settings.Preview.Buttons.Next";
		if(config.contains(path)) {
			return new ItemBuilder()
			.setMaterial(config.getString(path + ".Item"))
			.setName(config.getString(path + ".Name"))
			.setLore(config.getStringList(path + ".Lore"))
			.addLorePlaceholder("%page%", (getPage(player) + 1) + "")
			.addLorePlaceholder("%Page%", (getPage(player) + 1) + "")
			.build();
		}else {
			return new ItemBuilder()
			.setMaterial(Material.FEATHER)
			.setName("&6&lNext >>")
			.addLore("&7&lPage: &b%page%")
			.addLorePlaceholder("%page%", (getPage(player) + 1) + "")
			.addLorePlaceholder("%Page%", (getPage(player) + 1) + "")
			.build();
		}
	}
	
	public static ItemStack getBackButton(Player player) {
		FileConfiguration config = Files.CONFIG.getFile();
		String path = "Settings.Preview.Buttons.Back";
		if(config.contains(path)) {
			return new ItemBuilder()
			.setMaterial(config.getString(path + ".Item"))
			.setName(config.getString(path + ".Name"))
			.setLore(config.getStringList(path + ".Lore"))
			.addLorePlaceholder("%page%", (getPage(player) - 1) + "")
			.addLorePlaceholder("%Page%", (getPage(player) - 1) + "")
			.build();
		}else {
			return new ItemBuilder()
			.setMaterial(Material.FEATHER)
			.setName("&6&l<< Back")
			.addLore("&7&lPage: &b%page%")
			.addLorePlaceholder("%page%", (getPage(player) - 1) + "")
			.addLorePlaceholder("%Page%", (getPage(player) - 1) + "")
			.build();
		}
	}
	
	public static Material getPaneColor(Player player) {
		return playerColor.getOrDefault(player.getUniqueId(), Material.WHITE_STAINED_GLASS_PANE);
	}
	
	public static void pickNewPaneColor(Player player) {
		playerColor.put(player.getUniqueId(), Methods.getRandomPaneColor());
	}
	
	public static Boolean playerInMenu(Player player) {
		if(playerInMenu.containsKey(player.getUniqueId())) {
			return playerInMenu.get(player.getUniqueId());
		}
		return false;
	}
	
	public static void setPlayerInMenu(Player player, Boolean inMenu) {
		playerInMenu.put(player.getUniqueId(), inMenu);
	}
	
}