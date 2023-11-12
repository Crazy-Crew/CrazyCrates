package us.crazycrew.crazycrates.paper.listeners.menus;

import com.badbones69.crazycrates.api.enums.Permissions;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import us.crazycrew.crazycrates.api.users.UserManager;
import us.crazycrew.crazycrates.paper.managers.CrateManager;
import us.crazycrew.crazycrates.paper.api.builders.types.CrateAdminMenu;
import us.crazycrew.crazycrates.paper.api.enums.Translation;
import us.crazycrew.crazycrates.paper.api.modules.ModuleHandler;
import us.crazycrew.crazycrates.paper.other.MiscUtils;
import java.util.HashMap;

public class CrateAdminListener extends ModuleHandler {

    @NotNull
    private final CrateManager crateManager = this.plugin.getCrateManager();

    @NotNull
    private final UserManager userManager = this.plugin.getCrazyHandler().getUserManager();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        Inventory inventory = event.getInventory();

        if (!(inventory.getHolder(false) instanceof CrateAdminMenu)) {
            return;
        }

        event.setCancelled(true);

        if (!MiscUtils.permCheck(player, Permissions.CRAZY_CRATES_ADMIN_ACCESS, false)) {
            player.closeInventory(InventoryCloseEvent.Reason.CANT_USE);
            player.sendMessage(Translation.no_permission.getString());
            return;
        }

        ItemStack item = event.getCurrentItem();

        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        if (!this.crateManager.isKey(item)) {
            return;
        }

        Crate crate = this.crateManager.getCrateFromKey(item);

        if (event.getClick() == ClickType.LEFT) {
            player.getInventory().addItem(crate.getKey());
        }

        if (event.getClick() == ClickType.RIGHT) {
            this.userManager.addKeys(1, player.getUniqueId(), crate.getName(), KeyType.virtual_key);

            ItemStack key = crate.getKey();

            if (key.getItemMeta() != null) {
                HashMap<String, String> placeholders = new HashMap<>();

                placeholders.put("%amount%", String.valueOf(1));
                placeholders.put("%key%", crate.getKey().getItemMeta().getDisplayName());

                player.sendMessage(Translation.obtaining_keys.getMessage(placeholders).toString());
            }
        }
    }

    @Override
    public String getModuleName() {
        return "Crate Admin Listener";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void reload() {}
}