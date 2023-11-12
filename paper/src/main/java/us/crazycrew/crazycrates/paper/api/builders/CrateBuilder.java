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
import us.crazycrew.crazycrates.paper.other.MiscUtils;

public abstract class CrateBuilder {

    @NotNull
    protected final CrazyCrates plugin = CrazyCrates.getPlugin(CrazyCrates.class);

    private final InventoryBuilder menu;
    private final Player player;
    private final String title;
    private final Crate crate;
    private final int size;

    private Inventory inventory;

    protected CrateBuilder(Crate crate, Player player, int size, String title) {
        this.crate = crate;

        this.player = player;
        this.size = size;
        this.title = title;

        this.menu = new CratePrizeMenu(crate, player, 27, title);

        if (this.inventory == null) this.inventory = this.menu.build().getInventory();
    }

    public abstract void open(KeyType type, boolean checkHand);

    public void addCrateTask(Player player, BukkitTask task) {
        this.plugin.getCrateManager().addCrateTask(player, task);
    }

    /**
     * @return crate that is being opened
     */

    public Crate getCrateTask(Player player) {
        return this.plugin.getCrateManager().getOpeningCrate(player);
    }

    public void removeCrateTask(Player player) {
        this.plugin.getCrateManager().removeCrateTask(player);
    }

    public Crate getCrate() {
        return this.crate;
    }

    /**
     * @return title of the crate
     */
    public String getTitle() {
        return this.title;
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
    public void setGlassPane(ItemStack item, int slot) {
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
}