package com.badbones69.crazycrates.paper.commands;

import com.badbones69.common.api.enums.keys.FileKeys;
import com.badbones69.crazycrates.paper.api.commands.CratesCommand;
import com.badbones69.crazycrates.paper.api.objects.crate.Crate;
import com.badbones69.crazycrates.paper.api.objects.items.DisplayItem;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.ryderbelserion.fusion.kyori.permissions.PermissionContext;
import com.ryderbelserion.fusion.kyori.permissions.enums.PermissionType;
import com.ryderbelserion.fusion.paper.builders.commands.context.PaperCommandContext;
import com.ryderbelserion.fusion.paper.builders.gui.enums.GuiState;
import com.ryderbelserion.fusion.paper.builders.gui.types.simple.SimpleGui;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.Collection;
import java.util.List;

public class BaseCommand extends CratesCommand {

    @Override
    public void run(@NotNull final PaperCommandContext context) {
        final CommentedConfigurationNode configuration = FileKeys.crate_menu.getYamlConfig();

        final SimpleGui gui = SimpleGui.gui(
                this.plugin,
                configuration.node("gui", "name").getString("<bold><gradient:#e91e63:blue>Crazy Crates</gradient></bold>"),
                configuration.node("gui", "rows").getInt(3)
        );

        final Collection<Crate> crates = this.crateManager.getCrates().values();

        if (crates.isEmpty()) {
            return;
        }

        final Player player = context.getPlayer();

        for (final Crate crate : crates) {
            final DisplayItem item = crate.getDisplayItem();

            item.addItem(player, gui, event -> crate.getSound("click").ifPresent(sound -> sound.play(event.getWhoClicked())));
        }

        gui.addState(GuiState.block_all_interactions);

        gui.build(player);
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