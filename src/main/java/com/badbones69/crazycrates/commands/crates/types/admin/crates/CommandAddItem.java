package com.badbones69.crazycrates.commands.crates.types.admin.crates;

import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.commands.crates.types.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.ArgName;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Optional;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionDefault;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import java.util.HashMap;

public class CommandAddItem extends BaseCommand {

    @Command("additem")
    @Permission(value = "crazycrates.additem", def = PermissionDefault.OP)
    public void add(Player player, @ArgName("crate") @Suggestion("crates") String crateName, @ArgName("prize") @Suggestion("prizes") String prizeName, @ArgName("chance") @Suggestion("numbers") int chance, @ArgName("tier") @Suggestion("tiers") @Optional String tier) {
        if (crateName == null || crateName.isEmpty() || crateName.isBlank()) {
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
            crate.addEditorItem(item, prizeName, tier, chance);

            Messages.added_item_with_editor.sendMessage(player, new HashMap<>() {{
                put("{crate}", fancyName);
                put("{prize}", prizeName);
            }});

            return;
        }

        crate.addEditorItem(item, prizeName, chance);

        Messages.added_item_with_editor.sendMessage(player, new HashMap<>() {{
            put("{crate}", fancyName);
            put("{prize}", prizeName);
        }});
    }
}