package com.ryderbelserion.crazycrates.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.ryderbelserion.crazycrates.CrazyCrates;
import com.ryderbelserion.crazycrates.loader.CrazyPlugin;
import com.ryderbelserion.vital.common.api.commands.Command;
import com.ryderbelserion.vital.paper.commands.PaperCommand;
import com.ryderbelserion.vital.paper.commands.context.PaperCommandInfo;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;

public class BaseCommand extends PaperCommand {

    private final CrazyPlugin plugin;

    public BaseCommand(CrazyCrates plugin) {
        this.plugin = plugin.getPlugin();
    }

    @Override
    public void execute(PaperCommandInfo info) {
        final Player player = info.getPlayer();

        player.sendMessage("This is the base command!");
    }

    @Override
    public @NotNull String getPermission() {
        return "crazycrates.access";
    }

    @Override
    public @NotNull LiteralCommandNode<CommandSourceStack> literal() {
        final LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("crazycrates").requires(source -> source.getSender().hasPermission(getPermission()));

        root.executes(context -> {
            execute(new PaperCommandInfo(context));

            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
        });

        return root.build();
    }

    @Override
    public @NotNull Command<CommandSourceStack, PaperCommandInfo> registerPermission() {
        final Permission permission = this.plugin.getServer().getPluginManager().getPermission(getPermission());

        if (permission == null) {
            this.plugin.getServer().getPluginManager().addPermission(new Permission(getPermission(), PermissionDefault.OP));
        }

        return this;
    }
}