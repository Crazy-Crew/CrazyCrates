package com.badbones69.crazycrates.tasks.crates.types;

import com.badbones69.crazycrates.api.builders.types.features.CrateSpinMenu;
import com.badbones69.crazycrates.api.enums.misc.Files;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.api.PrizeManager;
import com.badbones69.crazycrates.api.objects.gui.GuiSettings;
import com.badbones69.crazycrates.managers.events.enums.EventType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.api.builders.CrateBuilder;
import com.badbones69.crazycrates.utils.MiscUtils;
import java.util.UUID;

public class CrateOnTheGo extends CrateBuilder {

    public CrateOnTheGo(@NotNull final Crate crate, @NotNull final Player player) {
        super(crate, player);
    }

    private final Player player = getPlayer();
    private final UUID uuid = this.player.getUniqueId();
    private final Crate crate = getCrate();

    @Override
    public void open(@NotNull final KeyType type, final boolean checkHand, final boolean isSilent, final EventType eventType) {
        // Crate event failed so we return.
        if (isCrateEventValid(type, checkHand, isSilent, eventType)) {
            return;
        }

        final String fileName = this.crate.getFileName();

        final boolean keyCheck = this.userManager.takeKeys(this.uuid, fileName, KeyType.physical_key, this.crate.useRequiredKeys() ? this.crate.getRequiredKeys() : 1, true);

        if (!keyCheck) {
            // Remove from opening list.
            this.crateManager.removePlayerFromOpeningList(this.player);

            return;
        }

        final Prize prize = this.crate.pickPrize(this.player);

        if (this.crate.isCyclePrize() && !PrizeManager.isCapped(this.crate, this.player)) { // re-open this menu
            new CrateSpinMenu(this.player, new GuiSettings(this.crate, prize, Files.respin_gui.getConfiguration())).open();

            this.crateManager.removePlayerFromOpeningList(this.player);
            this.crateManager.removePlayerKeyType(this.player);

            return;
        } else {
            this.userManager.removeRespinPrize(this.uuid, fileName);

            if (!crate.isCyclePersistRestart()) {
                userManager.removeRespinCrate(uuid, fileName, userManager.getCrateRespin(uuid, fileName));
            }
        }

        PrizeManager.givePrize(this.player, this.crate, prize);

        if (prize.useFireworks()) MiscUtils.spawnFirework(this.player.getLocation().add(0, 1, 0), null);

        this.crateManager.removePlayerKeyType(this.player);
    }
}