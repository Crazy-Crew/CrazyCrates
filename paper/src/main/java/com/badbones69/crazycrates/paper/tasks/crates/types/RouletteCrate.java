package com.badbones69.crazycrates.paper.tasks.crates.types;

import com.badbones69.crazycrates.paper.api.builders.types.features.CrateSpinMenu;
import com.badbones69.crazycrates.paper.api.enums.other.keys.FileKeys;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import com.badbones69.crazycrates.paper.api.PrizeManager;
import com.badbones69.crazycrates.paper.api.objects.gui.GuiSettings;
import com.badbones69.crazycrates.paper.managers.events.enums.EventType;
import com.ryderbelserion.fusion.paper.api.scheduler.FoliaScheduler;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.paper.api.builders.CrateBuilder;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import java.util.UUID;

public class RouletteCrate extends CrateBuilder {

    public RouletteCrate(@NotNull final Crate crate, @NotNull final Player player, final int size) {
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
            this.crateManager.removePlayerFromOpeningList(player);

            return;
        }

        setItem(13, this.crate.pickPrize(this.player).getDisplayItem(this.player, this.crate));

        final boolean isGlassBorderToggled = this.crate.isGlassBorderToggled();

        addCrateTask(new FoliaScheduler(null, this.player) {
            int full = 0;
            int time = 1;

            int even = 0;
            int open = 0;

            @Override
            public void run() {
                if (this.full <= 15) {
                    setItem(13, crate.pickPrize(player).getDisplayItem(player, crate));

                    if (isGlassBorderToggled) {
                        setGlass();
                    }

                    if (this.full >= 2) {
                        playSound("cycle-sound", Sound.Source.MASTER, "block.note_block.xylophone");
                    }

                    this.even++;

                    if (this.even >= 4) {
                        this.even = 0;

                        setItem(13, crate.pickPrize(player).getDisplayItem(player, crate));
                    }
                }

                this.open++;

                if (this.open >= 5) {
                    player.openInventory(inventory);

                    this.open = 0;
                }

                this.full++;

                if (this.full > 16) {
                    if (MiscUtils.slowSpin(46, 9).contains(this.time)) {
                        if (isGlassBorderToggled) {
                            setGlass();
                        }

                        setItem(13, crate.pickPrize(player).getDisplayItem(player, crate));

                        playSound("cycle-sound", Sound.Source.MASTER, "block.note_block.xylophone");
                    }

                    this.time++;

                    if (this.time >= 23) {
                        playSound("stop-sound", Sound.Source.MASTER, "entity.player.levelup");

                        crateManager.endCrate(player);

                        final ItemStack item = inventory.getItem(13);

                        if (item != null) {
                            Prize prize = crate.getPrize(item);

                            if (crate.isCyclePrize() && !PrizeManager.isCapped(crate, player)) { // re-open this menu
                                new CrateSpinMenu(player, new GuiSettings(crate, prize, FileKeys.respin_gui.getConfiguration())).open();

                                crateManager.removePlayerFromOpeningList(player);

                                return;
                            } else {
                                userManager.removeRespinPrize(uuid, fileName);

                                if (!crate.isCyclePersistRestart()) {
                                    userManager.removeRespinCrate(uuid, fileName, userManager.getCrateRespin(uuid, fileName));
                                }
                            }

                            PrizeManager.givePrize(player, crate, prize);
                        }

                        crateManager.removePlayerFromOpeningList(player);

                        new FoliaScheduler(null, player) {
                            @Override
                            public void run() { //todo() use inventory holders
                                if (player.getOpenInventory().getTopInventory().equals(inventory)) player.closeInventory(InventoryCloseEvent.Reason.UNLOADED);
                            }
                        }.runDelayed(40);
                    }
                }
            }
        }.runAtFixedRate(2, 2));
    }

    private void setGlass() {
        for (int slot = 0; slot < getSize(); slot++) {
            if (slot != 13) {
                setCustomGlassPane(slot);
            }
        }
    }
}