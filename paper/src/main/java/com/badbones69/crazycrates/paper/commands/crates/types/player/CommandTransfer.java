package com.badbones69.crazycrates.paper.commands.crates.types.player;

import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.api.events.PlayerReceiveKeyEvent;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.commands.crates.types.BaseCommand;
import com.badbones69.crazycrates.paper.managers.events.EventManager;
import com.badbones69.crazycrates.paper.managers.events.enums.EventType;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.ArgName;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import us.crazycrew.crazycrates.api.constants.MessageKeys;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import java.util.Map;
import java.util.UUID;

public class CommandTransfer extends BaseCommand {

    @Command("transfer")
    @Permission(value = "crazycrates.transfer", def = PermissionDefault.OP)
    @Syntax("/crazycrates transfer <crate_name> <player_name> <amount>")
    public void transfer(Player player, @ArgName("crate") @Suggestion("crates") String crateName, @ArgName("player") @Suggestion("players") Player target, @ArgName("amount") @Suggestion("numbers") int amount) {
        if (crateName.isBlank()) {
            this.senderAdapter.sendMessage(player, MessageKeys.cannot_be_empty, Map.of("{value}", "crate name"));

            return;
        }

        final Crate crate = this.crateManager.getCrateFromName(crateName);

        // If the crate is menu or null. we return
        if (crate == null || crate.getCrateType() == CrateType.menu) {
            Messages.not_a_crate.sendMessage(player, "{crate}", crateName);

            return;
        }

        final UUID uuid = player.getUniqueId();
        final UUID receiver = target.getUniqueId();

        // If it's the same player, we return.
        if (uuid.toString().equalsIgnoreCase(receiver.toString())) {
            Messages.same_player.sendMessage(player);

            return;
        }

        final String fancyName = crate.getCrateName();
        final String fileName = crate.getFileName();

        final int clamp = Math.max(amount, 1);

        // If they don't have enough keys, we return.
        if (this.userManager.getVirtualKeys(uuid, fileName) < clamp) {
            Messages.transfer_not_enough_keys.sendMessage(player, "{crate}", fancyName);

            return;
        }

        final PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReceiveReason.TRANSFER, clamp);

        this.pluginManager.callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        if (this.userManager.takeKeys(uuid, fileName, KeyType.virtual_key, clamp, false)) { // only add keys if successfully taken.
            this.userManager.addVirtualKeys(receiver, fileName, clamp);

            final String playerName = player.getName();

            Messages.transfer_sent_keys.sendMessage(player, Map.of(
                    "{keytype}", KeyType.virtual_key.getFriendlyName(),
                    "{amount}", String.valueOf(clamp),
                    "{player}", target.getName(),
                    "{crate}", fancyName
            ));

            Messages.transfer_received_keys.sendMessage(target, Map.of(
                    "{keytype}", KeyType.virtual_key.getFriendlyName(),
                    "{amount}", String.valueOf(clamp),
                    "{player}", playerName,
                    "{crate}", fancyName
            ));

            EventManager.logEvent(EventType.event_key_transferred, target.getName(), player, crate, KeyType.virtual_key, clamp);
        }
    }
}