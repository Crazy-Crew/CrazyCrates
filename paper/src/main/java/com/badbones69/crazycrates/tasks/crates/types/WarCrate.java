package com.badbones69.crazycrates.tasks.crates.types;

import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.other.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.api.builders.CrateBuilder;
import com.badbones69.crazycrates.api.builders.types.CratePrizeMenu;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import java.util.HashMap;

public class WarCrate extends CrateBuilder {

    private final HashMap<ItemStack, String> colorCodes = new HashMap<>();

    public WarCrate(Crate crate, Player player, int size) {
        super(crate, player, size);
    }

    @Override
    public void open(KeyType type, boolean checkHand) {
        // Crate event failed so we return.
        if (isCrateEventValid(type, checkHand)) {
            return;
        }

        boolean keyCheck = this.plugin.getCrazyHandler().getUserManager().takeKeys(1, getPlayer().getUniqueId(), getCrate().getName(), type, checkHand);

        if (!keyCheck) {
            // Send the message about failing to take the key.
            MiscUtils.failedToTakeKey(getPlayer(), getCrate());

            // Remove from opening list.
            this.plugin.getCrateManager().removePlayerFromOpeningList(getPlayer());

            // Remove closer/picker
            this.plugin.getCrateManager().removeCloser(getPlayer());
            this.plugin.getCrateManager().removePicker(getPlayer());

            return;
        }

        this.plugin.getCrateManager().addPicker(getPlayer(), false);
        this.plugin.getCrateManager().addCloser(getPlayer(), false);

        addCrateTask(new BukkitRunnable() {
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
                    plugin.getCrateManager().addPicker(getPlayer(), true);
                }
            }
        }.runTaskTimer(this.plugin, 1, 3));
    }

    private void setRandomPrizes() {
        if (!this.plugin.getCrateManager().isInOpeningList(getPlayer()) && !(getInventory().getHolder(false) instanceof CratePrizeMenu)) return;

        for (int index = 0; index < 9; index++) {
            setItem(index, getCrate().pickPrize(getPlayer()).getDisplayItem(getPlayer()));
        }
    }

    private void setRandomGlass() {
        if (!this.plugin.getCrateManager().isInOpeningList(getPlayer()) && !(getInventory().getHolder(false) instanceof CratePrizeMenu)) return;

        if (this.colorCodes.isEmpty()) getColorCode();

        ItemBuilder builder = MiscUtils.getRandomPaneColor();
        builder.setName("&" + this.colorCodes.get(builder.build()) + "&l???");
        ItemStack item = builder.build();

        for (int index = 0; index < 9; index++) {
            setItem(index, item);
        }
    }

    private void getColorCode() {
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.WHITE_STAINED_GLASS_PANE).build(), "f");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.ORANGE_STAINED_GLASS_PANE).build(), "6");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.MAGENTA_STAINED_GLASS_PANE).build(), "d");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.LIGHT_BLUE_STAINED_GLASS_PANE).build(), "3");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.YELLOW_STAINED_GLASS_PANE).build(), "e");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.LIME_STAINED_GLASS_PANE).build(), "a");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.PINK_STAINED_GLASS_PANE).build(), "c");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.GRAY_STAINED_GLASS_PANE).build(), "7");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.LIGHT_GRAY_STAINED_GLASS_PANE).build(), "7");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.CYAN_STAINED_GLASS_PANE).build(), "3");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.PURPLE_STAINED_GLASS_PANE).build(), "5");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.BLUE_STAINED_GLASS_PANE).build(), "9");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.BROWN_STAINED_GLASS_PANE).build(), "6");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.GREEN_STAINED_GLASS_PANE).build(), "2");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.RED_STAINED_GLASS_PANE).build(), "4");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.BLACK_STAINED_GLASS_PANE).build(), "8");
    }

    @Override
    public void run() {

    }
}