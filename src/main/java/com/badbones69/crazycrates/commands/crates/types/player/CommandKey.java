package com.badbones69.crazycrates.commands.crates.types.player;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.ryderbelserion.vital.paper.enums.Support;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.ArgName;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Description;
import dev.triumphteam.cmd.core.annotations.Optional;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Command(value = "keys", alias = { "key" })
@Description("Views the amount of keys you/others have.")
public class CommandKey {

    private @NotNull final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private @NotNull final BukkitUserManager userManager = this.plugin.getUserManager();
    private @NotNull final CrateManager crateManager = this.plugin.getCrateManager();

    @Command
    @Permission(value = "crazycrates.keys", def = PermissionDefault.TRUE)
    public void personal(Player player) {
        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{crates_opened}", String.valueOf(this.userManager.getTotalCratesOpened(player.getUniqueId())));

        getKeys(player, player, Messages.virtual_keys_header.getMessage(player, placeholders), Messages.no_virtual_keys.getMessage(player));
    }

    @Command("view")
    @Permission("crazycrates.keys-others")
    public void view(CommandSender sender, @ArgName("player") @Optional @Suggestion("players") Player target) {
        if (target == null) {
            if (sender instanceof Player player) {
                personal(player);

                return;
            }

            return;
        }

        if (target == sender) {
            personal(target);

            return;
        }

        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{player}", target.getName());
        placeholders.put("{crates_opened}", String.valueOf(this.userManager.getTotalCratesOpened(target.getUniqueId())));

        String header = Messages.other_player_no_keys_header.getMessage(target, placeholders);

        String content = Messages.other_player_no_keys.getMessage(target, "{player}", target.getName());

        getKeys(target, sender, header, content);
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

        final List<String> message = new ArrayList<>();

        message.add(header);

        final Map<Crate, Integer> keys = new HashMap<>();

        this.crateManager.getUsableCrates().forEach(crate -> keys.put(crate, this.userManager.getVirtualKeys(player.getUniqueId(), crate.getName())));

        boolean hasKeys = false;

        for (final Crate crate : keys.keySet()) {
            final int amount = keys.get(crate);

            if (amount > 0) {
                final Map<String, String> placeholders = new HashMap<>();

                hasKeys = true;

                placeholders.put("{crate}", crate.getCrateInventoryName());
                placeholders.put("{keys}", String.valueOf(amount));
                placeholders.put("{crate_opened}", String.valueOf(this.userManager.getCrateOpened(player.getUniqueId(), crate.getName())));

                message.add(Messages.per_crate.getMessage(player, placeholders));
            }
        }

        if (Support.placeholder_api.isEnabled() ) {
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