package us.crazycrew.crazycrates.paper.api.crates.menus.types;

import com.badbones69.crazycrates.paper.api.objects.Crate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.api.crates.menus.InventoryBuilder;

public class CrateAdminMenu extends InventoryBuilder {

    public CrateAdminMenu(CrazyCrates plugin, Player player, int size, String title) {
        super(plugin, player, size, title);
    }

    @Override
    public InventoryBuilder build() {
        Inventory inventory = getInventory();

        for (Crate crate : getPlugin().getCrateManager().getCrates()) {
            if (crate.getCrateType() != CrateType.menu) {
                if (inventory.firstEmpty() >= 0) inventory.setItem(inventory.firstEmpty(), crate.getAdminKey());
            }
        }

        return this;
    }
}