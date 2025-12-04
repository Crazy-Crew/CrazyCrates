package com.badbones69.crazycrates.paper.commands.crates.types.admin.crates;

import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.commands.crates.types.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionDefault;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import java.util.Map;

public class CommandAddItem extends BaseCommand {

    @Command("additem")
    @Permission(value = "crazycrates.additem", def = PermissionDefault.OP)
    @Syntax("/crazycrates additem <crate_name> <prize_number> <weight> [tier]")
    public void add(Player player, @ArgName("crate") @Suggestion("crates") String crateName, @ArgName("prize") @Suggestion("prizes") String prizeName, @ArgName("weight") @Suggestion("doubles") double weight, @ArgName("tier") @Suggestion("tiers") @Optional String tier) {
        if (crateName == null || crateName.isBlank()) {
            Messages.cannot_be_empty.sendMessage(player, "{value}", "crate name");

            return;
        }

        final ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType().isAir()) {
            Messages.cannot_be_air.sendMessage(player);

            return;
        }

        final Crate crate = getCrate(player, crateName, false);

        if (crate == null || crate.getCrateType() == CrateType.menu) {
            Messages.not_a_crate.sendMessage(player, "{crate}", crateName);

            return;
        }

        final String fancyName = crate.getCrateName();

        if (tier != null) {
            crate.addEditorItem(item, prizeName, tier, weight);

            Messages.added_item_with_editor.sendMessage(player, Map.of(
                    "{crate}", fancyName,
                    "{prize}", prizeName
            ));

            return;
        }

        crate.addEditorItem(item, prizeName, weight);

        Messages.added_item_with_editor.sendMessage(player, Map.of(
                "{crate}", fancyName,
                "{prize}", prizeName
        ));
    }
}