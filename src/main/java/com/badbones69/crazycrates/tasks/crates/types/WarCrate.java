package com.badbones69.crazycrates.tasks.crates.types;

import com.badbones69.crazycrates.api.objects.Crate;
import com.ryderbelserion.vital.paper.builders.items.ItemBuilder;
import com.ryderbelserion.vital.paper.util.scheduler.FoliaRunnable;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.api.builders.CrateBuilder;
import com.badbones69.crazycrates.api.builders.types.CratePrizeMenu;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WarCrate extends CrateBuilder {

    private @NotNull final CrateManager crateManager = this.plugin.getCrateManager();

    private @NotNull final BukkitUserManager userManager = this.plugin.getUserManager();

    private final Map<Material, String> colorCodes = new HashMap<>();

    public WarCrate(@NotNull final Crate crate, @NotNull final Player player, final int size) {
        super(crate, player, size);
    }

    private boolean isOpen = false;

    @Override
    public void open(@NotNull final KeyType type, final boolean checkHand) {
        // Crate event failed so we return.
        if (isCrateEventValid(type, checkHand)) {
            return;
        }

        final Player player = getPlayer();
        final UUID uuid = player.getUniqueId();
        final Crate crate = getCrate();
        final String crateName = crate.getName();

        final boolean keyCheck = this.userManager.takeKeys(uuid, crateName, type, 1, checkHand);

        if (!keyCheck) {
            // Remove from opening list.
            this.crateManager.removePlayerFromOpeningList(player);

            // Remove closer/picker
            this.crateManager.removeCloser(player);
            this.crateManager.removePicker(player);

            return;
        }

        this.crateManager.addPicker(player, false);
        this.crateManager.addCloser(player, false);

        setRandomPrizes();

        player.openInventory(getInventory());

        addCrateTask(new FoliaRunnable(player.getScheduler(), null) {
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
        final Player player = getPlayer();
        final Crate crate = getCrate();

        if (!this.crateManager.isInOpeningList(player) && !(getInventory().getHolder(false) instanceof CratePrizeMenu)) return;

        for (int index = 0; index < 9; index++) {
            setItem(index, crate.pickPrize(player).getDisplayItem(player));
        }
    }

    private void setRandomGlass() {
        final Player player = getPlayer();

        if (!this.crateManager.isInOpeningList(player) && !(getInventory().getHolder(false) instanceof CratePrizeMenu)) return;

        if (this.colorCodes.isEmpty()) getColorCode();

        final ItemBuilder builder = MiscUtils.getRandomPaneColor();
        builder.setDisplayName("<" + this.colorCodes.get(builder.getType()) + "><bold>???</bold>");
        final ItemStack item = builder.getStack();

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

    @Override
    public void run() {

    }
}