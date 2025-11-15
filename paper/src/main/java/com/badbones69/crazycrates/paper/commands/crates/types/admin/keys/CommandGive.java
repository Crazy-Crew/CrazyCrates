package com.badbones69.crazycrates.paper.commands.crates.types.admin.keys;

import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.api.enums.Permissions;
import com.badbones69.crazycrates.paper.api.events.PlayerReceiveKeyEvent;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import com.badbones69.crazycrates.paper.commands.crates.types.BaseCommand;
import com.badbones69.crazycrates.paper.api.PlayerBuilder;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.*;
import dev.triumphteam.cmd.core.argument.keyed.Flags;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import java.util.Map;

public class CommandGive extends BaseCommand {

    @Command("give")
    @Flag(flag = "s", longFlag = "silent")
    @Permission(value = "crazycrates.givekey", def = PermissionDefault.OP)
    @Syntax("/crazycrates give <key_type> <crate_name> <amount> <player_name> [-s/--silent]")
    public void give(CommandSender sender, @ArgName("key_type") @Suggestion("keys") String type, @ArgName("crate") @Suggestion("crates") String crateName, @ArgName("amount") @Suggestion("numbers") int amount, @ArgName("player") @Suggestion("players") PlayerBuilder target, Flags flags) {
        final boolean isSilent = flags.hasFlag("s");

        if (crateName == null || crateName.isBlank()) {
            Messages.cannot_be_empty.sendMessage(sender, "{value}", "crate name");

            return;
        }

        if (amount <= 0) {
            Messages.not_a_number.sendMessage(sender, "{number}", String.valueOf(amount));

            return;
        }

        final Crate crate = getCrate(sender, crateName, false);

        if (crate == null || crate.getCrateType() == CrateType.menu) {
            Messages.not_a_crate.sendMessage(sender, "{crate}", crateName);

            return;
        }

        final KeyType keyType = getKeyType(type);

        if (target.getPlayer() != null) {
            addKey(sender, target.getPlayer(), crate, keyType, amount, isSilent);

            return;
        }

        addKey(sender, target.getOfflinePlayer(), crate, keyType, amount, isSilent);
    }

    @Command("give-random")
    @Flag(flag = "s", longFlag = "silent")
    @Permission(value = "crazycrates.giverandomkey", def = PermissionDefault.OP)
    @Syntax("/crazycrates give-random <key_type> <crate_name> <amount> <player_name> [-s/--silent]")
    public void random(CommandSender sender, @Suggestion("keys") String type, @Suggestion("numbers") int amount, @Suggestion("players") PlayerBuilder target, Flags flags) {
        give(sender, type, this.crateManager.getUsableCrates().get((int) MiscUtils.pickNumber(0, (this.crateManager.getUsableCrates().size() - 2))).getFileName(), amount, target, flags);
    }

    @Command("giveall")
    @Flag(flag = "s", longFlag = "silent")
    @Permission(value = "crazycrates.giveall", def = PermissionDefault.OP)
    @Syntax("/crazycrates giveall <key_type> <crate_name> <amount> [-s/--silent]")
    public void all(CommandSender sender, @Suggestion("keys") String type, @Suggestion("crates") String crateName, @Suggestion("numbers") int amount, Flags flags) {
        final boolean isSilent = flags.hasFlag("s");

        if (crateName.isEmpty()) {
            Messages.not_a_crate.sendMessage(sender, "{crate}", crateName);

            return;
        }

        if (amount <= 0) {
            Messages.not_a_number.sendMessage(sender, "{amount}", String.valueOf(amount));

            return;
        }

        final Crate crate = getCrate(sender, crateName, false);

        if (crate == null) {
            Messages.not_a_crate.sendMessage(sender, "{crate}", crateName);

            return;
        }

        final KeyType keyType = getKeyType(type);

        Messages.given_everyone_keys.sendMessage(sender, Map.of(
                "{keytype}", keyType.getFriendlyName(),
                "{amount}", String.valueOf(amount),
                "{key}", crate.getKeyName()
        ));

        for (final Player player : this.server.getOnlinePlayers()) {
            if (Permissions.CRAZYCRATES_PLAYER_EXCLUDE.hasPermission(player)) continue;

            final PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReceiveReason.GIVE_ALL_COMMAND, amount);

            this.pluginManager.callEvent(event);

            if (event.isCancelled()) continue;

            addKey(sender, player, crate, keyType, amount, isSilent, true);
        }
    }
}