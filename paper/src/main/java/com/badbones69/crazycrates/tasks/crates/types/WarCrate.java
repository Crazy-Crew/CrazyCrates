package com.badbones69.crazycrates.tasks.crates.types;

import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.builders.ItemBuilder;
import com.badbones69.crazycrates.managers.events.enums.EventType;
import com.ryderbelserion.vital.paper.util.scheduler.FoliaRunnable;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.api.builders.CrateBuilder;
import com.badbones69.crazycrates.tasks.menus.CratePrizeMenu;
import com.badbones69.crazycrates.utils.MiscUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WarCrate extends CrateBuilder {

    private final Map<Material, String> colorCodes = new HashMap<>();

    public WarCrate(@NotNull final Crate crate, @NotNull final Player player, final int size) {
        super(crate, player, size);
    }

    private final Inventory inventory = getInventory();
    private final Player player = getPlayer();
    private final UUID uuid = this.player.getUniqueId();
    private final Crate crate = getCrate();

    @Override
    public void open(@NotNull final KeyType type, final boolean checkHand, final boolean isSilent, final EventType eventType) {
        // Crate event failed so we return.
        if (isCrateEventValid(type, checkHand, isSilent, eventType)) {
            return;
        }

        final String fileName = this.crate.getFileName();

        final boolean keyCheck = this.userManager.takeKeys(this.uuid, fileName, type, this.crate.useRequiredKeys() ? this.crate.getRequiredKeys() : 1, checkHand);

        if (!keyCheck) {
            // Remove from opening list.
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

        addCrateTask(new FoliaRunnable(this.player.getScheduler(), null) {
            int full = 0;
            int open = 0;

            @Override
            public void run() {
                if (this.full < 25) {
                    setRandomPrizes();

                    playSound("cycle-sound", Sound.Source.PLAYER, "block.lava.pop");
                }

                this.open++;

                if (this.open >= 3) {
                    player.updateInventory();

                    this.open = 0;
                }

                this.full++;

                if (this.full == 26) {
                    playSound("stop-sound", Sound.Source.PLAYER, "block.lava.pop");

                    setRandomGlass();

                    crateManager.addPicker(player, true);
                }
            }
        }.runAtFixedRate(this.plugin, 1, 3));
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
        builder.setDisplayName("<" + this.colorCodes.get(builder.getType()) + "><bold>???</bold>");
        final ItemStack item = builder.asItemStack();

        for (int index = 0; index < 9; index++) {
            setItem(index, item);
        }
    }

    private void getColorCode() {
        this.colorCodes.put(Material.WHITE_STAINED_GLASS_PANE, "white");
        this.colorCodes.put(Material.ORANGE_STAINED_GLASS_PANE, "gold");
        this.colorCodes.put(Material.MAGENTA_STAINED_GLASS_PANE, "light_purple");
        this.colorCodes.put(Material.LIGHT_BLUE_STAINED_GLASS_PANE, "dark_aqua");
        this.colorCodes.put(Material.YELLOW_STAINED_GLASS_PANE, "yellow");
        this.colorCodes.put(Material.LIME_STAINED_GLASS_PANE, "green");
        this.colorCodes.put(Material.PINK_STAINED_GLASS_PANE, "red");
        this.colorCodes.put(Material.GRAY_STAINED_GLASS_PANE, "dark_gray");
        this.colorCodes.put(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "gray");
        this.colorCodes.put(Material.CYAN_STAINED_GLASS_PANE, "aqua");
        this.colorCodes.put(Material.PURPLE_STAINED_GLASS_PANE, "dark_purple");
        this.colorCodes.put(Material.BLUE_STAINED_GLASS_PANE, "dark_blue");
        this.colorCodes.put(Material.BROWN_STAINED_GLASS_PANE, "gold");
        this.colorCodes.put(Material.GREEN_STAINED_GLASS_PANE, "green");
        this.colorCodes.put(Material.RED_STAINED_GLASS_PANE, "dark_red");
        this.colorCodes.put(Material.BLACK_STAINED_GLASS_PANE, "black");
    }
}