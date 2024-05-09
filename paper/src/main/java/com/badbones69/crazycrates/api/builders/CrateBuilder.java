package com.badbones69.crazycrates.api.builders;

import com.badbones69.crazycrates.api.builders.types.CratePrizeMenu;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Tier;
import com.badbones69.crazycrates.scheduler.FoliaRunnable;
import com.badbones69.crazycrates.tasks.crates.effects.SoundEffect;
import com.google.common.base.Preconditions;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.events.CrateOpenEvent;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import java.util.List;

public abstract class CrateBuilder extends FoliaRunnable {

    protected final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private final InventoryBuilder builder;
    private final Inventory inventory;
    private final Location location;
    private final Player player;
    private final Crate crate;
    private final int size;

    /**
     * Create a crate with inventory size.
     *
     * @param crate crate opened by player.
     * @param player player opening crate.
     * @param size size of inventory.
     */
    public CrateBuilder(Crate crate, Player player, int size) {
        super(player.getScheduler(), null);

        Preconditions.checkNotNull(crate, "Crate can't be null.");
        Preconditions.checkNotNull(player, "Player can't be null.");

        this.crate = crate;

        this.location = player.getLocation();

        this.player = player;
        this.size = size;

        this.builder = new CratePrizeMenu(crate, player, size, crate.getCrateInventoryName());
        this.inventory = this.builder.build().getInventory();
    }

    /**
     * Create a crate with inventory size.
     *
     * @param crate crate opened by player.
     * @param player player opening crate.
     * @param size size of inventory.
     * @param crateName crate name of crate.
     */
    public CrateBuilder(Crate crate, Player player, int size, String crateName) {
        super(player.getScheduler(), null);

        Preconditions.checkNotNull(crate, "Crate can't be null.");
        Preconditions.checkNotNull(player, "Player can't be null.");

        this.crate = crate;

        this.location = player.getLocation();

        this.player = player;
        this.size = size;

        this.builder = new CratePrizeMenu(crate, player, size, crateName);
        this.inventory = this.builder.build().getInventory();
    }

    /**
     * Create a crate with inventory size.
     *
     * @param crate crate opened by player.
     * @param player player opening crate.
     * @param size size of inventory.
     * @param location location of player.
     */
    public CrateBuilder(Crate crate, Player player, int size, Location location) {
        super(player.getScheduler(), null);

        Preconditions.checkNotNull(crate, "Crate can't be null.");
        Preconditions.checkNotNull(player, "Player can't be null.");
        Preconditions.checkNotNull(location, "Location can't be null.");

        this.crate = crate;

        this.location = location;

        this.player = player;
        this.size = size;

        this.builder = new CratePrizeMenu(crate, player, size, crate.getCrateInventoryName());
        this.inventory = this.builder.build().getInventory();
    }

    /**
     * Create a crate with no inventory size.
     *
     * @param crate crate opened by player.
     * @param player player opening crate.
     */
    public CrateBuilder(Crate crate, Player player) {
        super(player.getScheduler(), null);

        Preconditions.checkNotNull(crate, "Crate can't be null.");
        Preconditions.checkNotNull(player, "Player can't be null.");

        this.crate = crate;

        this.location = player.getLocation();

        this.player = player;

        this.size = 0;
        this.inventory = null;
        this.builder = null;
    }

    /**
     * Create a crate with no inventory size.
     *
     * @param crate crate opened by player.
     * @param player player opening crate.
     * @param location location of player.
     */
    public CrateBuilder(Crate crate, Player player, Location location) {
        super(player.getScheduler(), null);

        Preconditions.checkNotNull(crate, "Crate can't be null.");
        Preconditions.checkNotNull(player, "Player can't be null.");
        Preconditions.checkNotNull(location, "Location can't be null.");

        this.crate = crate;

        this.location = location;

        this.player = player;
        this.size = 0;

        this.builder = null;
        this.inventory = null;
    }

    /**
     * The open method for crates.
     *
     * @param type type of key.
     * @param checkHand whether to check hands or not.
     */
    public abstract void open(KeyType type, boolean checkHand);

    /**
     * Add a new crate task.
     *
     * @param task task to add.
     */
    public void addCrateTask(ScheduledTask task) {
        this.plugin.getCrateManager().addCrateTask(this.player, task);
    }

    /**
     * Remove crate task.
     */
    public void removeTask() {
        this.plugin.getCrateManager().removeCrateTask(this.player);
    }

    /**
     * Cancel a crate task and remove it.
     */
    public void cancelCrateTask() {
        // Cancel
        this.plugin.getCrateManager().getCrateTask(this.player).cancel();

        // Remove the task.
        removeTask();
    }

    /**
     * @return true or false.
     */
    public boolean hasCrateTask() {
        return this.plugin.getCrateManager().hasCrateTask(this.player);
    }

    /**
     * @return crate that is being opened.
     */
    @NotNull
    public Crate getCrate() {
        return this.crate;
    }

    /**
     * @return title of the crate.
     */
    @NotNull
    public String getTitle() {
        return this.crate.getCrateInventoryName();
    }

    /**
     * @return player opening the crate.
     */
    @NotNull
    public Player getPlayer() {
        return this.player;
    }

    /**
     * @return inventory size.
     */
    public int getSize() {
        return this.size;
    }

    /**
     * If the crate type is fire cracker, we won't run the open crate event again.
     *
     * @return true or false.
     */
    public boolean isFireCracker() {
        return this.crate.getCrateType() == CrateType.fire_cracker;
    }

    /**
     * If the crate type is cosmic crate, we won't run the event again.
     *
     * @return true or false.
     */
    public boolean isCosmicCrate() {
        return this.crate.getCrateType() == CrateType.cosmic;
    }

    /**
     * @return file configuration of crate.
     */
    @NotNull
    public FileConfiguration getFile() {
        return this.crate.getFile();
    }

    /**
     * @return inventory of the crate.
     */
    @NotNull
    public Inventory getInventory() {
        return this.inventory;
    }

    /**
     * Specific crates need a location.
     *
     * @return location in the world.
     */
    @NotNull
    public Location getLocation() {
        return this.location;
    }

    /**
     * @return instance of this class.
     */
    @NotNull
    public InventoryBuilder getMenu() {
        return this.builder.build();
    }

    /**
     * Sets an item to a slot.
     *
     * @param item item to set.
     * @param slot slot to set at.
     */
    public void setItem(int slot, ItemStack item) {
        getInventory().setItem(slot, item);
    }

    /**
     * Sets an item to a slot.
     *
     * @param slot slot to set at.
     * @param material material to use.
     * @param name name of item.
     * @param lore lore of item.
     */
    public void setItem(int slot, Material material, String name, List<String> lore) {
        ItemBuilder builder = new ItemBuilder(new ItemStack(material)).setName(name).setLore(lore).setTarget(getPlayer());

        getInventory().setItem(slot, builder.build());
    }

    /**
     * Sets an item to a slot.
     *
     * @param slot slot to set at.
     * @param material material to use.
     * @param name name of item.
     */
    public void setItem(int slot, Material material, String name) {
        ItemBuilder builder = new ItemBuilder(new ItemStack(material)).setName(name).setTarget(getPlayer());

        getInventory().setItem(slot, builder.build());
    }

    /**
     * Sets random glass pane at a specific slot.
     *
     * @param slot slot to set at.
     */
    public void setCustomGlassPane(int slot) {
        getInventory().setItem(slot, getRandomGlassPane());
    }

    public ItemStack getRandomGlassPane() {
        return MiscUtils.getRandomPaneColor().setName(" ").build();
    }

    /**
     * Calls the crate open event and returns true/false if successful or not.
     *
     * @param keyType virtual or physical key.
     * @param checkHand true or false.
     * @return true if cancelled otherwise false.
     */
    public boolean isCrateEventValid(KeyType keyType, boolean checkHand) {
        CrateOpenEvent event = new CrateOpenEvent(this.player, this.crate, keyType, checkHand, this.crate.getFile());
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

    protected boolean isCancelled = false;

    /**
     * Cancel the task.
     */
    @Override
    public void cancel() {
        super.cancel();
        this.isCancelled = true;
    }

    /**
     * @return the display item of the picked prize.
     */
    public ItemStack getDisplayItem() {
        return getCrate().pickPrize(getPlayer()).getDisplayItem(getPlayer());
    }

    /**
     * @return the display item of the picked prize with a tier.
     */
    public ItemStack getDisplayItem(Tier tier) {
        return getCrate().pickPrize(getPlayer(), tier).getDisplayItem(getPlayer());
    }

    /**
     * Plays a sound at different volume levels with fallbacks.
     *
     * @param type i.e. stop, cycle or click sound.
     * @param category sound category to respect client settings.
     * @param fallback fallback sound in case no sound is found.
     */
    public void playSound(String type, SoundCategory category, String fallback) {
        ConfigurationSection section = getFile().getConfigurationSection("Crate.sound");

        if (section != null) {
            SoundEffect sound = new SoundEffect(
                    section,
                    type,
                    fallback,
                    category
            );

            sound.play(getPlayer(), getPlayer().getLocation());
        }
    }
}