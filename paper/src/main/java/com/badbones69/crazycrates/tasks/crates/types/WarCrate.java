package com.badbones69.crazycrates.tasks.crates.types;

import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.builders.ItemBuilder;
import com.badbones69.crazycrates.scheduler.FoliaRunnable;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.api.builders.CrateBuilder;
import com.badbones69.crazycrates.api.builders.types.CratePrizeMenu;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import java.util.HashMap;
import java.util.Map;

public class WarCrate extends CrateBuilder {

    private final @NotNull CrateManager crateManager = this.plugin.getCrateManager();

    private final @NotNull BukkitUserManager userManager = this.plugin.getUserManager();

    private final Map<ItemStack, String> colorCodes = new HashMap<>();

    public WarCrate(Crate crate, Player player, int size) {
        super(crate, player, size);
    }

    @Override
    public void open(KeyType type, boolean checkHand) {
        // Crate event failed so we return.
        if (isCrateEventValid(type, checkHand)) {
            return;
        }

        boolean keyCheck = this.userManager.takeKeys(1, getPlayer().getUniqueId(), getCrate().getName(), type, checkHand);

        if (!keyCheck) {
            // Remove from opening list.
            this.crateManager.removePlayerFromOpeningList(getPlayer());

            // Remove closer/picker
            this.crateManager.removeCloser(getPlayer());
            this.crateManager.removePicker(getPlayer());

            return;
        }

        this.crateManager.addPicker(getPlayer(), false);
        this.crateManager.addCloser(getPlayer(), false);

        addCrateTask(new FoliaRunnable(getPlayer().getScheduler(), null) {
            int full = 0;
            int open = 0;

            @Override
            public void run() {
                if (this.full < 25) {
                    setRandomPrizes();

                    playSound("cycle-sound", SoundCategory.PLAYERS, "BLOCK_LAVA_POP");
                }

                this.open++;

                if (this.open >= 3) {
                    getPlayer().openInventory(getInventory());

                    this.open = 0;
                }

                this.full++;

                if (this.full == 26) {
                    playSound("stop-sound", SoundCategory.PLAYERS, "BLOCK_LAVA_POP");

                    setRandomGlass();

                    crateManager.addPicker(getPlayer(), true);
                }
            }
        }.runAtFixedRate(this.plugin, 1, 3));
    }

    private void setRandomPrizes() {
        if (!this.crateManager.isInOpeningList(getPlayer()) && !(getInventory().getHolder(false) instanceof CratePrizeMenu)) return;

        for (int index = 0; index < 9; index++) {
            setItem(index, getCrate().pickPrize(getPlayer()).getDisplayItem(getPlayer()));
        }
    }

    private void setRandomGlass() {
        if (!this.crateManager.isInOpeningList(getPlayer()) && !(getInventory().getHolder(false) instanceof CratePrizeMenu)) return;

        if (this.colorCodes.isEmpty()) getColorCode();

        ItemBuilder builder = MiscUtils.getRandomPaneColor();
        builder.setName("<" + this.colorCodes.get(builder.build()) + "><bold>???</bold>");
        ItemStack item = builder.build();

        for (int index = 0; index < 9; index++) {
            setItem(index, item);
        }
    }

    private void getColorCode() {
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.WHITE_STAINED_GLASS_PANE).build(), "white");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.ORANGE_STAINED_GLASS_PANE).build(), "gold");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.MAGENTA_STAINED_GLASS_PANE).build(), "light_purple");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.LIGHT_BLUE_STAINED_GLASS_PANE).build(), "dark_aqua");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.YELLOW_STAINED_GLASS_PANE).build(), "yellow");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.LIME_STAINED_GLASS_PANE).build(), "green");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.PINK_STAINED_GLASS_PANE).build(), "red");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.GRAY_STAINED_GLASS_PANE).build(), "dark_gray");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.LIGHT_GRAY_STAINED_GLASS_PANE).build(), "gray");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.CYAN_STAINED_GLASS_PANE).build(), "aqua");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.PURPLE_STAINED_GLASS_PANE).build(), "dark_purple");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.BLUE_STAINED_GLASS_PANE).build(), "dark_blue");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.BROWN_STAINED_GLASS_PANE).build(), "gold");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.GREEN_STAINED_GLASS_PANE).build(), "green");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.RED_STAINED_GLASS_PANE).build(), "dark_red");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.BLACK_STAINED_GLASS_PANE).build(), "black");
    }

    @Override
    public void run() {

    }
}