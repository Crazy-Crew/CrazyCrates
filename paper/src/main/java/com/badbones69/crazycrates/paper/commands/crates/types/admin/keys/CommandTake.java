package com.badbones69.crazycrates.paper.commands.crates.types.admin.keys;

import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.commands.crates.types.BaseCommand;
import com.ryderbelserion.fusion.paper.builders.items.PlayerBuilder;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.ArgName;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import us.crazycrew.crazycrates.api.constants.MessageKeys;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.api.enums.types.KeyType;

import java.util.Map;

public class CommandTake extends BaseCommand {

    @Command("take")
    @Permission(value = "crazycrates.takekey", def = PermissionDefault.OP)
    @Syntax("/crazycrates take <key_type> <crate_name> <amount> <player_name>")
    public void take(CommandSender sender, @ArgName("key_type") @Suggestion("keys") String type, @ArgName("crate") @Suggestion("crates") String crateName, @ArgName("amount") @Suggestion("numbers") int amount, @ArgName("player") @Suggestion("players") PlayerBuilder target) {
        if (crateName == null || crateName.isBlank()) {
            this.senderAdapter.sendMessage(sender, MessageKeys.cannot_be_empty, Map.of("{value}", "crate name"));

            return;
        }

        final Crate crate = getCrate(sender, crateName, false);

        if (crate == null || crate.getCrateType() == CrateType.menu) {
            Messages.not_a_crate.sendMessage(sender, "{crate}", crateName);

            return;
        }

        final KeyType keyType = getKeyType(type);

        target.getPlayer().ifPresentOrElse(player -> takeKey(sender, player, crate, keyType, amount), () -> target.getOfflinePlayer().ifPresent(player -> takeKey(sender, player, crate, keyType, amount)));
    }
}