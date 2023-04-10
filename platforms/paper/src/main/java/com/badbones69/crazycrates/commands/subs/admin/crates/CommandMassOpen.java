package com.badbones69.crazycrates.commands.subs.admin.crates;

import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.enums.settings.Messages;
import com.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.commands.subs.CommandManager;
import com.badbones69.crazycrates.enums.types.KeyType;
import com.badbones69.crazycrates.listeners.CrateControlListener;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class CommandMassOpen extends CommandManager {

    @SubCommand("mass-open")
    @Permission(value = "crazycrates.command.admin.massopen", def = PermissionDefault.OP)
    public void onAdminCrateMassOpen(CommandSender sender, @Suggestion("crates") String crateName, @Suggestion("numbers") int amount) {
        if (!(sender instanceof Player player)) return;

        Crate crate = crazyManager.getCrateFromName(crateName);
        if (crate == null || crateName.equalsIgnoreCase("menu")) {
            sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", crateName));
            return;
        }

        crazyManager.addPlayerToOpeningList(player, crate);

        int keys = crazyManager.getVirtualKeys(player, crate);
        int keysUsed = 0;

        if (keys == 0) {
            player.sendMessage(Messages.NO_VIRTUAL_KEY.getMessage());
            return;
        }

        for (; keys > 0; keys--) {
            if (Methods.isInventoryFull(player)) break;
            if (keysUsed > amount) break;
            if (keysUsed >= crate.getMaxMassOpen()) break;

            Prize prize = crate.pickPrize(player);
            crazyManager.givePrize(player, prize, crate);
            plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), prize));

            if (prize.useFireworks()) Methods.firework(((Player) sender).getLocation().clone().add(.5, 1, .5));

            keysUsed++;
        }

        if (!crazyManager.takeKeys(keysUsed, player, crate, KeyType.VIRTUAL_KEY, false)) {
            Methods.failedToTakeKey(player, crate);
            CrateControlListener.inUse.remove(player);
            crazyManager.removePlayerFromOpeningList(player);
            return;
        }
        crazyManager.removePlayerFromOpeningList(player);
    }

}