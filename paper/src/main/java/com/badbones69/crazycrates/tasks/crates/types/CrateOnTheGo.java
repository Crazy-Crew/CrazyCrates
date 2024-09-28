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

    @Override
    public void open(@NotNull final KeyType type, final boolean checkHand, final boolean isSilent, final EventType eventType) {
        // Crate event failed so we return.
        if (isCrateEventValid(type, checkHand, isSilent, eventType)) {
            return;
        }

        final Player player = getPlayer();
        final UUID uuid = player.getUniqueId();
        final Crate crate = getCrate();
        final String fileName = crate.getFileName();

        final boolean keyCheck = this.userManager.takeKeys(uuid, fileName, KeyType.physical_key, crate.useRequiredKeys() ? crate.getRequiredKeys() : 1, true);

        if (!keyCheck) {
            // Remove from opening list.
            this.crateManager.removePlayerFromOpeningList(player);

            return;
        }

        final Prize prize = crate.pickPrize(player);

        if (crate.isCyclePrize() && !PrizeManager.isCapped(crate, player)) { // re-open this menu
            new CrateSpinMenu(player, new GuiSettings(crate, prize, Files.respin_gui.getConfiguration())).open();

            this.crateManager.removePlayerFromOpeningList(player);
            this.crateManager.removePlayerKeyType(player);

            return;
        }

        PrizeManager.givePrize(player, crate, prize);

        if (prize.useFireworks()) MiscUtils.spawnFirework(player.getLocation().add(0, 1, 0), null);

        this.crateManager.removePlayerKeyType(player);
    }
}