package com.badbones69.crazycrates.commands.crates.types.player;

import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.events.PlayerReceiveKeyEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.commands.crates.types.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
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
    public void transfer(Player player, @Suggestion("crates") String crateName, @Suggestion("players") Player target, @Suggestion("numbers") int amount) {
        if (crateName.isEmpty() || crateName.isBlank()) {
            player.sendRichMessage(Messages.cannot_be_empty.getMessage(player, "{value}", "crate name"));

            return;
        }

        final Crate crate = this.crateManager.getCrateFromName(crateName);

        // If the crate is menu or null. we return
        if (crate == null || crate.getCrateType() == CrateType.menu) {
            player.sendRichMessage(Messages.not_a_crate.getMessage(player, "{crate}", crateName));

            return;
        }

        final UUID uuid = player.getUniqueId();
        final UUID receiver = target.getUniqueId();

        // If it's the same player, we return.
        if (uuid.toString().equalsIgnoreCase(receiver.toString())) {
            player.sendRichMessage(Messages.same_player.getMessage(player));

            return;
        }

        // If they don't have enough keys, we return.
        if (this.userManager.getVirtualKeys(uuid, crate.getName()) <= amount) {
            player.sendRichMessage(Messages.transfer_not_enough_keys.getMessage(player, "{crate}", crate.getName()));

            return;
        }

        final PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReceiveReason.TRANSFER, amount);
        this.plugin.getServer().getPluginManager().callEvent(event);

        // If the event is cancelled, We return.
        if (event.isCancelled()) return;

        this.userManager.takeKeys(uuid, crate.getName(), KeyType.virtual_key, amount, false);
        this.userManager.addKeys(receiver, crate.getName(), KeyType.virtual_key, amount);

        final Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{crate}", crate.getName());
        placeholders.put("{amount}", String.valueOf(amount));
        placeholders.put("{keytype}", KeyType.virtual_key.getFriendlyName());
        placeholders.put("{player}", player.getName());

        player.sendRichMessage(Messages.transfer_sent_keys.getMessage(player, placeholders));

        target.sendRichMessage(Messages.transfer_received_keys.getMessage(target, "{player}", player.getName()));
    }
}