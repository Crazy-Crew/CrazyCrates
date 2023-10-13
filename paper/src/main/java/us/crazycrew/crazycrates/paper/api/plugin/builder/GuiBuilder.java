package us.crazycrew.crazycrates.paper.api.plugin.builder;

import com.ryderbelserion.cluster.api.utils.ColorUtils;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.paper.CrazyCrates;

@SuppressWarnings("deprecation")
public abstract class GuiBuilder implements InventoryHolder {

    private final Inventory inventory;
    private String title;
    private int size;

    public GuiBuilder(CrazyCrates plugin, int size, String title) {
        this.title = title;
        this.size = size;

        this.inventory = plugin.getServer().createInventory(this, this.size, ColorUtils.parse(this.title));
    }

    public abstract GuiBuilder build();

    public void size(int size) {
        this.size = size;
    }

    public void title(String title) {
        this.title = title;
    }

    public void update(HumanEntity human) {
        human.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW);

        human.openInventory(getInventory());
    }

    @Override
    @NotNull
    public Inventory getInventory() {
        return this.inventory;
    }
}