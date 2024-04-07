package com.badbones69.crazycrates.commands.subs;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.google.common.collect.Lists;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.*;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.api.enums.Messages;
import us.crazycrew.crazycrates.api.users.UserManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Command(value = "keys", alias = {"key"})
@Description("Views the amount of keys you/others have.")
public class BaseKeyCommand extends BaseCommand {

    @NotNull
    private final CrazyCrates plugin = CrazyCrates.get();

    @NotNull
    private final UserManager userManager = this.plugin.getUserManager();

    @Default
    @Permission("crazycrates.command.player.key")
    public void viewPersonal(Player player) {
        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{crates_opened}", String.valueOf(this.userManager.getTotalCratesOpened(player.getUniqueId())));

        getKeys(player, player, Messages.virtual_keys_header.getMessage(placeholders, player), Messages.no_virtual_keys.getMessage(player));
    }

    @SubCommand("view")
    @Permission("crazycrates.command.player.key.others")
    public void viewOthers(CommandSender sender, @Suggestion ("online-players") Player target) {
        if (target == sender) {
            viewPersonal(target);

            return;
        }

        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{player}", target.getName());
        placeholders.put("{crates_opened}", String.valueOf(this.userManager.getTotalCratesOpened(target.getUniqueId())));

        String header = Messages.other_player_no_keys_header.getMessage(placeholders, sender);

        String otherPlayer = Messages.other_player_no_keys.getMessage("{player}", target.getName(), sender);

        getKeys(target, sender, header, otherPlayer);
    }

    /**
     * Get keys from player or sender or other player.
     *
     * @param player player to get keys.
     * @param sender sender to send message to.
     * @param header header of the message.
     * @param messageContent content of the message.
     */
    private void getKeys(Player player, CommandSender sender, String header, String messageContent) {
        List<String> message = Lists.newArrayList();

        message.add(header);

        Map<Crate, Integer> keys = new HashMap<>();

        this.plugin.getCrateManager().getUsableCrates().forEach(crate -> keys.put(crate, this.userManager.getVirtualKeys(player.getUniqueId(), crate.getName())));

        boolean hasKeys = false;

        for (Crate crate : keys.keySet()) {
            int amount = keys.get(crate);

            if (amount > 0) {
                Map<String, String> placeholders = new HashMap<>();

                hasKeys = true;

                placeholders.put("{crate}", crate.getFile().getString("Crate.Name"));
                placeholders.put("{keys}", String.valueOf(amount));
                placeholders.put("{crate_opened}", String.valueOf(this.userManager.getCrateOpened(player.getUniqueId(), crate.getName())));

                message.add(Messages.per_crate.getMessage(placeholders, player));
            }
        }

        if (MiscUtils.isPapiActive()) {
            if (sender instanceof Player person) {
                if (hasKeys) {
                    message.forEach(line -> person.sendMessage(PlaceholderAPI.setPlaceholders(person, line)));

                    return;
                }

                sender.sendMessage(PlaceholderAPI.setPlaceholders(person, messageContent));

                return;
            }

            return;
        }

        if (hasKeys) {
            message.forEach(sender::sendMessage);

            return;
        }

        sender.sendMessage(messageContent);
    }
}