package com.badbones69.crazycrates.api.builders;

import com.badbones69.crazycrates.api.builders.types.CratePrizeMenu;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Tier;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.ryderbelserion.vital.paper.builders.items.ItemBuilder;
import com.ryderbelserion.vital.paper.util.scheduler.FoliaRunnable;
import com.badbones69.crazycrates.tasks.crates.effects.SoundEffect;
import com.google.common.base.Preconditions;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.Material;
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

    protected @NotNull final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    protected @NotNull final CrateManager crateManager = this.plugin.getCrateManager();

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
    public CrateBuilder(@NotNull final Crate crate, @NotNull final Player player, final int size) {
        super(player.getScheduler(), null);

        Preconditions.checkNotNull(crate, "Crate can't be null.");
        Preconditions.checkNotNull(player, "Player can't be null.");

        this.crate = crate;

        this.location = player.getLocation();

        this.player = player;
        this.size = size;

        this.builder = new CratePrizeMenu(player, crate.getCrateInventoryName(), size, crate);
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
    public CrateBuilder(@NotNull final Crate crate, @NotNull final Player player, final int size, @NotNull final String crateName) {
        super(player.getScheduler(), null);

        Preconditions.checkNotNull(crate, "Crate can't be null.");
        Preconditions.checkNotNull(player, "Player can't be null.");

        this.crate = crate;

        this.location = player.getLocation();

        this.player = player;
        this.size = size;

        this.builder = new CratePrizeMenu(player, crateName, size, crate);
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
    public CrateBuilder(@NotNull final Crate crate, @NotNull final Player player, final int size, @NotNull final Location location) {
        super(player.getScheduler(), null);

        Preconditions.checkNotNull(crate, "Crate can't be null.");
        Preconditions.checkNotNull(player, "Player can't be null.");
        Preconditions.checkNotNull(location, "Location can't be null.");

        this.crate = crate;

        this.location = location;

        this.player = player;
        this.size = size;

        this.builder = new CratePrizeMenu(player, crate.getCrateInventoryName(), size, crate);
        this.inventory = this.builder.build().getInventory();
    }

    /**
     * Create a crate with no inventory size.
     *
     * @param crate crate opened by player.
     * @param player player opening crate.
     */
    public CrateBuilder(@NotNull final Crate crate, @NotNull final Player player) {
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
    public CrateBuilder(@NotNull final Crate crate, @NotNull final Player player, @NotNull final Location location) {
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
    public abstract void open(@NotNull final KeyType type, final boolean checkHand);

    /**
     * Add a new crate task.
     *
     * @param task task to add.
     */
    public void addCrateTask(@NotNull final ScheduledTask task) {
        this.crateManager.addCrateTask(this.player, task);
    }

    /**
     * Remove crate task.
     */
    public void removeTask() {
        this.crateManager.removeCrateTask(this.player);
    }

    /**
     * Cancel a crate task and remove it.
     */
    public void cancelCrateTask() {
        // Cancel
        this.crateManager.getCrateTask(this.player).cancel();

        // Remove the task.
        removeTask();
    }

    /**
     * @return true or false.
     */
    public final boolean hasCrateTask() {
        return this.crateManager.hasCrateTask(this.player);
    }

    /**
     * @return crate that is being opened.
     */
    public @NotNull final Crate getCrate() {
        return this.crate;
    }

    /**
     * @return title of the crate.
     */
    public @NotNull final String getTitle() {
        return this.crate.getCrateInventoryName();
    }

    /**
     * @return player opening the crate.
     */
    public @NotNull final Player getPlayer() {
        return this.player;
    }

    /**
     * @return inventory size.
     */
    public final int getSize() {
        return this.size;
    }

    /**
     * If the crate type is fire cracker, we won't run the open crate event again.
     *
     * @return true or false.
     */
    public final boolean isFireCracker() {
        return this.crate.getCrateType() == CrateType.fire_cracker;
    }

    /**
     * If the crate type is cosmic crate, we won't run the event again.
     *
     * @return true or false.
     */
    public final boolean isCosmicCrate() {
        return this.crate.getCrateType() == CrateType.cosmic;
    }

    /**
     * @return file configuration of crate.
     */
    public FileConfiguration getFile() {
        return this.crate.getFile();
    }

    /**
     * @return inventory of the crate.
     */
    public Inventory getInventory() {
        return this.inventory;
    }

    /**
     * Specific crates need a location.
     *
     * @return location in the world.
     */
    public Location getLocation() {
        return this.location;
    }

    /**
     * @return instance of this class.
     */
    public InventoryBuilder getMenu() {
        return this.builder.build();
    }

    /**
     * Sets an item to a slot.
     *
     * @param item item to set.
     * @param slot slot to set at.
     */
    public void setItem(final int slot, @NotNull final ItemStack item) {
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
    public void setItem(final int slot, @NotNull final Material material, @NotNull final String name, @NotNull final List<String> lore) {
        getInventory().setItem(slot, new ItemBuilder(material).setPlayer(getPlayer()).setDisplayName(name).setDisplayLore(lore).getStack());
    }

    /**
     * Sets an item to a slot.
     *
     * @param slot slot to set at.
     * @param material material to use.
     * @param name name of item.
     */
    public void setItem(final int slot, @NotNull final Material material, @NotNull final String name) {
        getInventory().setItem(slot, new ItemBuilder(material).setPlayer(getPlayer()).setDisplayName(name).getStack());
    }

    /**
     * Sets random glass pane at a specific slot.
     *
     * @param slot slot to set at.
     */
    public void setCustomGlassPane(final int slot) {
        getInventory().setItem(slot, getRandomGlassPane());
    }

    /**
     * @return the itemstack
     */
    public @NotNull final ItemStack getRandomGlassPane() {
        return MiscUtils.getRandomPaneColor().setDisplayName(" ").getStack();
    }

    /**
     * Calls the crate open event and returns true/false if successful or not.
     *
     * @param keyType virtual or physical key.
     * @param checkHand true or false.
     * @return true if cancelled otherwise false.
     */
    public final boolean isCrateEventValid(@NotNull final KeyType keyType, final boolean checkHand) {
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
    public ItemStack getDisplayItem(@NotNull final Tier tier) {
        return getCrate().pickPrize(getPlayer(), tier).getDisplayItem(getPlayer());
    }

    /**
     * Plays a sound at different volume levels with fallbacks.
     *
     * @param type i.e. stop, cycle or click sound
     * @param source sound category to respect client settings
     * @param fallback fallback sound in case no sound is found
     */
    public void playSound(@NotNull final String type, @NotNull final Sound.Source source, @NotNull final String fallback) {
        if (type.isEmpty() && fallback.isEmpty()) return;

        ConfigurationSection section = getFile().getConfigurationSection("Crate.sound");

        if (section != null) {
            Player player = getPlayer();

            SoundEffect sound = new SoundEffect(
                    section,
                    type,
                    fallback,
                    source
            );

            sound.play(player, player.getLocation());
        }
    }
}