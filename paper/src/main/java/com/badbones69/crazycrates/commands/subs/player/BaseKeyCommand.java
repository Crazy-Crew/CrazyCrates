package com.badbones69.crazycrates.commands.subs.player;

/*
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.enums.settings.Messages;
import com.badbones69.crazycrates.api.objects.Crate;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.ArgName;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Command(value = "keys", alias = {"key"})
public class BaseKeyCommand extends BaseCommand {

    private final CrazyManager crazyManager = CrazyManager.getInstance();

    @Default
    @Permission("crazycrates.command.player.key")
    public void viewPersonal(Player player) {

        List<String> message = new ArrayList<>();
        message.add(Messages.PERSONAL_HEADER.getMessageNoPrefix());
        HashMap<Crate, Integer> keys = crazyManager.getVirtualKeys(player);
        boolean hasKeys = false;

        for (Crate crate : keys.keySet()) {
            int amount = keys.get(crate);

            if (amount > 0) {
                hasKeys = true;
                HashMap<String, String> placeholders = new HashMap<>();
                placeholders.put("%Crate%", crate.getFile().getString("Crate.Name", ""));
                placeholders.put("%Keys%", amount + "");
                message.add(Messages.PER_CRATE.getMessageNoPrefix(placeholders));
            }
        }

        if (hasKeys) {
            player.sendMessage(Messages.convertList(message));
        } else {
            player.sendMessage(Messages.PERSONAL_NO_VIRTUAL_KEYS.getMessage());
        }
    }

    @SubCommand("view")
    @Permission("crazycrates.command.player.key.others")
    public void viewOthers(CommandSender sender, @ArgName("username") Player target) {
        List<String> message = new ArrayList<>();
        message.add(Messages.OTHER_PLAYER_HEADER.getMessageNoPrefix("%Player%", target.getName()));
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
            sender.sendMessage(Messages.OTHER_PLAYER_NO_VIRTUAL_KEYS.getMessage("%Player%", target.getName()));
        }
    }
}
 */