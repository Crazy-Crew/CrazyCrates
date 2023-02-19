package com.badbones69.crazycrates.commands.subs.player;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.objects.Crate;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.HashMap;

@Command(value = "keys", alias = {"key"})
public class BaseKeyCommand extends BaseCommand {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrazyManager crazyManager = plugin.getStarter().getCrazyManager();

    @Default
    @Permission("crazycrates.command.player.key")
    public void viewPersonal(Player player) {
        //List<String> otherPlayerHeader = Locale.KEYS_OTHER_PLAYER_VIRTUAL_KEYS_HEADER;
        //String otherPlayer = Locale.KEYS_OTHER_PLAYER_NO_VIRTUAL_KEYS;

        //getKeys(player, player, Methods.convertList(otherPlayerHeader).replaceAll("%Player%", player.getName()), otherPlayer.replaceAll("%Player%", player.getName()));
    }

    @SubCommand("view")
    @Permission("crazycrates.command.player.key.others")
    public void viewOthers(CommandSender sender, @Suggestion ("online-players") Player target) {
        if (target == sender) {
            //ColorUtils.sendMessage(sender, Locale.TARGET_SAME_PLAYER, true);
            return;
        }

        //List<String> otherPlayerHeader = Locale.KEYS_OTHER_PLAYER_VIRTUAL_KEYS_HEADER;
        //String otherPlayer = Locale.KEYS_OTHER_PLAYER_NO_VIRTUAL_KEYS;

        //getKeys(target, sender, Methods.convertList(otherPlayerHeader).replaceAll("%Player%", target.getName()), otherPlayer.replaceAll("%Player%", target.getName()));
    }

    private void getKeys(Player target, CommandSender sender, String header, String subHeader) {
        //List<String> message = new ArrayList<>();

        //message.add(header);

        HashMap<Crate, Integer> keys = crazyManager.getVirtualKeys(target);

        boolean hasKeys = false;

        for (Crate crate : keys.keySet()) {
            int amount = keys.get(crate);

            if (amount > 0) {
                hasKeys = true;

                //message.add(Locale.CRATE_FORMAT.replace("%crates%", crate.getFile().getString("Crate.Name")).replace("%keys%", amount + ""));
            }
        }

        if (hasKeys) {
            //sender.sendMessage(ColorUtils.color(Methods.convertList(message)));
            return;
        }

        //sender.sendMessage(subHeader);
    }
}