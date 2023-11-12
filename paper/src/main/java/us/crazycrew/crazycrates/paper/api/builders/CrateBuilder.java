package us.crazycrew.crazycrates.paper.api.builders;

import com.badbones69.crazycrates.paper.api.objects.Crate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.api.builders.types.CratePrizeMenu;
import us.crazycrew.crazycrates.paper.api.events.CrateOpenEvent;
import us.crazycrew.crazycrates.paper.other.MiscUtils;

import java.util.List;

public abstract class CrateBuilder {

    @NotNull
    protected final CrazyCrates plugin = CrazyCrates.getPlugin(CrazyCrates.class);

    private final InventoryBuilder menu;
    private final Inventory inventory;
    private final Player player;
    private final Crate crate;
    private final int size;

    public CrateBuilder(Crate crate, Player player, int size) {
        this.crate = crate;

        this.player = player;
        this.size = size;

        this.menu = new CratePrizeMenu(crate, player, size, crate.getCrateInventoryName());

        this.inventory = this.menu.build().getInventory();
    }

    public abstract void open(KeyType type, boolean checkHand);

    public void addCrateTask(Player player, BukkitTask task) {
        this.plugin.getCrateManager().addCrateTask(player, task);
    }

    /**
     * @return crate that is being opened
     */
    public Crate getCrate() {
        return this.crate;
    }

    /**
     * @return title of the crate
     */
    public String getTitle() {
        return this.crate.getCrateInventoryName();
    }

    /**
     * @return player opening the crate
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * @return inventory size
     */
    public int getSize() {
        return this.size;
    }

    /**
     * @return inventory of the crate
     */
    public Inventory getInventory() {
        return this.inventory;
    }

    /**
     * @return instance of this class
     */
    public InventoryBuilder getMenu() {
        return this.menu.build();
    }

    // Item Management

    /**
     * Sets an item to a slot
     *
     * @param item to set
     * @param slot to set at
     */
    public void setItem(ItemStack item, int slot) {
        getInventory().setItem(slot, item);
    }

    /**
     * Sets random glass pane at a specific slot.
     *
     * @param slot to set at
     */
    public void setCustomGlassPane(int slot) {
        ItemStack item = MiscUtils.getRandomPaneColor().setName(" ").build();
        getInventory().setItem(slot, item);
    }

    /**
     * Calls the crate open event and returns true/false if successful or not.
     *
     * @param player opening the crate
     * @param crate object being opened
     * @param keyType virtual or physical key
     * @param checkHand true or false
     * @return true if cancelled otherwise false.
     */
    public boolean isCrateEventValid(Player player, Crate crate, KeyType keyType, boolean checkHand) {
        CrateOpenEvent event = new CrateOpenEvent(this.plugin, player, crate, keyType, checkHand, crate.getFile());
        event.callEvent();

        if (event.isCancelled()) {
            List.of(
                    "Crate " + crate.getName() + " event has been cancelled.",
                    "A few reasons for why this happened can be found below",
                    "",
                    " 1) No valid prizes can be found, Likely a yaml issue.",
                    " 2) The player does not have the permission to open the crate."
            ).forEach(this.plugin.getLogger()::warning);
        }

        return event.isCancelled();
    }
}