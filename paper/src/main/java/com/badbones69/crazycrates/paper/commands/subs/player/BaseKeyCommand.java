package com.badbones69.crazycrates.paper.commands.subs.player;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.CrazyManager;
import com.badbones69.crazycrates.paper.api.enums.settings.Messages;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.google.common.collect.Lists;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.List;

@Command(value = "keys", alias = {"key"})
public class BaseKeyCommand extends BaseCommand {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrazyManager crazyManager = plugin.getStarter().getCrazyManager();

    @Default
    @Permission("crazycrates.command.player.key")
    public void viewPersonal(Player player) {
        String header = Messages.PERSONAL_HEADER.getMessageNoPrefix();

        String noKeys = Messages.PERSONAL_NO_VIRTUAL_KEYS.getMessage();

        getKeys(player, player, header, noKeys);
    }

    @SubCommand("view")
    @Permission("crazycrates.command.player.key.others")
    public void viewOthers(CommandSender sender, @Suggestion ("online-players") Player target) {
        if (target == sender) {
            sender.sendMessage(Messages.SAME_PLAYER.getMessage());
            return;
        }

        String header = Messages.OTHER_PLAYER_HEADER.getMessageNoPrefix("%Player%", target.getName());

        String otherPlayer = Messages.OTHER_PLAYER_NO_VIRTUAL_KEYS.getMessage("%Player%", target.getName());

        getKeys(target, sender, header, otherPlayer);
    }

    private void getKeys(Player target, CommandSender sender, String header, String messageContent) {
        List<String> message = Lists.newArrayList();

        message.add(header);

        HashMap<Crate, Integer> keys = crazyManager.getVirtualKeys(target);

        boolean hasKeys = false;

        for (Crate crate : keys.keySet()) {
            int amount = keys.get(crate);

            if (amount > 0) {
                hasKeys = true;
                HashMap<String, String> placeholders = new HashMap<>();
                placeholders.put("%Crate%", crate.getFile().getString("Crate.Name"));
                placeholders.put("%Keys%", amount + "");
                message.add(Messages.PER_CRATE.getMessageNoPrefix(placeholders));
            }
        }

        if (hasKeys) {
            sender.sendMessage(Messages.convertList(message));
        } else {
            sender.sendMessage(messageContent);
        }
    }
}