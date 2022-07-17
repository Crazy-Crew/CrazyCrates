package com.badbones69.crazycrates.commands.subs.player.keys;

import com.badbones69.crazycrates.api.enums.settings.Messages;
import com.badbones69.crazycrates.commands.KeysBaseCommand;
import com.badbones69.crazycrates.support.utils.KeyUtils;
import com.google.inject.Inject;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import org.bukkit.entity.Player;

public class KeysViewCommand extends KeysBaseCommand {

    @Inject private KeyUtils keyUtils;

    @SubCommand("view")
    @Permission("crazycrates.command.player.key.others")
    public void viewOthers(Player player, Player target) {
        String header = Messages.OTHER_PLAYER_HEADER.getMessageNoPrefix().replace("%player%", target.getName());

        keyUtils.checkKeys(target, header, player);
    }
}