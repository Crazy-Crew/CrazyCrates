package com.badbones69.crazycrates.tasks;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.builders.types.CrateAdminMenu;
import com.badbones69.crazycrates.api.builders.types.CrateMainMenu;
import com.badbones69.crazycrates.api.builders.types.CratePrizeMenu;
import com.badbones69.crazycrates.api.builders.types.CrateTierMenu;
import com.badbones69.crazycrates.api.builders.types.CratePreviewMenu;
import com.badbones69.crazycrates.api.enums.PersistentKeys;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Tier;
import com.badbones69.crazycrates.config.ConfigManager;
import com.badbones69.crazycrates.config.impl.ConfigKeys;
import com.ryderbelserion.vital.paper.builders.items.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PaginationManager {

    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private final SettingsManager config = ConfigManager.getConfig();

    private ItemBuilder menuButton;
    private ItemBuilder nextButton;
    private ItemBuilder backButton;

    /**
     * Load all our buttons.
     */
    public void loadButtons() {
        this.menuButton = new ItemBuilder().withType(this.config.getProperty(ConfigKeys.menu_button_item))
                .setDisplayName(this.config.getProperty(ConfigKeys.menu_button_name))
                .setDisplayLore(this.config.getProperty(ConfigKeys.menu_button_lore))
                .setCustomModelData(this.config.getProperty(ConfigKeys.menu_button_model_data))
                .setPersistentString(PersistentKeys.main_menu_button.getNamespacedKey(), "none");

        this.nextButton = new ItemBuilder().withType(this.config.getProperty(ConfigKeys.next_button_item))
                .setDisplayName(this.config.getProperty(ConfigKeys.next_button_name))
                .setDisplayLore(this.config.getProperty(ConfigKeys.next_button_lore))
                .setCustomModelData(this.config.getProperty(ConfigKeys.next_button_model_data));

        this.backButton = new ItemBuilder().withType(this.config.getProperty(ConfigKeys.back_button_item))
                .setDisplayName(this.config.getProperty(ConfigKeys.back_button_name))
                .setDisplayLore(this.config.getProperty(ConfigKeys.back_button_lore))
                .setCustomModelData(this.config.getProperty(ConfigKeys.back_button_model_data));
    }

    private final Map<UUID, Integer> pages = new HashMap<>();

    /**
     * Get the preview items with optionally getting the preview tier items.
     *
     * @param player {@link Player}
     * @param crate {@link Crate}
     * @param page page number
     * @param isTier true or false
     * @param tier {@link Tier}
     * @return {@link ItemStack}
     */
    public final List<ItemStack> getPreviewItems(final Player player, final Crate crate, int page, final boolean isTier, final Tier tier) {
        final List<ItemStack> items = new ArrayList<>();

        if (page <= 0) page = 1;

        final List<ItemStack> prizes = !isTier && tier == null ? crate.getPreviewItems(player) : crate.getPreviewItems(tier, player);

        final int count = prizes.size();

        final int max = crate.getPerPage();

        int startIndex = page * max - max;

        int endIndex = Math.min(startIndex + max, count);

        for (;startIndex < endIndex; startIndex++) {
            if (startIndex < count) {
                items.add(prizes.get(startIndex));
            }
        }

        return items;
    }

    /**
     * Get the menu button.
     *
     * @param player {@link Player}
     * @return {@link ItemStack}
     */
    public final ItemStack getMenuButton(@NotNull final Player player) {
        return this.menuButton.setPlayer(player).getStack();
    }

    /**
     * Get the back button.
     *
     * @param player {@link Player}
     * @return {@link ItemStack}
     */
    public final ItemStack getBackButton(final Player player) {
        int page = getPage(player) - 1;

        ItemStack itemStack = this.backButton.addLorePlaceholder("{page}", String.valueOf(page)).getStack();

        itemStack.editMeta(itemMeta -> {
            final PersistentDataContainer container = itemMeta.getPersistentDataContainer();

            container.set(PersistentKeys.back_button.getNamespacedKey(), PersistentDataType.INTEGER, page);
        });

        return itemStack;
    }

    /**
     * Get the next button.
     *
     * @param player {@link Player}
     * @return {@link ItemStack}
     */
    public final ItemStack getNextButton(final Player player) {
        int page = getPage(player) + 1;

        ItemStack itemStack = this.nextButton.addLorePlaceholder("{page}", String.valueOf(page)).getStack();

        itemStack.editMeta(itemMeta -> {
            final PersistentDataContainer container = itemMeta.getPersistentDataContainer();

            container.set(PersistentKeys.next_button.getNamespacedKey(), PersistentDataType.INTEGER, page);
        });

        return itemStack;
    }

    public final List<ItemStack> getPreviewItems(final Player player, final Crate crate, int page) {
        return getPreviewItems(player, crate, page, false, null);
    }

    /**
     * Goes to the next page
     *
     * @param player {@link Player}
     * @param crate {@link Crate}
     * @param page page number
     */
    public void nextPage(final Player player, final Crate crate, final int page) {
        setPage(player, crate, page);

        buildInventory(player, crate, page);
    }

    /**
     * Goes back a page
     *
     * @param player {@link Player}
     * @param crate {@link Crate}
     * @param page page number
     */
    public void backPage(final Player player, final Crate crate, final int page) {
        setPage(player, crate, page);

        buildInventory(player, crate, page);
    }

    /**
     * Build the tier menu
     *
     * @param player {@link Player}
     * @param crate {@link Crate}
     * @param page page number
     */
    public void buildInventory(final Player player, final Crate crate, final int page) {
        player.openInventory(new CratePreviewMenu(
                player,
                crate,
                crate.getMaxSlots(),
                page <= 0 ? getPage(player) : page
        ).build().getInventory());
    }

    /**
     * Build the tier menu
     *
     * @param player {@link Player}
     * @param crate {@link Crate}
     * @param page page number
     * @param tier {@link Tier}
     */
    public void buildInventory(final Player player, final Crate crate, final int page, final Tier tier) {
        player.openInventory(new CratePreviewMenu(
                player,
                tier,
                crate,
                crate.getMaxSlots(),
                page <= 0 ? getPage(player) : page
        ).build().getInventory());
    }

    /**
     * Build the tier menu
     *
     * @param player {@link Player}
     * @param crate {@link Crate}
     */
    public void buildTierMenu(final Player player, final Crate crate) {
        player.openInventory(new CrateTierMenu(
                player,
                crate
        ).build().getInventory());
    }

    /**
     * Build the main menu
     *
     * @param player {@link Player}
     * @param config {@link SettingsManager}
     */
    public void buildMainMenu(final Player player, final SettingsManager config) {
        player.openInventory(new CrateMainMenu(
                player,
                config.getProperty(ConfigKeys.inventory_name),
                config.getProperty(ConfigKeys.inventory_size)
        ).build().getInventory());
    }

    /**
     * Remove the person viewing the gui.
     *
     * @param player {@link Player}
     */
    public void remove(final Player player) {
        this.pages.remove(player.getUniqueId());
    }

    /**
     * Get the page
     *
     * @param player {@link Player}
     * @return the page
     */
    public int getPage(final Player player) {
        return this.pages.getOrDefault(player.getUniqueId(), 1);
    }

    /**
     * Sets the page based on the crate prizes.
     *
     * @param player {@link Player}
     * @param crate {@link Crate}
     * @param page page number
     */
    public void setPage(final Player player, final Crate crate, int page) {
        int max = getMaxPages(crate);

        if (page > max) {
            page = max;
        }

        this.pages.put(player.getUniqueId(), page);
    }

    /**
     * Gets the max pages based on the preview sizes
     *
     * @param crate the {@link Crate}
     * @return a number
     */
    public int getMaxPages(final Crate crate) {
        return (int) Math.ceil((double) crate.getPreview().size() / crate.getPerPage());
    }

    /**
     * Checks if inventory is one of our holders.
     *
     * @param inventory {@link Inventory}
     * @return true or false
     */
    public boolean isInventory(final Inventory inventory) {
        final InventoryHolder holder = inventory.getHolder(false);

        return holder instanceof CrateAdminMenu || holder instanceof CrateMainMenu || holder instanceof CratePreviewMenu || holder instanceof CrateTierMenu;
    }
}