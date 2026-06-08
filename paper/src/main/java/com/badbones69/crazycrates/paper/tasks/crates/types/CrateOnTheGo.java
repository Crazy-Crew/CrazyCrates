package com.badbones69.crazycrates.paper.tasks.crates.types;

import com.badbones69.crazycrates.paper.api.builders.types.features.CrateSpinMenu;
import com.badbones69.crazycrates.paper.api.enums.other.keys.FileKeys;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import com.badbones69.crazycrates.paper.api.PrizeManager;
import com.badbones69.crazycrates.paper.api.objects.gui.GuiSettings;
import com.badbones69.crazycrates.paper.managers.events.enums.EventType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.paper.api.builders.CrateBuilder;
import java.util.UUID;

public class CrateOnTheGo extends CrateBuilder {

    public CrateOnTheGo(@NotNull final Crate crate, @NotNull final Player player) {
        super(crate, player);
    }

    private final Player player = getPlayer();
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

        final Prize prize = this.crate.pickPrize(this.player);

        if (this.crate.isCyclePrize() && !PrizeManager.isCapped(this.crate, this.player)) { // re-open this menu
            new CrateSpinMenu(this.player, new GuiSettings(this.crate, prize, FileKeys.respin_gui.getConfiguration())).open();

            this.crateManager.removePlayerFromOpeningList(this.player);
            this.crateManager.removePlayerKeyType(this.player);

            return;
        } else {
            this.userManager.removeRespinPrize(this.uuid, fileName);

            if (!this.crate.isCyclePersistRestart()) {
                this.userManager.removeRespinCrate(this.uuid, fileName, userManager.getCrateRespin(this.uuid, fileName));
            }
        }

        PrizeManager.givePrize(this.player, this.crate, prize);

        this.userManager.addOpenedCrate(this.uuid, fileName, amount);

        this.crateManager.endCrate(this.crate, this.player);
    }
}