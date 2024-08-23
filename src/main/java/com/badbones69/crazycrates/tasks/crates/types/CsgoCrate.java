package com.badbones69.crazycrates.tasks.crates.types;

import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.api.PrizeManager;
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
import com.badbones69.crazycrates.api.utils.MiscUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CsgoCrate extends CrateBuilder {

    private @NotNull final CrateManager crateManager = this.plugin.getCrateManager();

    private @NotNull final BukkitUserManager userManager = this.plugin.getUserManager();

    public CsgoCrate(@NotNull final Crate crate, @NotNull final Player player, final int size) {
        super(crate, player, size);
    }

    @Override
    public void open(@NotNull final KeyType type, final boolean checkHand) {
        // Crate event failed so we return.
        if (isCrateEventValid(type, checkHand)) {
            return;
        }

        final Player player = getPlayer();
        final UUID uuid = player.getUniqueId();
        final Crate crate = getCrate();
        final String fileName = crate.getFileName();

        final boolean keyCheck = this.userManager.takeKeys(uuid, fileName, type, crate.useRequiredKeys() ? crate.getRequiredKeys() : 1, checkHand);

        if (!keyCheck) {
            // Remove from opening list.
            this.crateManager.removePlayerFromOpeningList(player);

            return;
        }

        // Set the glass/display items to the inventory.
        populate();

        // Open the inventory.
        player.openInventory(getInventory());

        addCrateTask(new FoliaRunnable(player.getScheduler(), null) {
            int time = 1;

            int full = 0;

            int open = 0;

            @Override
            public void run() {
                if (this.full <= 50) { // When Spinning
                    moveItemsAndSetGlass();

                    playSound("cycle-sound", Sound.Source.PLAYER, "block.note_block.xylophone");
                }

                this.open++;

                if (this.open >= 5) {
                    player.openInventory(getInventory());

                    this.open = 0;
                }

                this.full++;

                if (this.full > 51) {
                    if (MiscUtils.slowSpin(120, 15).contains(this.time)) { // When Slowing Down
                        moveItemsAndSetGlass();

                        playSound("cycle-sound", Sound.Source.PLAYER, "block.note_block.xylophone");
                    }

                    this.time++;

                    if (this.time == 60) { // When done
                        playSound("stop-sound", Sound.Source.PLAYER, "entity.player.levelup");

                        crateManager.endCrate(player);

                        final ItemStack itemStack = new ItemStack(Material.GRAY_STAINED_GLASS);

                        setItem(4, itemStack);
                        setItem(22, itemStack);

                        final ItemStack item = getInventory().getItem(13);

                        if (item != null) {
                            final Prize prize = crate.getPrize(item);

                            PrizeManager.givePrize(player, crate, prize);
                        }

                        crateManager.removePlayerFromOpeningList(player);

                        cancel();

                        new FoliaRunnable(player.getScheduler(), null) {
                            @Override
                            public void run() {
                                if (player.getOpenInventory().getTopInventory().equals(getInventory())) player.closeInventory();
                            }
                        }.runDelayed(plugin, 40);
                    } else if (this.time > 60) { // Added this due reports of the prizes spamming when low tps.
                        cancel();
                    }
                }
            }
        }.runAtFixedRate(this.plugin, 1, 1));
    }

    private void populate() {
        getBorder().forEach(this::setCustomGlassPane);

        // Set display items.
        for (int index = 9; index > 8 && index < 18; index++) {
            setItem(index, getCrate().pickPrize(getPlayer()).getDisplayItem(getPlayer()));
        }
    }

    private void moveItemsAndSetGlass() {
        final List<ItemStack> items = new ArrayList<>();

        final Player player = getPlayer();
        final Crate crate = getCrate();

        for (int i = 9; i > 8 && i < 17; i++) {
            items.add(getInventory().getItem(i));
        }

        setItem(9, crate.pickPrize(player).getDisplayItem(player));

        for (int i = 0; i < 8; i++) {
            setItem(i + 10, items.get(i));
        }

        getBorder().forEach(this::setCustomGlassPane);
    }

    @Override
    public void run() {

    }

    private List<Integer> getBorder() {
        return Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 18, 19, 20, 21, 22, 23, 24, 25, 26);
    }
}