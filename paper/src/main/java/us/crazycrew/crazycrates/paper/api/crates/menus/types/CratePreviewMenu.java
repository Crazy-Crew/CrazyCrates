package us.crazycrew.crazycrates.paper.api.crates.menus.types;

import com.badbones69.crazycrates.paper.api.objects.Crate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.api.crates.menus.InventoryBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CratePreviewMenu extends InventoryBuilder {

    public CratePreviewMenu(CrazyCrates plugin, Crate crate, Player player, int size, int page, String title) {
        super(plugin, crate, player, size, page, title);
    }

    @Override
    public InventoryBuilder build() {
        Inventory inventory = getInventory();

        setDefaultItems(inventory);

        for (ItemStack item : getPageItems(getPage())) {
            int nextSlot = inventory.firstEmpty();

            if (nextSlot >= 0) {
                inventory.setItem(nextSlot, item);
            } else {
                break;
            }
        }

        return this;
    }

    private void setDefaultItems(Inventory inventory) {
        if (getCrate().isBorderToggle()) {
            List<Integer> borderItems = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8);

            for (int i : borderItems) { // Top Boarder slots
                inventory.setItem(i, getCrate().getBorderItem().build());
            }

            borderItems.replaceAll(getCrate()::getAbsoluteItemPosition);

            for (int i : borderItems) { // Bottom Boarder slots
                inventory.setItem(i, getCrate().getBorderItem().build());
            }
        }

        int page = getPlugin().getCrazyHandler().getInventoryManager().getPage(getPlayer());

        if (getPlugin().getCrazyHandler().getInventoryManager().inCratePreview(getPlayer())) inventory.setItem(getCrate().getAbsoluteItemPosition(4), getPlugin().getCrazyHandler().getInventoryManager().getMenuButton());

        if (page == 1) {
            if (getCrate().isBorderToggle()) inventory.setItem(getCrate().getAbsoluteItemPosition(3), getCrate().getBorderItem().build());
        } else {
            inventory.setItem(getCrate().getAbsoluteItemPosition(3), getPlugin().getCrazyHandler().getInventoryManager().getBackButton(getPlayer()));
        }

        if (page == getCrate().getMaxPage()) {
            if (getCrate().isBorderToggle()) inventory.setItem(getCrate().getAbsoluteItemPosition(5), getCrate().getBorderItem().build());
        } else {
            inventory.setItem(getCrate().getAbsoluteItemPosition(5), getPlugin().getCrazyHandler().getInventoryManager().getNextButton(getPlayer()));
        }
    }

    private List<ItemStack> getPageItems(int page) {
        List<ItemStack> list = getCrate().getPreviewItems();
        List<ItemStack> items = new ArrayList<>();

        if (page <= 0) page = 1;

        int max = getCrate().getMaxSlots() - (getCrate().isBorderToggle() ? 18 : getCrate().getMaxSlots() >= list.size() ? 0 : getCrate().getMaxSlots() != 9 ? 9 : 0);
        int index = page * max - max;
        int endIndex = index >= list.size() ? list.size() - 1 : index + max;

        for (; index < endIndex; index++) {
            if (index < list.size()) items.add(list.get(index));
        }

        for (; items.isEmpty(); page--) {
            if (page <= 0) break;
            index = page * max - max;
            endIndex = index >= list.size() ? list.size() - 1 : index + max;

            for (; index < endIndex; index++) {
                if (index < list.size()) items.add(list.get(index));
            }
        }

        return items;
    }
}