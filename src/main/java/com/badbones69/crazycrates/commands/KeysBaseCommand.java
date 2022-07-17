package com.badbones69.crazycrates.commands;

import com.badbones69.crazycrates.api.enums.settings.Messages;
import com.badbones69.crazycrates.support.utils.KeyUtils;
import com.google.inject.Inject;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import org.bukkit.entity.Player;

@Command(value = "keys", alias = {"key"})
public class KeysBaseCommand extends BaseCommand {

    @Inject private KeyUtils keyUtils;

    @Default
    @Permission("crazycrates.command.player.key")
    public void view(Player player) {
        keyUtils.checkKeys(player, Messages.PERSONAL_HEADER.getMessageNoPrefix(), null);
    }
}