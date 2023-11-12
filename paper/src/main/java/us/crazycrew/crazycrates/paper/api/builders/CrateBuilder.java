package us.crazycrew.crazycrates.paper.api.builders;

import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.google.common.base.Preconditions;
import org.bukkit.Location;
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
    private final Location location;
    private final Player player;
    private final Crate crate;
    private final int size;

    public CrateBuilder(Crate crate, Player player, int size) {
        Preconditions.checkNotNull(crate, "Crate can't be null.");
        Preconditions.checkNotNull(player, "Player can't be null.");

        this.crate = crate;

        this.location = player.getLocation();

        this.player = player;
        this.size = size;

        this.menu = new CratePrizeMenu(crate, player, size, crate.getCrateInventoryName());

        this.inventory = this.menu.build().getInventory();
    }

    public CrateBuilder(Crate crate, Player player, int size, Location location) {
        Preconditions.checkNotNull(crate, "Crate can't be null.");
        Preconditions.checkNotNull(player, "Player can't be null.");
        Preconditions.checkNotNull(location, "Location can't be null.");

        this.crate = crate;

        this.location = location;

        this.player = player;
        this.size = size;

        this.menu = new CratePrizeMenu(crate, player, size, crate.getCrateInventoryName());

        this.inventory = this.menu.build().getInventory();
    }

    public CrateBuilder(Crate crate, Player player, Location location) {
        Preconditions.checkNotNull(crate, "Crate can't be null.");
        Preconditions.checkNotNull(player, "Player can't be null.");
        Preconditions.checkNotNull(location, "Location can't be null.");

        this.crate = crate;

        this.location = location;

        this.player = player;
        this.size = 0;

        this.menu = null;

        this.inventory = null;
    }

    public abstract void open(KeyType type, boolean checkHand);

    public void addCrateTask(BukkitTask task) {
        this.plugin.getCrateManager().addCrateTask(this.player, task);
    }

    public void removeTask() {
        this.plugin.getCrateManager().removeCrateTask(this.player);
    }

    public void cancelCrateTask() {
        // Cancel
        this.plugin.getCrateManager().getCrateTask(this.player).cancel();

        // Remove the task.
        removeTask();
    }

    public boolean hasCrateTask() {
        return this.plugin.getCrateManager().hasCrateTask(this.player);
    }

    /**
     * @return crate that is being opened
     */
    @NotNull
    public Crate getCrate() {
        return this.crate;
    }

    /**
     * @return title of the crate
     */
    @NotNull
    public String getTitle() {
        return this.crate.getCrateInventoryName();
    }

    /**
     * @return player opening the crate
     */
    @NotNull
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
    @NotNull
    public Inventory getInventory() {
        return this.inventory;
    }

    /**
     * Specific crates need a location.
     *
     * @return location in the world
     */
    @NotNull
    public Location getLocation() {
        return this.location;
    }

    /**
     * @return instance of this class
     */
    @NotNull
    public InventoryBuilder getMenu() {
        return this.menu.build();
    }

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
     * @param keyType virtual or physical key
     * @param checkHand true or false
     * @return true if cancelled otherwise false.
     */
    public boolean isCrateEventValid(KeyType keyType, boolean checkHand) {
        CrateOpenEvent event = new CrateOpenEvent(this.plugin, this.player, this.crate, keyType, checkHand, this.crate.getFile());
        event.callEvent();

        if (event.isCancelled()) {
            List.of(
                    "Crate " + this.crate.getName() + " event has been cancelled.",
                    "A few reasons for why this happened can be found below",
                    "",
                    " 1) No valid prizes can be found, Likely a yaml issue.",
                    " 2) The player does not have the permission to open the crate."
            ).forEach(this.plugin.getLogger()::warning);
        }

        return event.isCancelled();
    }
}