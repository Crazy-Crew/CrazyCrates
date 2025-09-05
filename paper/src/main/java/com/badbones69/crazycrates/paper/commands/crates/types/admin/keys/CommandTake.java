package com.badbones69.crazycrates.paper.commands.crates.types.admin.keys;

import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.commands.crates.types.BaseCommand;
import com.badbones69.crazycrates.paper.api.PlayerBuilder;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.ArgName;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.api.enums.types.KeyType;

public class CommandTake extends BaseCommand {

    @Command("take")
    @Permission(value = "crazycrates.takekey", def = PermissionDefault.OP)
    @Syntax("/crazycrates take <key_type> <crate_name> <amount> <player_name>")
    public void take(CommandSender sender, @ArgName("key_type") @Suggestion("keys") String type, @ArgName("crate") @Suggestion("crates") String crateName, @ArgName("amount") @Suggestion("numbers") int amount, @ArgName("player") @Suggestion("players") PlayerBuilder target) {
        if (crateName == null || crateName.isBlank()) {
            Messages.cannot_be_empty.sendMessage(sender, "{value}", "crate name");

            return;
        }

        final Crate crate = getCrate(sender, crateName, false);

        if (crate == null || crate.getCrateType() == CrateType.menu) {
            Messages.not_a_crate.sendMessage(sender, "{crate}", crateName);

            return;
        }

        final KeyType keyType = getKeyType(type);

        if (target.getPlayer() != null) {
            final Player player = target.getPlayer();

            takeKey(sender, player, crate, keyType, Math.max(amount, 1));

            return;
        }

        takeKey(sender, target.getOfflinePlayer(), crate, keyType, Math.max(amount, 1));
    }
}