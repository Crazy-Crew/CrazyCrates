package com.badbones69.crazycrates.paper.commands.crates.types.player;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.api.enums.other.Plugins;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.managers.BukkitUserManager;
import com.badbones69.crazycrates.paper.tasks.crates.CrateManager;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.*;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Command(value = "keys", alias = { "key" })
@Description("Views the amount of keys you/others have.")
public class CommandKey {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final BukkitUserManager userManager = this.plugin.getUserManager();
    private final CrateManager crateManager = this.plugin.getCrateManager();

    @Command
    @Permission(value = "crazycrates.keys", def = PermissionDefault.TRUE)
    @Syntax("/keys")
    public void personal(Player player) {
        getKeys(player, player, Messages.virtual_keys_header.getMessage(
                player,
                "{crates_opened}",
                String.valueOf(userManager.getTotalCratesOpened(player.getUniqueId()))
        ), Messages.no_virtual_key.getMessage(player));
    }

    @Command("view")
    @Permission("crazycrates.keys-others")
    @Syntax("/keys view [player_name]")
    public void view(CommandSender sender, @ArgName("player") @Optional @Suggestion("players") Player target) {
        if (target == null) {
            if (sender instanceof Player player) {
                personal(player);

                return;
            }

            return;
        }

        final String targetName = target.getName();

        if (targetName.equalsIgnoreCase(sender.getName())) {
            personal(target);

            return;
        }

        getKeys(target, sender, Messages.other_player_no_keys_header.getMessage(target, Map.of(
                "{crates_opened}", String.valueOf(userManager.getTotalCratesOpened(target.getUniqueId())),
                "{player}", targetName
        )), Messages.other_player_no_keys.getMessage(target, "{player}", targetName));
    }

    /**
     * Get keys from player or sender or other player.
     *
     * @param player player to get keys.
     * @param sender sender to send message to.
     * @param header header of the message.
     * @param content content of the message.
     */
    private void getKeys(@NotNull final Player player, @NotNull final CommandSender sender, @NotNull final String header, @NotNull final String content) {
        if (header.isEmpty() || content.isEmpty()) return;

        final UUID uuid = player.getUniqueId();

        final List<String> message = new ArrayList<>();

        message.add(header);

        final Map<Crate, Integer> keys = new HashMap<>();

        this.crateManager.getUsableCrates().forEach(crate -> keys.put(crate, this.userManager.getVirtualKeys(uuid, crate.getFileName())));

        boolean hasKeys = false;

        for (final Crate crate : keys.keySet()) {
            final int amount = keys.get(crate);

            if (amount > 0) {
                hasKeys = true;

                message.add(Messages.per_crate.getMessage(player, Map.of(
                        "{crate_opened}", String.valueOf(userManager.getCrateOpened(uuid, crate.getFileName())),
                        "{keys}", String.valueOf(amount),
                        "{crate}", crate.getCrateName()
                )));
            }
        }

        if (Plugins.placeholder_api.isEnabled() ) {
            if (sender instanceof Player person) {
                if (hasKeys) {
                    message.forEach(line -> person.sendRichMessage(PlaceholderAPI.setPlaceholders(person, line)));

                    return;
                }

                sender.sendRichMessage(PlaceholderAPI.setPlaceholders(person, content));

                return;
            }

            return;
        }

        if (hasKeys) {
            message.forEach(sender::sendRichMessage);

            return;
        }

        sender.sendRichMessage(content);
    }
}