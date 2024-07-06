package com.badbones69.crazycrates.tasks;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.api.enums.PersistentKeys;
import com.badbones69.crazycrates.api.objects.Crate;
import com.ryderbelserion.vital.paper.builders.items.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.badbones69.crazycrates.config.ConfigManager;
import com.badbones69.crazycrates.config.impl.ConfigKeys;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InventoryManager {

    private @NotNull final SettingsManager config = ConfigManager.getConfig();

    private ItemBuilder menuButton;
    private ItemBuilder nextButton;
    private ItemBuilder backButton;

    public void loadButtons() {
        this.menuButton = new ItemBuilder().withType(this.config.getProperty(ConfigKeys.menu_button_item))
                .setDisplayName(this.config.getProperty(ConfigKeys.menu_button_name))
                .setDisplayLore(this.config.getProperty(ConfigKeys.menu_button_lore))
                .setPersistentString(PersistentKeys.main_menu_button.getNamespacedKey(), "none");

        this.nextButton = new ItemBuilder().withType(this.config.getProperty(ConfigKeys.next_button_item))
                .setDisplayName(this.config.getProperty(ConfigKeys.next_button_name))
                .setDisplayLore(this.config.getProperty(ConfigKeys.next_button_lore))
                .setPersistentString(PersistentKeys.next_button.getNamespacedKey(), "none");

        this.backButton = new ItemBuilder().withType(this.config.getProperty(ConfigKeys.back_button_item))
                .setDisplayName(this.config.getProperty(ConfigKeys.back_button_name))
                .setDisplayLore(this.config.getProperty(ConfigKeys.back_button_lore))
                .setPersistentString(PersistentKeys.back_button.getNamespacedKey(), "none");
    }

    public @NotNull final ItemStack getMenuButton(@NotNull final Player player) {
        return this.menuButton.setPlayer(player).getStack();
    }

    public @NotNull final ItemStack getNextButton(@Nullable final Player player) {
        ItemBuilder button = new ItemBuilder(this.nextButton);

        if (player != null) {
            button.setPlayer(player).addLorePlaceholder("{page}", (getPage(player) + 1) + "");
        }

        return button.getStack();
    }

    public @NotNull final ItemStack getBackButton(@Nullable final Player player) {
        ItemBuilder button = new ItemBuilder(this.backButton);

        if (player != null) {
            button.setPlayer(player).addLorePlaceholder("{page}", (getPage(player) - 1) + "");
        }

        return button.getStack();
    }

    private final Map<UUID, Crate> crateViewers = new HashMap<>();

    public void openNewCratePreview(@NotNull final Player player, @NotNull final Crate crate) {
        this.crateViewers.put(player.getUniqueId(), crate);

        if (crate.isPreviewTierToggle()) {
            player.openInventory(crate.getTierPreview(player));

            return;
        }

        setPage(player, 1);

        player.openInventory(crate.getPreview(player));
    }

    public void addCrateViewer(@NotNull final Player player, @NotNull final Crate crate) {
        this.crateViewers.put(player.getUniqueId(), crate);
    }

    public void openCratePreview(@NotNull final Player player, @NotNull final Crate crate) {
        this.crateViewers.put(player.getUniqueId(), crate);

        player.openInventory(crate.getPreview(player));
    }

    public void closeCratePreview(@NotNull final Player player) {
        this.pageViewers.remove(player.getUniqueId());
        this.viewers.remove(player.getUniqueId());
        this.crateViewers.remove(player.getUniqueId());

        player.closeInventory();
    }

    public @Nullable final Crate getCratePreview(@NotNull final Player player) {
        return this.crateViewers.get(player.getUniqueId());
    }

    public void removeCrateViewer(@NotNull final Player player) {
        this.crateViewers.remove(player.getUniqueId());
    }

    public void removePageViewer(@NotNull final Player player) {
        this.pageViewers.remove(player.getUniqueId());
    }

    public final boolean inCratePreview(@NotNull final Player player) {
        return this.crateViewers.containsKey(player.getUniqueId());
    }

    private final Map<UUID, Integer> pageViewers = new HashMap<>();

    public void nextPage(@NotNull final Player player) {
        setPage(player, getPage(player) + 1);
    }

    public void backPage(@NotNull final Player player) {
        setPage(player, getPage(player) - 1);
    }

    public int getPage(@NotNull final Player player) {
        return this.pageViewers.getOrDefault(player.getUniqueId(), 1);
    }

    public void setPage(@NotNull final Player player, int page) {
        int max = this.crateViewers.get(player.getUniqueId()).getMaxPage();

        if (page < 1) {
            page = 1;
        } else if (page >= max) {
            page = max;
        }

        this.pageViewers.put(player.getUniqueId(), page);
    }

    private final List<UUID> viewers = new ArrayList<>();

    public void addViewer(@NotNull final Player player) {
        this.viewers.add(player.getUniqueId());
    }

    public void removeViewer(@NotNull final Player player) {
        this.viewers.remove(player.getUniqueId());
    }

    public void purge() {
        this.viewers.clear();
    }

    public @NotNull final List<UUID> getViewers() {
        return Collections.unmodifiableList(this.viewers);
    }
}