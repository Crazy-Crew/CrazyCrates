package com.badbones69.crazycrates.commands.crates.types.admin.keys;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.commands.crates.types.BaseCommand;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;

public class CommandRespin extends BaseCommand {

    @Command("respin")
    @Permission(value = "crazycrates.respin.use", def = PermissionDefault.OP, description = "Allows the sender to access /crazycrates respin.")
    public void root(final CommandSender sender) {
        //todo() send help message
    }

    public class CommandRespinAdd {

        protected @NotNull final CrazyCrates plugin = CrazyCrates.getPlugin();

        protected @NotNull final BukkitUserManager userManager = this.plugin.getUserManager();

        @Command("add")
        @Permission(value = "crazycrates.respin.add", def = PermissionDefault.OP, description = "Allows the sender to add a respin for another person.")
        public void add(final CommandSender sender, @Suggestion("players") final Player target, @Suggestion("crates") final String crateName, @Suggestion("numbers") final int amount) {
            if (crateName == null || crateName.isEmpty() || crateName.isBlank()) {
                Messages.cannot_be_empty.sendMessage(sender, "{value}", "crate name");

                return;
            }

            if (amount <= 0) {
                Messages.not_a_number.sendMessage(sender, "{number}", String.valueOf(amount));

                return;
            }

            final Crate crate = getCrate(sender, crateName, false);

            if (crate == null || crate.getCrateType() == CrateType.menu) {
                Messages.not_a_crate.sendMessage(sender, "{crate}", crateName);

                return;
            }

            this.userManager.addRespinCrate(target.getUniqueId(), crate.getFileName(), amount);

            //todo() send message that they added a prize.
        }
    }

    public class CommandRespinRemove {

        protected @NotNull final CrazyCrates plugin = CrazyCrates.getPlugin();

        protected @NotNull final BukkitUserManager userManager = this.plugin.getUserManager();

        @Command("remove")
        @Permission(value = "crazycrates.respin.remove", def = PermissionDefault.OP, description = "Allows the sender to remove a respin from another person.")
        public void remove(final CommandSender sender, @Suggestion("players") final Player target, @Suggestion("crates") final String crateName, @Suggestion("numbers") final int amount) {
            if (crateName == null || crateName.isEmpty() || crateName.isBlank()) {
                Messages.cannot_be_empty.sendMessage(sender, "{value}", "crate name");

                return;
            }

            if (amount <= 0) {
                Messages.not_a_number.sendMessage(sender, "{number}", String.valueOf(amount));

                return;
            }

            final Crate crate = getCrate(sender, crateName, false);

            if (crate == null || crate.getCrateType() == CrateType.menu) {
                Messages.not_a_crate.sendMessage(sender, "{crate}", crateName);

                return;
            }

            this.userManager.removeRespinCrate(target.getUniqueId(), crate.getFileName(), amount);

            //todo() send message that they added a prize.
        }
    }
}