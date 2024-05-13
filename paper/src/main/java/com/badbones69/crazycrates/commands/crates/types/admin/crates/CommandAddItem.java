package com.badbones69.crazycrates.commands.crates.types.admin.crates;

import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.commands.crates.types.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Optional;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionDefault;
import us.crazycrew.crazycrates.api.enums.types.CrateType;

public class CommandAddItem extends BaseCommand {

    @Command("additem")
    @Permission(value = "crazycrates.additem", def = PermissionDefault.OP)
    public void add(Player player, @Suggestion("crates") String crateName, @Suggestion("prizes") String prizeName, @Suggestion("numbers") int chance, @Suggestion("tiers") @Optional String tier) {
        if (crateName.isEmpty() || crateName.isBlank()) {
            player.sendRichMessage(Messages.cannot_be_empty.getMessage(player, "{value}", "crate name"));

            return;
        }

        final ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType().isAir()) {
            player.sendRichMessage(Messages.cannot_be_air.getMessage(player));

            return;
        }

        final Crate crate = getCrate(player, crateName, false);

        if (crate == null || crate.getCrateType() == CrateType.menu) {
            player.sendRichMessage(Messages.not_a_crate.getMessage(player, "{crate}", crateName));

            return;
        }

        if (tier != null) {
            crate.addEditorItem(item, prizeName, tier, chance);

            return;
        }

        crate.addEditorItem(item, player, prizeName, chance);
    }
}