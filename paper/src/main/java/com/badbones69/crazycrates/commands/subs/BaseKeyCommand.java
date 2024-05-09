package com.badbones69.crazycrates.commands.subs;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import com.google.common.collect.Lists;
import com.ryderbelserion.vital.enums.Support;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.*;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.api.enums.Messages;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Command(value = "keys", alias = {"key"})
@Description("Views the amount of keys you/others have.")
public class BaseKeyCommand extends BaseCommand {

    private final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private final @NotNull BukkitUserManager userManager = this.plugin.getUserManager();

    @Default
    @Permission("crazycrates.command.player.key")
    public void viewPersonal(Player player) {
        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{crates_opened}", String.valueOf(this.userManager.getTotalCratesOpened(player.getUniqueId())));

        getKeys(player, player, Messages.virtual_keys_header.getMessage(player, placeholders), Messages.no_virtual_keys.getMessage(player));
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

        String header = Messages.other_player_no_keys_header.getMessage(null, placeholders);

        String content = Messages.other_player_no_keys.getMessage(null, "{player}", target.getName());

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
    private void getKeys(Player player, CommandSender sender, String header, String content) {
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