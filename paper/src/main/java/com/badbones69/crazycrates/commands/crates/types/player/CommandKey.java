package com.badbones69.crazycrates.commands.crates.types.player;

import com.ryderbelserion.vital.util.builders.PlayerBuilder;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Description;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

@Command(value = "keys", alias = { "key" })
@Description("Views the amount of keys you/others have.")
public class CommandKey {

    @Command
    @Permission(value = "crazycrates.keys", def = PermissionDefault.TRUE)
    public void personal(Player player) {
        /*Map<String, String> placeholders = new ConcurrentHashMap<>();

        placeholders.put("{crates_opened}", String.valueOf(this.userManager.getTotalCratesOpened(player.getUniqueId())));

        getKeys(player, player, Messages.virtual_keys_header.getMessage(player, placeholders), Messages.no_virtual_keys.getMessage(player));*/
    }

    @Command("view")
    @Permission("crazycrates.keys-others")
    public void view(CommandSender sender, @Suggestion("players") PlayerBuilder target) {
        /*if (target == sender) {
            viewPersonal(target);

            return;
        }

        Map<String, String> placeholders = new ConcurrentHashMap<>();

        placeholders.put("{player}", target.getName());
        placeholders.put("{crates_opened}", String.valueOf(this.userManager.getTotalCratesOpened(target.getUniqueId())));

        String header = Messages.other_player_no_keys_header.getMessage(null, placeholders);

        String content = Messages.other_player_no_keys.getMessage(null, "{player}", target.getName());

        getKeys(target, sender, header, content);*/
    }
}