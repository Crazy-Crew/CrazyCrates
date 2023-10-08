package com.badbones69.crazycrates.paper.commands.subs.player;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.CrazyManager;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.paper.api.plugin.CrazyHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Command(value = "keys", alias = {"key"})
public class BaseKeyCommand extends BaseCommand {

    private final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private final @NotNull CrazyHandler crazyHandler = this.plugin.getCrazyHandler();

    private final @NotNull CrazyManager crazyManager = this.crazyHandler.getCrazyManager();

    @Default
    @Permission(value = "crazycrates.key", def = PermissionDefault.TRUE)
    public void viewPersonalKeys(Player player) {
        //TODO() Update message enum.
        //String header = Messages.PERSONAL_HEADER.getMessageNoPrefix();

        //String noKeys = Messages.PERSONAL_NO_VIRTUAL_KEYS.getMessage();

        //getKeys(player, player, header, noKeys);
    }

    @SubCommand("view")
    @Permission(value = "crazycrates.key-others", def = PermissionDefault.TRUE)
    public void viewOthersKeys(CommandSender sender, @Suggestion ("online-players") Player target) {
        if (target == sender) {
            //TODO() Update message enum.
            //sender.sendMessage(Messages.SAME_PLAYER.getMessage());
            return;
        }

        //TODO() Update message enum.
        //String header = Messages.OTHER_PLAYER_HEADER.getMessageNoPrefix("%Player%", target.getName());
        //String otherPlayer = Messages.OTHER_PLAYER_NO_VIRTUAL_KEYS.getMessage("%Player%", target.getName());

        //getKeys(target, sender, header, otherPlayer);
    }

    private void getKeys(Player player, CommandSender sender, String header, String messageContent) {
        List<String> message = new ArrayList<>();

        message.add(header);

        HashMap<Crate, Integer> keys = new HashMap<>();

        this.crazyManager.getCrates().forEach(crate -> keys.put(crate, this.crazyHandler.getUserManager().getVirtualKeys(player.getUniqueId(), crate.getName())));

        boolean hasKeys = false;

        for (Crate crate : keys.keySet()) {
            int amount = keys.get(crate);

            if (amount > 0) {
                hasKeys = true;

                HashMap<String, String> placeholders = new HashMap<>();

                placeholders.put("{crate}", crate.getFile().getString("Crate.Name"));
                placeholders.put("{keys}", String.valueOf(amount));
                //TODO() Update message enum.
                //message.add(Messages.PER_CRATE.getMessageNoPrefix(placeholders));
            }
        }

        if (hasKeys) {
            message.forEach(sender::sendMessage);
            return;
        }

        sender.sendMessage(messageContent);
    }
}