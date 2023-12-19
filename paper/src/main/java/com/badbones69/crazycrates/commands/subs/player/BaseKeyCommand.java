package com.badbones69.crazycrates.commands.subs.player;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.objects.Crate;
import com.google.common.collect.Lists;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.api.enums.Messages;
import java.util.HashMap;
import java.util.List;

@Command(value = "keys", alias = {"key"})
public class BaseKeyCommand extends BaseCommand {

    @NotNull
    private final CrazyCrates plugin = CrazyCrates.get();

    @Default
    @Permission("crazycrates.command.player.key")
    public void viewPersonal(Player player) {
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("%crates_opened%", String.valueOf(this.plugin.getCrazyHandler().getUserManager().getTotalCratesOpened(player.getUniqueId())));

        getKeys(player, player, Messages.no_virtual_keys_header.getMessage(placeholders).toString(), Messages.no_virtual_keys.getString());
    }

    @SubCommand("view")
    @Permission("crazycrates.command.player.key.others")
    public void viewOthers(CommandSender sender, @Suggestion ("online-players") Player target) {
        if (target == sender) {
            sender.sendMessage(Messages.same_player.getString());
            return;
        }

        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("%player%", target.getName());
        placeholders.put("%crates_opened%", String.valueOf(this.plugin.getCrazyHandler().getUserManager().getTotalCratesOpened(target.getUniqueId())));

        String header = Messages.other_player_no_keys_header.getMessage(placeholders).toString();

        String otherPlayer = Messages.other_player_no_keys.getMessage("%player%", target.getName()).toString();

        getKeys(target, sender, header, otherPlayer);
    }

    private void getKeys(Player player, CommandSender sender, String header, String messageContent) {
        List<String> message = Lists.newArrayList();

        message.add(header);

        HashMap<Crate, Integer> keys = new HashMap<>();

        this.plugin.getCrateManager().getCrates().forEach(crate -> keys.put(crate, this.plugin.getCrazyHandler().getUserManager().getVirtualKeys(player.getUniqueId(), crate.getName())));

        boolean hasKeys = false;

        for (Crate crate : keys.keySet()) {
            int amount = keys.get(crate);

            if (amount > 0) {
                HashMap<String, String> placeholders = new HashMap<>();

                hasKeys = true;

                placeholders.put("%crate%", crate.getFile().getString("Crate.Name"));
                placeholders.put("%keys%", String.valueOf(amount));
                placeholders.put("%crate_opened%", String.valueOf(this.plugin.getCrazyHandler().getUserManager().getCrateOpened(player.getUniqueId(), crate.getName())));
                message.add(Messages.per_crate.getMessage(placeholders).toString());
            }
        }

        if (hasKeys) {
            message.forEach(sender::sendMessage);
            return;
        }

        sender.sendMessage(messageContent);
    }
}