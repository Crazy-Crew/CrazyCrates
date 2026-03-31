package com.badbones69.crazycrates.paper.commands.types.admin;

import com.badbones69.common.api.enums.keys.FileKeys;
import com.badbones69.common.utils.ConfigUtils;
import com.badbones69.crazycrates.paper.api.commands.CratesCommand;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.ryderbelserion.fusion.files.types.configurate.JsonCustomFile;
import com.ryderbelserion.fusion.kyori.permissions.PermissionContext;
import com.ryderbelserion.fusion.kyori.permissions.enums.PermissionType;
import com.ryderbelserion.fusion.mojang.enums.SuggestionType;
import com.ryderbelserion.fusion.paper.builders.commands.context.PaperCommandContext;
import com.ryderbelserion.fusion.paper.utils.ItemUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import static io.papermc.paper.command.brigadier.Commands.argument;

public class ItemCommand extends CratesCommand {

    @Override
    public void run(@NotNull final PaperCommandContext context) {
        final String name = context.getStringArgument("name");

        if (name.isBlank()) {
            return;
        }

        final CommandContext<CommandSourceStack> source = context.getContext();

        final ItemStack itemStack = source.getArgument("item", ItemStack.class);

        if (itemStack == null || itemStack.isEmpty()) {
            return;
        }

        final JsonCustomFile customFile = FileKeys.items.getJsonCustomFile();

        ConfigUtils.setValue(customFile.getConfiguration().node("items"), ItemUtils.toBase64(itemStack), name);

        customFile.save();
    }

    @Override
    public @NotNull final LiteralCommandNode<CommandSourceStack> literal() {
        final LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("item").requires(this::requirement);

        final RequiredArgumentBuilder<CommandSourceStack, String> arg1 = argument("name", StringArgumentType.string())
                .suggests((ctx, builder) -> suggestArgument(builder, SuggestionType.STRING_SUGGESTION, "", 5, 0));

        final RequiredArgumentBuilder<CommandSourceStack, ItemStack> arg2 = argument("item", ArgumentTypes.itemStack())
                .executes(context -> {
                    run(new PaperCommandContext(context));

                    return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                });

        return root.then(arg1.then(arg2)).build();
    }

    @Override
    public @NotNull final List<PermissionContext> getPermissions() {
        return List.of(
                new PermissionContext(
                        "crazycrates.item",
                        "Builds an ItemStack, and adds it to item.json",
                        PermissionType.OP
                )
        );
    }

    @Override
    public final boolean requirement(@NotNull final CommandSourceStack context) {
        return context.getSender().hasPermission(getPermissions().getFirst().getPermission());
    }
}