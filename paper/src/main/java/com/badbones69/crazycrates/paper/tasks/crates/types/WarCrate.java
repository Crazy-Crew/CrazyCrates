package com.badbones69.crazycrates.paper.tasks.crates.types;

import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.managers.events.enums.EventType;
import com.ryderbelserion.fusion.paper.builders.ItemBuilder;
import com.ryderbelserion.fusion.paper.scheduler.FoliaScheduler;
import net.kyori.adventure.sound.Sound;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.paper.api.builders.CrateBuilder;
import com.badbones69.crazycrates.paper.tasks.menus.CratePrizeMenu;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WarCrate extends CrateBuilder {

    private final Map<NamespacedKey, String> colorCodes = new HashMap<>();

    public WarCrate(@NotNull final Crate crate, @NotNull final Player player, final int size) {
        super(crate, player, size);
    }

    private final Inventory inventory = getInventory();
    private final Player player = getPlayer();
    private final UUID uuid = this.player.getUniqueId();
    private final Crate crate = getCrate();

    @Override
    public void open(@NotNull final KeyType type, final boolean checkHand, final boolean isSilent, @Nullable final EventType eventType) {
        // Crate event failed, so we return.
        if (isCrateEventValid(type, checkHand, isSilent, eventType)) {
            return;
        }

        final String fileName = this.crate.getFileName();

        final boolean keyCheck = this.userManager.takeKeys(this.uuid, fileName, type, this.crate.useRequiredKeys() ? this.crate.getRequiredKeys() : 1, checkHand);

        if (!keyCheck) {
            // Remove from an opening list.
            this.crateManager.removePlayerFromOpeningList(this.player);

            // Remove closer/picker
            this.crateManager.removeCloser(this.player);
            this.crateManager.removePicker(this.player);

            return;
        }

        this.crateManager.addPicker(this.player, false);
        this.crateManager.addCloser(this.player, false);

        setRandomPrizes();

        this.player.openInventory(this.inventory);

        addCrateTask(new FoliaScheduler(this.plugin, null, this.player) {
            int full = 0;
            int open = 0;

            @Override
            public void run() {
                if (this.full < 25) {
                    setRandomPrizes();

                    playSound("cycle-sound", Sound.Source.MASTER, "block.lava.pop");
                }

                this.open++;

                if (this.open >= 3) {
                    player.updateInventory();

                    this.open = 0;
                }

                this.full++;

                if (this.full == 26) {
                    playSound("stop-sound", Sound.Source.MASTER, "block.lava.pop");

                    setRandomGlass();

                    crateManager.addPicker(player, true);
                }
            }
        }.runAtFixedRate(1, 3));
    }

    private void setRandomPrizes() {
        if (!this.crateManager.isInOpeningList(this.player) && !(this.inventory.getHolder(false) instanceof CratePrizeMenu)) return;

        for (int index = 0; index < 9; index++) {
            setItem(index, this.crate.pickPrize(this.player).getDisplayItem(this.player, this.crate));
        }
    }

    private void setRandomGlass() {
        final Player player = getPlayer();

        if (!this.crateManager.isInOpeningList(player) && !(this.inventory.getHolder(false) instanceof CratePrizeMenu)) return;

        if (this.colorCodes.isEmpty()) getColorCode();

        final ItemBuilder builder = MiscUtils.getRandomPaneColor();
        
        builder.withDisplayName("<" + this.colorCodes.get(builder.getType().getKey()) + "><bold>???</bold>");
        
        final ItemStack item = builder.asItemStack();

        for (int index = 0; index < 9; index++) {
            setItem(index, item);
        }
    }

    private void getColorCode() {
        this.colorCodes.put(ItemType.WHITE_STAINED_GLASS_PANE.getKey(), "white");
        this.colorCodes.put(ItemType.ORANGE_STAINED_GLASS_PANE.getKey(), "gold");
        this.colorCodes.put(ItemType.MAGENTA_STAINED_GLASS_PANE.getKey(), "light_purple");
        this.colorCodes.put(ItemType.LIGHT_BLUE_STAINED_GLASS_PANE.getKey(), "dark_aqua");
        this.colorCodes.put(ItemType.YELLOW_STAINED_GLASS_PANE.getKey(), "yellow");
        this.colorCodes.put(ItemType.LIME_STAINED_GLASS_PANE.getKey(), "green");
        this.colorCodes.put(ItemType.PINK_STAINED_GLASS_PANE.getKey(), "red");
        this.colorCodes.put(ItemType.GRAY_STAINED_GLASS_PANE.getKey(), "dark_gray");
        this.colorCodes.put(ItemType.LIGHT_GRAY_STAINED_GLASS_PANE.getKey(), "gray");
        this.colorCodes.put(ItemType.CYAN_STAINED_GLASS_PANE.getKey(), "aqua");
        this.colorCodes.put(ItemType.PURPLE_STAINED_GLASS_PANE.getKey(), "dark_purple");
        this.colorCodes.put(ItemType.BLUE_STAINED_GLASS_PANE.getKey(), "dark_blue");
        this.colorCodes.put(ItemType.BROWN_STAINED_GLASS_PANE.getKey(), "gold");
        this.colorCodes.put(ItemType.GREEN_STAINED_GLASS_PANE.getKey(), "green");
        this.colorCodes.put(ItemType.RED_STAINED_GLASS_PANE.getKey(), "dark_red");
        this.colorCodes.put(ItemType.BLACK_STAINED_GLASS_PANE.getKey(), "black");
    }
}