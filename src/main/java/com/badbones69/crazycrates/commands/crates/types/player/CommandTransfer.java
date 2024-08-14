package com.badbones69.crazycrates.commands.crates.types.player;

import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.events.PlayerReceiveKeyEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.commands.crates.types.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.ArgName;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommandTransfer extends BaseCommand {

    @Command("transfer")
    @Permission(value = "crazycrates.transfer", def = PermissionDefault.OP)
    public void transfer(Player player, @ArgName("crate") @Suggestion("crates") String crateName, @ArgName("player") @Suggestion("players") Player target, @ArgName("amount") @Suggestion("numbers") int amount) {
        if (crateName.isEmpty() || crateName.isBlank()) {
            Messages.cannot_be_empty.sendMessage(player, "{value}", "crate name");

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

        // If they don't have enough keys, we return.
        if (this.userManager.getVirtualKeys(uuid, crateName) <= amount) {
            Messages.transfer_not_enough_keys.sendMessage(player, "{crate}", crate.getName());

            return;
        }

        final PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReceiveReason.TRANSFER, amount);
        this.plugin.getServer().getPluginManager().callEvent(event);

        // If the event is cancelled, We return.
        if (event.isCancelled()) return;

        this.userManager.takeKeys(uuid, crateName, KeyType.virtual_key, amount, false);
        this.userManager.addKeys(receiver, crateName, KeyType.virtual_key, amount);

        final Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{crate}", crate.getName());
        placeholders.put("{amount}", String.valueOf(amount));
        placeholders.put("{keytype}", KeyType.virtual_key.getFriendlyName());
        placeholders.put("{player}", player.getName());

        Messages.transfer_sent_keys.sendMessage(player, placeholders);

        Messages.transfer_received_keys.sendMessage(target, "{player}", player.getName());
    }
}