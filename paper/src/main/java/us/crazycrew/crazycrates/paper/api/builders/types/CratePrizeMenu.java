package us.crazycrew.crazycrates.paper.api.builders.types;

import com.badbones69.crazycrates.paper.api.objects.Crate;
import org.bukkit.entity.Player;
import us.crazycrew.crazycrates.paper.api.builders.InventoryBuilder;

public class CratePrizeMenu extends InventoryBuilder {

    public CratePrizeMenu(Crate crate, Player player, int size, String title) {
        super(crate, player, size, title);
    }

    @Override
    public InventoryBuilder build() {
        return this;
    }
}