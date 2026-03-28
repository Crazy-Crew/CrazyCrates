package com.badbones69.crazycrates.paper.commands.types.admin;

import com.badbones69.crazycrates.paper.api.commands.CratesCommand;
import com.badbones69.crazycrates.paper.utils.ItemUtils;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.kyori.permissions.PermissionContext;
import com.ryderbelserion.fusion.kyori.permissions.enums.PermissionType;
import com.ryderbelserion.fusion.paper.builders.commands.context.PaperCommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.ArrayList;
import java.util.List;

public class TestCommand extends CratesCommand {

    @Override
    public void run(@NotNull final PaperCommandContext context) {
        this.crateManager.getCrate("CrateExample").ifPresent(crate -> {
            final CommentedConfigurationNode configuration = crate.getConfiguration().node("Prizes");

            final List<ItemStack> items = new ArrayList<>();

            for (final Object key : configuration.childrenMap().keySet()) {
                final CommentedConfigurationNode prize = configuration.node(key).node("Items");

                this.fusion.log(Level.WARNING, "<red>Prize: <green>%s".formatted(prize.path()));

                for (final CommentedConfigurationNode node : prize.childrenMap().values()) {
                    this.fusion.log(Level.WARNING, "<red>Item: <green>%s <yellow>%s".formatted(node.path(), node.node("name").getString("N/A")));

                    items.add(ItemUtils.convertNode(node).asItemStack());
                }
            }

            final Player player = context.getPlayer();

            for (final ItemStack itemStack : items) {
                player.getInventory().addItem(itemStack);
            }
        });
    }

    @Override
    public @NotNull final LiteralCommandNode<CommandSourceStack> literal() {
        return Commands.literal("test").requires(this::requirement)
                .executes(context -> {
                    run(new PaperCommandContext(context));

                    return Command.SINGLE_SUCCESS;
                }).build();
    }

    @Override
    public @NotNull final List<PermissionContext> getPermissions() {
        return List.of(
                new PermissionContext(
                        "crazycrates.test",
                        "Tests the item builder",
                        PermissionType.TRUE
                )
        );
    }

    @Override
    public final boolean requirement(@NotNull final CommandSourceStack context) {
        return context.getSender().hasPermission(getPermissions().getFirst().getPermission());
    }
}