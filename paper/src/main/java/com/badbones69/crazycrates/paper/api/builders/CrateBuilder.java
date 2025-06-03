package com.badbones69.crazycrates.paper.api.builders;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.paper.api.PrizeManager;
import com.badbones69.crazycrates.paper.api.enums.other.keys.ItemKeys;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import com.badbones69.crazycrates.paper.api.objects.crates.CrateLocation;
import com.badbones69.crazycrates.paper.managers.events.enums.EventType;
import com.badbones69.crazycrates.paper.support.holograms.HologramManager;
import com.badbones69.crazycrates.paper.tasks.menus.CratePrizeMenu;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.Tier;
import com.badbones69.crazycrates.core.config.ConfigManager;
import com.badbones69.crazycrates.core.config.impl.ConfigKeys;
import com.badbones69.crazycrates.paper.managers.BukkitUserManager;
import com.badbones69.crazycrates.paper.tasks.crates.CrateManager;
import com.badbones69.crazycrates.paper.tasks.crates.other.CosmicCrateManager;
import com.badbones69.crazycrates.paper.tasks.crates.effects.SoundEffect;
import com.google.common.base.Preconditions;
import com.ryderbelserion.fusion.kyori.utils.AdvUtils;
import com.ryderbelserion.fusion.paper.api.scheduler.FoliaScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.events.CrateOpenEvent;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import java.util.List;

public abstract class CrateBuilder extends FoliaScheduler {

    protected final CrazyCrates plugin = CrazyCrates.getPlugin();

    protected final ComponentLogger logger = this.plugin.getComponentLogger();

    protected final CrateManager crateManager = this.plugin.getCrateManager();

    protected final BukkitUserManager userManager = this.plugin.getUserManager();

    private final InventoryBuilder builder;
    private final Inventory inventory;
    private final Location location;
    private final Player player;
    private final Crate crate;
    private final int size;

    /**
     * Create a crate with inventory size.
     *
     * @param crate crate opened by player
     * @param player player opening crate
     * @param size size of inventory
     * @param title inventory title
     */
    public CrateBuilder(@NotNull final Crate crate, @NotNull final Player player, final int size, @NotNull final String title) {
        super(CrazyCrates.getPlugin(), null, player);

        Preconditions.checkNotNull(crate, "Crate can't be null.");
        Preconditions.checkNotNull(player, "Player can't be null.");

        this.location = player.getLocation();
        this.player = player;

        this.crate = crate;
        this.size = size;

        this.builder = new CratePrizeMenu(player, title.isEmpty() ? getTitle() : title, size, crate);
        this.inventory = this.builder.build().getInventory();
    }

    /**
     * Create a crate with inventory size.
     *
     * @param crate crate opened by player
     * @param player player opening crate
     * @param size size of inventory
     */
    public CrateBuilder(@NotNull final Crate crate, @NotNull final Player player, final int size) {
        this(crate, player, size, "");
    }

    /**
     * Create a crate with inventory size.
     *
     * @param crate crate opened by player
     * @param player player opening crate
     * @param size size of inventory
     * @param location location of player
     */
    public CrateBuilder(@NotNull final Crate crate, @NotNull final Player player, final int size, @NotNull final Location location) {
        super(CrazyCrates.getPlugin(), null, player);

        Preconditions.checkNotNull(crate, "Crate can't be null.");
        Preconditions.checkNotNull(player, "Player can't be null.");
        Preconditions.checkNotNull(location, "Location can't be null.");

        this.location = location;
        this.player = player;

        this.crate = crate;
        this.size = size;

        this.builder = new CratePrizeMenu(player, getTitle(), size, crate);
        this.inventory = this.builder.build().getInventory();
    }

    /**
     * Create a crate with no inventory size.
     *
     * @param crate crate opened by player
     * @param player player opening crate
     * @param location location of player
     */
    public CrateBuilder(@NotNull final Crate crate, @NotNull final Player player, @NotNull final Location location) {
        super(CrazyCrates.getPlugin(), null, player);

        Preconditions.checkNotNull(crate, "Crate can't be null.");
        Preconditions.checkNotNull(player, "Player can't be null.");
        Preconditions.checkNotNull(location, "Location can't be null.");

        this.location = location;
        this.player = player;

        this.crate = crate;
        this.size = 0;

        this.builder = null;
        this.inventory = null;
    }

    /**
     * Create a crate with no inventory size.
     *
     * @param crate crate opened by player
     * @param player player opening crate
     */
    public CrateBuilder(@NotNull final Crate crate, @NotNull final Player player) {
        this(crate, player, player.getLocation());
    }

    /**
     * The open method for crates.
     *
     * @param type type of key
     * @param checkHand whether to check hands or not
     * @param eventType {@link EventType}
     * @param isSilent true or false
     */
    public abstract void open(@NotNull final KeyType type, final boolean checkHand, final boolean isSilent, final EventType eventType);

    public void displayItem(final Prize prize) {
        final boolean showQuickCrateItem = ConfigManager.getConfig().getProperty(ConfigKeys.show_quickcrate_item);

        // Only related to the item above the crate.
        if (showQuickCrateItem) {
            final HologramManager manager = this.crateManager.getHolograms();

            if (manager != null && this.crate.getHologram().isEnabled()) {
                CrateLocation crateLocation = this.crateManager.getCrateLocation(this.location);

                if (crateLocation != null) {
                    manager.removeHologram(crateLocation.getID());
                }
            }

            // Get the display item.
            ItemStack display = prize.getDisplayItem(this.player, this.crate); //todo() use display entities

            display.editPersistentDataContainer(container -> container.set(ItemKeys.crate_prize.getNamespacedKey(), PersistentDataType.STRING, "1"));

            Item reward;

            try {
                reward = this.player.getWorld().dropItem(this.location.clone().add(0.5, 1, 0.5), display);
            } catch (IllegalArgumentException exception) {
                if (MiscUtils.isLogging()) {
                    this.logger.warn("A prize could not be given due to an invalid display item for this prize.");
                    this.logger.warn("Crate: {} Prize: {}", prize.getCrateName(), prize.getPrizeName(), exception);
                }

                return;
            }

            reward.setVelocity(new Vector(0, 0.2, 0));

            reward.customName(AdvUtils.parse(prize.getPrizeName()));

            reward.setCustomNameVisible(true);
            reward.setCanMobPickup(false);
            reward.setCanPlayerPickup(false);

            this.crateManager.addReward(this.player, reward);
        }
    }

    /**
     * Add a new crate task.
     *
     * @param task task to add
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
     * @return crate that is being opened
     */
    public @NotNull final Crate getCrate() {
        return this.crate;
    }

    /**
     * @return title of the crate
     */
    public @NotNull final String getTitle() {
        return this.crate.getAnimationName();
    }

    /**
     * @return player opening the crate
     */
    public @NotNull final Player getPlayer() {
        return this.player;
    }

    /**
     * @return inventory size
     */
    public final int getSize() {
        return this.size;
    }

    /**
     * If the crate type is fire cracker, we won't run the open crate event again.
     *
     * @return true or false
     */
    public final boolean isFireCracker() {
        return this.crate.getCrateType() == CrateType.fire_cracker;
    }

    /**
     * If the crate type is cosmic crate, we won't run the event again.
     *
     * @return true or false
     */
    public final boolean isCosmicCrate() {
        return this.crate.getCrateType() == CrateType.cosmic;
    }

    /**
     * @return file configuration of crate
     */
    public final YamlConfiguration getFile() {
        return this.crate.getFile();
    }

    /**
     * @return inventory of the crate
     */
    public Inventory getInventory() {
        return this.inventory;
    }

    /**
     * Specific crates need a location.
     *
     * @return location in the world
     */
    public Location getLocation() {
        return this.location;
    }

    /**
     * @return instance of this class
     */
    public InventoryBuilder getMenu() {
        return this.builder.build();
    }

    /**
     * Sets an item to a slot.
     *
     * @param item item to set
     * @param slot slot to set at
     */
    public void setItem(final int slot, @NotNull final ItemStack item) {
        getInventory().setItem(slot, item);
    }

    /**
     * Sets an item to a slot.
     *
     * @param slot slot to set at
     * @param itemType material to use
     * @param name name of item
     * @param lore lore of item
     */
    public void setItem(final int slot, @NotNull final ItemType itemType, @NotNull final String name, @NotNull final List<String> lore) {
        getInventory().setItem(slot, new LegacyItemBuilder(this.plugin, itemType).setPlayer(getPlayer()).setDisplayName(name).setDisplayLore(lore).asItemStack());
    }

    /**
     * Sets an item to a slot.
     *
     * @param slot slot to set at
     * @param itemType material to use
     * @param name name of item
     */
    public void setItem(final int slot, @NotNull final ItemType itemType, @NotNull final String name) {
        getInventory().setItem(slot, new LegacyItemBuilder(this.plugin, itemType).setPlayer(getPlayer()).setDisplayName(name).asItemStack());
    }

    /**
     * Sets random glass pane at a specific slot.
     *
     * @param slot slot to set at
     */
    public void setCustomGlassPane(final int slot) {
        getInventory().setItem(slot, getRandomGlassPane());
    }

    /**
     * @return the itemstack
     */
    public @NotNull final ItemStack getRandomGlassPane() {
        return MiscUtils.getRandomPaneColor().setDisplayName(" ").asItemStack();
    }

    protected final SettingsManager config = ConfigManager.getConfig();

    /**
     * Calls the crate open event and returns true/false if successful or not.
     *
     * @param keyType virtual or physical key
     * @param checkHand true or false
     * @param eventType {@link EventType}
     * @return true if cancelled otherwise false
     */
    public final boolean isCrateEventValid(@NotNull final KeyType keyType, final boolean checkHand, final boolean isSilent, final EventType eventType) {
        CrateOpenEvent event = new CrateOpenEvent(this.player, this.crate, keyType, checkHand, this.crate.getFile(), isSilent, eventType);
        event.callEvent();

        if (event.isCancelled()) {
            if (MiscUtils.isLogging()) {
                final String fileName = this.crate.getFileName();

                final String playerName = this.player.getName();

                if (this.config.getProperty(ConfigKeys.use_new_permission_system) && this.player.hasPermission("crazycrates.deny.open." + fileName)) {
                    this.logger.warn("{} could not open {} due to having the permission preventing them from opening the crate.", playerName, fileName);
                } else if (!this.player.hasPermission("crazycrates.open." + fileName)) {
                    this.logger.warn("{} could not open {} due to not having the permission required to open the crate.", playerName, fileName);
                } else if (!this.crate.canWinPrizes(this.player)) {
                    this.logger.warn("{} could not open {} due to no valid prizes being found which led to the event being cancelled.", playerName, fileName);
                }
            }
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
     * @return the display item of the picked prize
     */
    public ItemStack getDisplayItem() {
        return getCrate().pickPrize(getPlayer()).getDisplayItem(getPlayer(), getCrate());
    }

    /**
     * @return the display item of the picked prize with a tier
     */
    public ItemStack getDisplayItem(@NotNull final Tier tier) {
        return getCrate().pickPrize(getPlayer(), tier).getDisplayItem(getPlayer(), getCrate());
    }

    /**
     * Plays a sound at different volume levels with fallbacks.
     *
     * @param type stop, cycle or click sound
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

    public final void populateTiers() {
        final CosmicCrateManager manager = (CosmicCrateManager) this.crate.getManager();
        final LegacyItemBuilder itemBuilder = manager.getMysteryCrate().setPlayer(this.player);

        for (int slot = 0; slot <= this.size; slot++) {
            itemBuilder.addNamePlaceholder("%Slot%", String.valueOf(slot)).addLorePlaceholder("%Slot%", String.valueOf(slot));

            itemBuilder.setAmount(slot);

            final Tier tier = PrizeManager.getTier(this.crate);

            if (tier != null) {
                this.crateManager.addTier(this.player, slot, tier);

                getInventory().setItem(getInventory().firstEmpty(), itemBuilder.asItemStack());
            }
        }
    }
}