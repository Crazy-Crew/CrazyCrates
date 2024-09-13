package com.badbones69.crazycrates.tasks;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.enums.misc.Keys;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Tier;
import com.badbones69.crazycrates.api.builders.ItemBuilder;
import com.ryderbelserion.vital.paper.api.builders.gui.types.PaginatedGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.badbones69.crazycrates.config.ConfigManager;
import com.badbones69.crazycrates.config.impl.ConfigKeys;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("WhileLoopReplaceableByForEach")
public class InventoryManager {

    private final SettingsManager config = ConfigManager.getConfig();
    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private ItemBuilder menuButton;
    private ItemBuilder nextButton;
    private ItemBuilder backButton;

    public void loadButtons() {
        this.menuButton = new ItemBuilder().withType(this.config.getProperty(ConfigKeys.menu_button_item).toLowerCase())
                .setDisplayName(this.config.getProperty(ConfigKeys.menu_button_name))
                .setDisplayLore(this.config.getProperty(ConfigKeys.menu_button_lore))
                .setCustomModelData(this.config.getProperty(ConfigKeys.menu_button_model_data));

        this.nextButton = new ItemBuilder().withType(this.config.getProperty(ConfigKeys.next_button_item).toLowerCase())
                .setDisplayName(this.config.getProperty(ConfigKeys.next_button_name))
                .setDisplayLore(this.config.getProperty(ConfigKeys.next_button_lore))
                .setCustomModelData(this.config.getProperty(ConfigKeys.next_button_model_data));

        this.backButton = new ItemBuilder().withType(this.config.getProperty(ConfigKeys.back_button_item).toLowerCase())
                .setDisplayName(this.config.getProperty(ConfigKeys.back_button_name))
                .setDisplayLore(this.config.getProperty(ConfigKeys.back_button_lore))
                .setCustomModelData(this.config.getProperty(ConfigKeys.back_button_model_data));
    }

    public final ItemStack getMenuButton(@NotNull final Player player) {
        return this.menuButton.setPlayer(player).asItemStack();
    }

    public final ItemStack getNextButton(@Nullable final Player player, @Nullable final Tier tier, @NotNull final PaginatedGui gui) {
        final ItemBuilder button = new ItemBuilder(this.nextButton);

        if (player != null) {
            button.setPlayer(player).addLorePlaceholder("{page}", String.valueOf(gui.getNextPageNumber()));
        }

        if (tier != null) {
            button.setPersistentString(Keys.crate_tier.getNamespacedKey(), tier.getName());
        }

        return button.asItemStack();
    }

    public final ItemStack getNextButton(@Nullable final Player player, @NotNull final PaginatedGui gui) {
        return getNextButton(player, null, gui);
    }

    public final ItemStack getBackButton(@Nullable final Player player, @Nullable final Tier tier, @NotNull final PaginatedGui gui) {
        final ItemBuilder button = new ItemBuilder(this.backButton);

        if (player != null) {
            button.setPlayer(player).addLorePlaceholder("{page}", String.valueOf(gui.getPreviousPageNumber()));
        }

        if (tier != null) {
            button.setPersistentString(Keys.crate_tier.getNamespacedKey(), tier.getName());
        }

        return button.asItemStack();
    }

    public final ItemStack getBackButton(@Nullable final Player player, @NotNull final PaginatedGui gui) {
        return getBackButton(player, null, gui);
    }

    public void openNewCratePreview(@NotNull final Player player, @NotNull final Crate crate) {
        if (crate.getCrateType() == CrateType.casino || crate.getCrateType() == CrateType.cosmic && crate.isPreviewTierToggle()) {
            crate.getTierPreview(player).open();

            return;
        }

        crate.getPreview(player).open();
    }

    private final List<UUID> previewViewers = new ArrayList<>();

    public void addPreviewViewer(final UUID uuid) {
        this.previewViewers.add(uuid);
    }

    public void removePreviewViewer(final UUID uuid) {
        this.previewViewers.remove(uuid);
    }

    public final List<UUID> getPreviewViewers() {
        return Collections.unmodifiableList(this.previewViewers);
    }

    public final boolean hasPreviewViewer(final UUID uuid) {
        return this.previewViewers.contains(uuid);
    }

    public void openPreview(final Crate crate) {
        final Iterator<UUID> viewers = getPreviewViewers().iterator();

        while (viewers.hasNext()) {
            final UUID uuid = viewers.next();

            final Player player = this.plugin.getServer().getPlayer(uuid);

            if (player == null || !player.isOnline()) {
                removePreviewViewer(uuid);

                continue;
            }

            openNewCratePreview(player, crate);
        }
    }

    public void closePreview() {
        final Iterator<UUID> viewers = getPreviewViewers().iterator();

        while (viewers.hasNext()) {
            final UUID uuid = viewers.next();

            final Player player = this.plugin.getServer().getPlayer(uuid);

            if (player == null || !player.isOnline()) {
                removePreviewViewer(uuid);

                continue;
            }

            player.closeInventory(InventoryCloseEvent.Reason.UNLOADED);

            if (this.config.getProperty(ConfigKeys.send_preview_taken_out_message)) {
                Messages.reloaded_forced_out_of_preview.sendMessage(player);
            }
        }
    }
}