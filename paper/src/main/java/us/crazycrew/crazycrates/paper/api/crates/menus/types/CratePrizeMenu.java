package us.crazycrew.crazycrates.paper.api.crates.menus.types;

import com.badbones69.crazycrates.paper.api.objects.Crate;
import org.bukkit.entity.Player;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.api.crates.menus.InventoryBuilder;

public class CratePrizeMenu extends InventoryBuilder {

    public CratePrizeMenu(CrazyCrates plugin, Crate crate, Player player, int size, String title) {
        super(plugin, crate, player, size, title);
    }

    @Override
    public InventoryBuilder build() {
        return this;
    }
}