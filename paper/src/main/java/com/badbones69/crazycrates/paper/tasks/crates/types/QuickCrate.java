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

        // Crate event failed, so we return.
        if (isCrateEventValid(type, checkHand, isSilent, amount, eventType, event -> {
            if (!this.userManager.takeKeys(this.uuid, fileName, type, amount, checkHand)) {
                this.crateManager.endCrate(this.crate, this.player);

                event.setCancelled(true);
            }
        })) {
            return;
        }

        int keys = switch (type) {
            case virtual_key -> this.userManager.getVirtualKeys(this.uuid, fileName);
            case physical_key -> this.userManager.getPhysicalKeys(this.uuid, fileName);
            default -> 1;
        };

        this.crateManager.addCrateInUse(this.player, this.location);

        if (this.player.isSneaking() && keys > 1) { //todo() test this
            int currentAmount = 0;

            for (;keys > 0; keys--) { // check keys first.
                if (currentAmount >= this.crate.getMaxMassOpen()) break;

                if (currentAmount >= amount) break;

                currentAmount++;
            }

            if (!this.userManager.takeKeys(this.uuid, fileName, type, currentAmount, true)) { // take keys first.
                // End the crate.
                this.crateManager.endCrate(this.crate, this.player);

                return;
            }

            int keysUsed = 0;

            // this ensures that keys are taken first BEFORE they get prizes.
            for (;currentAmount > 0; currentAmount--) { // loop through used until it's 0, if the inventory is full... give them the remaining keys back.
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

        if (!this.userManager.takeKeys(this.uuid, fileName, type, amount, false)) {
            // End the crate.
            this.crateManager.endCrate(this.crate, this.player);

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