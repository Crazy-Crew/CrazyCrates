package com.badbones69.crazycrates.paper.tasks.crates.types;

import com.badbones69.crazycrates.paper.api.builders.types.features.CrateSpinMenu;
import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.api.enums.other.keys.FileKeys;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import com.badbones69.crazycrates.paper.api.ChestManager;
import com.badbones69.crazycrates.paper.api.PrizeManager;
import com.badbones69.crazycrates.paper.api.objects.gui.GuiSettings;
import com.badbones69.crazycrates.paper.managers.events.enums.EventType;
import com.ryderbelserion.fusion.paper.api.scheduler.FoliaScheduler;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.paper.api.builders.CrateBuilder;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import java.util.HashMap;
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
    public void open(@NotNull final KeyType type, final boolean checkHand, final boolean isSilent, @NotNull final EventType eventType) {
        // Crate event failed, so we return.
        if (isCrateEventValid(type, checkHand, isSilent, eventType)) {
            return;
        }

        final String fileName = this.crate.getFileName();

        int keys = switch (type) {
            case virtual_key -> this.userManager.getVirtualKeys(this.uuid, fileName);
            case physical_key -> this.userManager.getPhysicalKeys(this.uuid, fileName);
            default -> 1;
        };

        if (this.crate.useRequiredKeys() && keys < this.crate.getRequiredKeys()) {
            final int finalKeys = keys;

            Messages.not_enough_keys.sendMessage(this.player, new HashMap<>() {{
                put("{required_amount}", String.valueOf(crate.getRequiredKeys()));
                put("{key_amount}", String.valueOf(crate.getRequiredKeys())); // deprecated, remove in the next major version of minecraft.
                put("{amount}", String.valueOf(finalKeys));
                put("{crate}", crate.getCrateName());
                put("{key}", crate.getKeyName());
            }});

            // Remove from an opening list.
            this.crateManager.removePlayerFromOpeningList(this.player);

            return;
        }

        this.crateManager.addCrateInUse(this.player, this.location);

        if (this.player.isSneaking() && keys > 1) {
            int used = 0;

            for (;keys > 0; keys--) {
                if (MiscUtils.isInventoryFull(this.player)) break;
                if (used >= this.crate.getMaxMassOpen()) break;

                final Prize prize = this.crate.pickPrize(this.player);

                PrizeManager.givePrize(this.player, this.location.clone().add(0.5, 1, 0.5), this.crate, prize);

                used++;
            }

            final boolean keyCheck = this.userManager.takeKeys(this.uuid, fileName, type, used, false);

            if (!keyCheck) {
                // Send the message about failing to take the key.
                MiscUtils.failedToTakeKey(this.player, fileName);

                // Remove from an opening list.
                this.crateManager.removePlayerFromOpeningList(this.player);

                // Remove crates in use
                this.crateManager.removeCrateInUse(this.player);

                return;
            }

            this.crateManager.endQuickCrate(this.player, this.location, this.crate, true);

            return;
        }

        final boolean keyCheck = this.userManager.takeKeys(this.uuid, fileName, type, this.crate.useRequiredKeys() ? this.crate.getRequiredKeys() : 1, true);

        if (!keyCheck) {
            // Send the message about failing to take the key.
            MiscUtils.failedToTakeKey(this.player, fileName);

            // Remove from opening list.
            this.crateManager.removePlayerFromOpeningList(this.player);

            // Remove crates in use
            this.crateManager.removeCrateInUse(this.player);

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

            if (!crate.isCyclePersistRestart()) {
                userManager.removeRespinCrate(uuid, fileName, userManager.getCrateRespin(uuid, fileName));
            }
        }

        // Only related to the item above the crate.
        displayItem(prize);

        ChestManager.openChest(this.location.getBlock(), true);

        PrizeManager.givePrize(this.player, this.location.clone().add(0.5, 1, 0.5), this.crate, prize);

        addCrateTask(new FoliaScheduler(this.plugin, null, this.player) {
            @Override
            public void run() {
                crateManager.endQuickCrate(player, location, crate, false);
            }
        }.runDelayed(40));
    }
}