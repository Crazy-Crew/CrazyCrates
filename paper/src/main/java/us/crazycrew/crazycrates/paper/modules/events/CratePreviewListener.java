package us.crazycrew.crazycrates.paper.modules.events;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.common.config.ConfigManager;
import us.crazycrew.crazycrates.common.config.types.Config;
import us.crazycrew.crazycrates.paper.CrazyHandler;
import us.crazycrew.crazycrates.paper.api.crates.menus.InventoryManager;
import us.crazycrew.crazycrates.paper.api.crates.menus.types.CrateMainMenu;
import us.crazycrew.crazycrates.paper.api.crates.menus.types.CratePreviewMenu;
import us.crazycrew.crazycrates.paper.modules.ModuleHandler;

public class CratePreviewListener extends ModuleHandler {

    @NotNull
    private final CrazyHandler crazyHandler = this.plugin.getCrazyHandler();

    @NotNull
    private final InventoryManager inventoryManager = this.crazyHandler.getInventoryManager();

    @NotNull
    private final ConfigManager configManager = this.plugin.getConfigManager();

    @NotNull
    private final SettingsManager config = this.configManager.getConfig();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        Inventory inventory = event.getClickedInventory();

        if (inventory == null || !(inventory.getHolder(false) instanceof CratePreviewMenu)) {
            return;
        }

        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();

        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        if (this.inventoryManager.getCratePreview(player) == null) {
            return;
        }

        Crate crate = this.inventoryManager.getCratePreview(player);

        if (event.getRawSlot() == crate.getAbsoluteItemPosition(4)) { // Clicked the menu button.
            if (this.inventoryManager.inCratePreview(player)) {
                this.inventoryManager.removeViewer(player);
                this.inventoryManager.closeCratePreview(player);

                CrateMainMenu crateMainMenu = new CrateMainMenu(player, this.config.getProperty(Config.inventory_size), this.config.getProperty(Config.inventory_name));

                player.openInventory(crateMainMenu.build().getInventory());
            }
        } else if (event.getRawSlot() == crate.getAbsoluteItemPosition(5)) { // Clicked the next button.
            if (this.inventoryManager.getPage(player) < crate.getMaxPage()) {
                this.inventoryManager.nextPage(player);

                this.inventoryManager.openCratePreview(player, crate);
            }
        } else if (event.getRawSlot() == crate.getAbsoluteItemPosition(3)) { // Clicked the back button.
            if (this.inventoryManager.getPage(player) > 1 && this.inventoryManager.getPage(player) <= crate.getMaxPage()) {
                this.inventoryManager.backPage(player);

                this.inventoryManager.openCratePreview(player, crate);
            }
        }
    }

    @Override
    public String getModuleName() {
        return "Crate Preview Listener";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void reload() {}
}