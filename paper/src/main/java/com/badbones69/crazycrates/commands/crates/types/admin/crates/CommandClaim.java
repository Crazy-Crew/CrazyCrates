package com.badbones69.crazycrates.commands.crates.types.admin.crates;

import com.badbones69.crazycrates.api.PrizeManager;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.commands.crates.types.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.ArgName;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import java.util.HashMap;
import java.util.UUID;

public class CommandClaim extends BaseCommand {

    @Command("claim")
    @Permission(value = "crazycrates.claim", def = PermissionDefault.OP)
    public void add(Player player, @ArgName("crate") @Suggestion("crates") String crateName) {
        if (crateName == null || crateName.isEmpty() || crateName.isBlank()) {
            Messages.cannot_be_empty.sendMessage(player, "{value}", "crate name");

            return;
        }

        final Crate crate = getCrate(player, crateName, false);

        if (crate == null || crate.getCrateType() == CrateType.menu) {
            Messages.not_a_crate.sendMessage(player, "{crate}", crateName);

            return;
        }

        final UUID uuid = player.getUniqueId();

        if (!this.userManager.hasRespinPrize(uuid, crateName)) {
            Messages.crate_prize_respins_empty.sendMessage(player, new HashMap<>() {{
                put("{crate_pretty}", crate.getCrateName());
                put("{crate}", crate.getFileName());
            }});

            if (!crate.isCyclePersistRestart()) {
                this.userManager.removeRespinCrate(uuid, crateName, this.userManager.getCrateRespin(uuid, crateName));
            }

            return;
        }

        final String prizeName = this.userManager.getRespinPrize(uuid, crateName);

        final Prize prize = crate.getPrize(prizeName);

        if (prize == null) {
            Messages.prize_not_found.sendMessage(player, "{prize}", prizeName);

            if (!crate.isCyclePersistRestart()) {
                this.userManager.removeRespinCrate(uuid, crateName, this.userManager.getCrateRespin(uuid, crateName));
            }

            this.userManager.removeRespinPrize(uuid, crateName);

            return;
        }

        PrizeManager.givePrize(player, crate, prize);

        Messages.crate_prize_respins_redeemed.sendMessage(player, new HashMap<>() {{
            put("{crate_pretty}", crate.getCrateName());
            put("{crate}", crate.getFileName());
            put("{prize}", prize.getPrizeName());
        }});

        if (!crate.isCyclePersistRestart()) {
            this.userManager.removeRespinCrate(uuid, crateName, this.userManager.getCrateRespin(uuid, crateName));
        }

        this.userManager.removeRespinPrize(uuid, crateName);
    }
}