package com.badbones69.crazycrates.commands.crates.types.admin.keys;

import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.enums.Permissions;
import com.badbones69.crazycrates.api.events.PlayerReceiveKeyEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.badbones69.crazycrates.commands.crates.types.BaseCommand;
import com.ryderbelserion.vital.paper.builders.PlayerBuilder;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
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
    public void give(CommandSender sender, @Suggestion("keys") String type, @Suggestion("crates") String crateName, @Suggestion("numbers") int amount, @Suggestion("players") PlayerBuilder target) {
        if (crateName.isEmpty() || crateName.isBlank()) {
            sender.sendRichMessage(Messages.cannot_be_empty.getMessage(sender, "{value}", "crate name"));

            return;
        }

        if (amount <= 0) {
            sender.sendRichMessage(Messages.not_a_number.getMessage(sender, "{number}", String.valueOf(amount)));

            return;
        }


        final Crate crate = getCrate(sender, crateName, false);

        if (crate == null || crate.getCrateType() == CrateType.menu) {
            sender.sendRichMessage(Messages.not_a_crate.getMessage(sender, "{crate}", crateName));

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
        give(sender, type, this.crateManager.getUsableCrates().get((int) MiscUtils.pickNumber(0, (this.crateManager.getUsableCrates().size() - 2))).getName(), amount, target);
    }

    @Command("giveall")
    public void all(CommandSender sender, @Suggestion("keys") String type, @Suggestion("crates") String crateName, @Suggestion("numbers") int amount) {
        if (crateName.isEmpty()) {
            sender.sendRichMessage(Messages.not_a_crate.getMessage(sender, "{crate}", crateName));

            return;
        }

        if (amount <= 0) {
            sender.sendRichMessage(Messages.not_a_number.getMessage(sender, "{number}", String.valueOf(amount)));

            return;
        }

        final Crate crate = getCrate(sender, crateName, false);

        if (crate == null) {
            sender.sendRichMessage(Messages.not_a_crate.getMessage(sender, "{crate}", crateName));

            return;
        }

        final KeyType keyType = getKeyType(type);

        final Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{amount}", String.valueOf(amount));
        placeholders.put("{keytype}", keyType.getFriendlyName());
        placeholders.put("{key}", crate.getKeyName());

        sender.sendRichMessage(Messages.given_everyone_keys.getMessage(sender, placeholders));

        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            if (Permissions.CRAZYCRATES_PLAYER_EXCLUDE.hasPermission(player)) continue;

            final PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReceiveReason.GIVE_ALL_COMMAND, amount);
            this.plugin.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) return;

            player.sendRichMessage(Messages.obtaining_keys.getMessage(player, placeholders));

            if (crate.getCrateType() == CrateType.crate_on_the_go) {
                player.getInventory().addItem(crate.getKey(amount, player));

                return;
            }

            this.userManager.addKeys(player.getUniqueId(), crate.getName(), keyType, amount);
        }
    }
}