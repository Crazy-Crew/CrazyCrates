package com.badbones69.crazycrates.tasks.crates.types;

import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.api.PrizeManager;
import com.ryderbelserion.vital.paper.util.scheduler.FoliaRunnable;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.api.builders.CrateBuilder;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import java.util.UUID;

public class RouletteCrate extends CrateBuilder {

    private @NotNull final CrateManager crateManager = this.plugin.getCrateManager();

    private @NotNull final BukkitUserManager userManager = this.plugin.getUserManager();

    public RouletteCrate(@NotNull final Crate crate, @NotNull final Player player, final int size) {
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

        setItem(13, getCrate().pickPrize(getPlayer()).getDisplayItem(player));

        addCrateTask(new FoliaRunnable(player.getScheduler(), null) {
            int full = 0;
            int time = 1;

            int even = 0;
            int open = 0;

            @Override
            public void run() {
                if (this.full <= 15) {
                    setItem(13, crate.pickPrize(player).getDisplayItem(player));
                    setGlass();

                    if (this.full >= 2) {
                        playSound("cycle-sound", Sound.Source.PLAYER, "block.note_block.xylophone");
                    }

                    this.even++;

                    if (this.even >= 4) {
                        this.even = 0;

                        setItem(13, crate.pickPrize(player).getDisplayItem(player));
                    }
                }

                this.open++;

                if (this.open >= 5) {
                    player.openInventory(getInventory());

                    this.open = 0;
                }

                this.full++;

                if (this.full > 16) {
                    if (MiscUtils.slowSpin(46, 9).contains(this.time)) {
                        setGlass();

                        setItem(13, crate.pickPrize(player).getDisplayItem(player));

                        playSound("cycle-sound", Sound.Source.PLAYER, "block.note_block.xylophone");
                    }

                    this.time++;

                    if (this.time >= 23) {
                        playSound("stop-sound", Sound.Source.PLAYER, "entity.player.levelup");

                        crateManager.endCrate(player);

                        final ItemStack item = getInventory().getItem(13);

                        if (item != null) {
                            Prize prize = crate.getPrize(item);
                            PrizeManager.givePrize(player, crate, prize);
                        }

                        crateManager.removePlayerFromOpeningList(player);

                        new FoliaRunnable(player.getScheduler(), null) {
                            @Override
                            public void run() {
                                if (player.getOpenInventory().getTopInventory().equals(getInventory())) player.closeInventory(InventoryCloseEvent.Reason.UNLOADED);
                            }
                        }.runDelayed(plugin, 40);
                    }
                }
            }
        }.runAtFixedRate(this.plugin, 2, 2));
    }

    private void setGlass() {
        for (int slot = 0; slot < getSize(); slot++) {
            if (slot != 13) {
                setCustomGlassPane(slot);
            }
        }
    }

    @Override
    public void run() {

    }
}