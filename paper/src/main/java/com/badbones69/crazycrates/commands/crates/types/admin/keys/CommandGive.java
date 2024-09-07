package com.badbones69.crazycrates.commands.crates.types.admin.keys;

import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.enums.Permissions;
import com.badbones69.crazycrates.api.events.PlayerReceiveKeyEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.badbones69.crazycrates.commands.crates.types.BaseCommand;
import com.ryderbelserion.vital.paper.api.builders.PlayerBuilder;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.ArgName;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import java.util.HashMap;
import java.util.Map;

public class CommandGive extends BaseCommand {

    @Command("give")
    @Permission(value = "crazycrates.givekey", def = PermissionDefault.OP)
    public void give(CommandSender sender, @ArgName("key_type") @Suggestion("keys") String type, @ArgName("crate") @Suggestion("crates") String crateName, @ArgName("amount") @Suggestion("numbers") int amount, @ArgName("player") @Suggestion("players") PlayerBuilder target) {
        if (crateName == null || crateName.isEmpty() || crateName.isBlank()) {
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
            addKey(sender, target.getPlayer(), crate, keyType, amount);

            return;
        }

        addKey(sender, target.getOfflinePlayer(), crate, keyType, amount);
    }

    @Command("give-random")
    @Permission(value = "crazycrates.giverandomkey", def = PermissionDefault.OP)
    public void random(CommandSender sender, @Suggestion("keys") String type, @Suggestion("numbers") int amount, @Suggestion("players") PlayerBuilder target) {
        give(sender, type, this.crateManager.getUsableCrates().get((int) MiscUtils.pickNumber(0, (this.crateManager.getUsableCrates().size() - 2))).getFileName(), amount, target);
    }

    @Command("giveall")
    @Permission(value = "crazycrates.giveall", def = PermissionDefault.OP)
    public void all(CommandSender sender, @Suggestion("keys") String type, @Suggestion("crates") String crateName, @Suggestion("numbers") int amount) {
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

        final Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{amount}", String.valueOf(amount));
        placeholders.put("{keytype}", keyType.getFriendlyName());
        placeholders.put("{key}", crate.getKeyName());

        Messages.given_everyone_keys.sendMessage(sender, placeholders);

        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            if (Permissions.CRAZYCRATES_PLAYER_EXCLUDE.hasPermission(player)) continue;

            final PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReceiveReason.GIVE_ALL_COMMAND, amount);
            this.plugin.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) return;

            Messages.obtaining_keys.sendMessage(player, placeholders);

            if (crate.getCrateType() == CrateType.crate_on_the_go) {
                MiscUtils.addItem(player, crate.getKey(amount, player));

                return;
            }

            this.userManager.addKeys(player.getUniqueId(), crateName, keyType, amount);
        }
    }
}