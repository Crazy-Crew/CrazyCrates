package com.badbones69.crazycrates.paper.commands;

import com.badbones69.crazycrates.paper.api.commands.CratesCommand;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.ryderbelserion.fusion.kyori.permissions.PermissionContext;
import com.ryderbelserion.fusion.kyori.permissions.enums.PermissionType;
import com.ryderbelserion.fusion.paper.builders.commands.context.PaperCommandContext;
import com.ryderbelserion.fusion.paper.builders.gui.enums.GuiState;
import com.ryderbelserion.fusion.paper.builders.gui.types.simple.SimpleGui;
import com.ryderbelserion.fusion.paper.builders.items.ItemBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import com.badbones69.common.config.impl.ConfigKeys;
import java.util.Collection;
import java.util.List;

public class BaseCommand extends CratesCommand {

    @Override
    public void run(@NotNull final PaperCommandContext context) {
        final SimpleGui gui = SimpleGui.gui(
                this.plugin,
                this.config.getProperty(ConfigKeys.inventory_name),
                this.config.getProperty(ConfigKeys.inventory_rows)
        );

        final Collection<Crate> crates = this.crateManager.getCrates().values();

        if (crates.isEmpty()) {
            return;
        }

        for (final Crate crate : crates) {
            if (!crate.isDisplayButton()) {
                continue;
            }

            gui.addSlotAction(crate.getDisplaySlot(), new ItemBuilder(ItemType.CHEST).withDisplayName(crate.getCrateName()).asItemStack(), action -> {
                final HumanEntity player = action.getWhoClicked();

                crate.getSound("click").ifPresent(sound -> sound.play(player));
            });
        }

        gui.addState(GuiState.block_all_interactions);

        gui.build(context.getPlayer());
    }

    @Override
    public @NotNull final LiteralCommandNode<CommandSourceStack> literal() {
        return Commands.literal("crazycrates").requires(this::requirement).executes(context -> {
            run(new PaperCommandContext(context));

            return Command.SINGLE_SUCCESS;
        }).build();
    }

    @Override
    public @NotNull final List<PermissionContext> getPermissions() {
        return List.of(
                new PermissionContext(
                        "crazycrates.gui",
                        "Opens the primary crate menu.",
                        PermissionType.TRUE
                )
        );
    }

    @Override
    public final boolean requirement(@NotNull final CommandSourceStack context) {
        return context.getSender() instanceof Player && context.getSender().hasPermission(getPermissions().getFirst().getPermission());
    }
}