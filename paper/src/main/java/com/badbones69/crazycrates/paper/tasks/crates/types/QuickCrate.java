package com.badbones69.crazycrates.paper.tasks.crates.types;

import com.badbones69.crazycrates.paper.api.builders.types.features.CrateSpinMenu;
import com.badbones69.crazycrates.paper.api.enums.other.keys.FileKeys;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import com.badbones69.crazycrates.paper.api.ChestManager;
import com.badbones69.crazycrates.paper.api.PrizeManager;
import com.badbones69.crazycrates.paper.api.objects.gui.GuiSettings;
import com.badbones69.crazycrates.paper.managers.events.EventManager;
import com.badbones69.crazycrates.paper.managers.events.enums.EventType;
import com.ryderbelserion.fusion.paper.builders.folia.FoliaScheduler;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.paper.api.builders.CrateBuilder;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class QuickCrate extends CrateBuilder {

    public QuickCrate(@NotNull final Crate crate, @NotNull final Player player, @NotNull final Location location) {
        super(crate, player, location);
    }

    private final Player player = getPlayer();
    private final Location location = getLocation();
    private final UUID uuid = this.player.getUniqueId();
    private final Crate crate = getCrate();

    @Override
    public void open(@NotNull final KeyType type, final boolean checkHand, final boolean isSilent, final int amount, @NotNull final EventType eventType) {
        final String fileName = this.crate.getFileName();

        final boolean isSneaking = this.player.isSneaking();

        final AtomicInteger reference = new AtomicInteger();

        // Crate event failed, so we return.
        if (isCrateEventValid(type, checkHand, isSilent, amount, eventType, event -> {
            if (isSneaking) {
                int keys = switch (type) {
                    case virtual_key -> this.userManager.getVirtualKeys(this.uuid, fileName);
                    case physical_key -> this.userManager.getPhysicalKeys(this.uuid, fileName);
                    default -> 1;
                };

                if (keys > 1) {
                    for (;keys > 0; keys--) { // check keys first.
                        int currentAmount = reference.get();

                        if (currentAmount >= this.crate.getMaxMassOpen()) break;

                        reference.getAndIncrement();
                    }

                    if (!this.userManager.takeKeys(this.uuid, fileName, type, reference.get(), true)) { // take keys first.
                        this.crateManager.endCrate(this.crate, this.player);

                        event.setCancelled(true);

                        return;
                    }
                }

                return;
            }

            if (!this.userManager.takeKeys(this.uuid, fileName, type, amount, checkHand)) {
                this.crateManager.endCrate(this.crate, this.player);

                event.setCancelled(true);
            }
        })) {
            return;
        }

        this.crateManager.addCrateInUse(this.player, this.location);

        int currentAmount = reference.get();

        if (currentAmount > 1) {
            int keysUsed = 0;

            for (;currentAmount > 0; currentAmount--) {
                if (MiscUtils.isInventoryFull(this.player)) {
                    this.userManager.addVirtualKeys(this.uuid, fileName, currentAmount);

                    break;
                }

                PrizeManager.givePrize(this.player, this.location.clone().add(0.5, 1, 0.5), this.crate,  this.crate.pickPrize(this.player));

                keysUsed++;
            }

            this.crateManager.endQuickCrate(this.player, this.location, this.crate, true);

            final String name = this.player.getName();

            EventManager.logEvent(EventType.event_crate_opened, name, this.player, this.crate, type, keysUsed);
            EventManager.logEvent(EventType.event_key_taken, name, this.player, this.crate, type, keysUsed);

            this.userManager.addOpenedCrate(this.uuid, fileName, keysUsed);

            return;
        }

        final Prize prize = this.crate.pickPrize(this.player);

        if (this.crate.isCyclePrize() && !PrizeManager.isCapped(this.crate, this.player)) { // re-open this menu
            new CrateSpinMenu(this.player, new GuiSettings(this.crate, prize, FileKeys.respin_gui.getConfiguration())).open();

            this.crateManager.removePlayerFromOpeningList(this.player);
            this.crateManager.removeCrateInUse(this.player);

            return;
        } else {
            this.userManager.removeRespinPrize(this.uuid, fileName);

            if (!this.crate.isCyclePersistRestart()) {
                this.userManager.removeRespinCrate(this.uuid, fileName, this.userManager.getCrateRespin(this.uuid, fileName));
            }
        }

        // Only related to the item above the crate.
        displayItem(prize);

        ChestManager.openChest(this.location.getBlock(), true);

        PrizeManager.givePrize(this.player, this.location.clone().add(0.5, 1, 0.5), this.crate, prize);

        this.userManager.addOpenedCrate(this.uuid, fileName, amount);

        addCrateTask(new FoliaScheduler(this.plugin, null, this.player) {
            @Override
            public void run() {
                crateManager.endQuickCrate(player, location, crate, false);
            }
        }.runDelayed(40));
    }
}