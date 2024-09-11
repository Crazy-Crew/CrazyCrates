package com.badbones69.crazycrates.commands;

import com.badbones69.crazycrates.CrazyCrates;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.Gui;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.GuiFiller;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.GuiItem;
import com.ryderbelserion.vital.paper.api.builders.gui.types.PaginatedGui;
import com.ryderbelserion.vital.paper.api.builders.items.ItemBuilder;
import com.ryderbelserion.vital.paper.commands.PaperCommand;
import com.ryderbelserion.vital.paper.commands.context.PaperCommandInfo;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class CommandGui extends PaperCommand {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final List<ItemStack> itemStacks = new ArrayList<>();

    public CommandGui() {
        final ItemStack itemStack = new ItemStack(Material.IRON_INGOT);

        this.itemStacks.addAll(List.of(
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack,
                itemStack
        ));
    }

    @Override
    public void execute(PaperCommandInfo info) {
        if (!info.isPlayer()) return;

        final PaginatedGui gui = Gui.paginated().setTitle("Beans").setRows(6).disableInteractions().create();

        final GuiFiller guiFiller = gui.getFiller();

        final Material glass = Material.BLACK_STAINED_GLASS_PANE;

        this.itemStacks.forEach(itemStack -> gui.addItem(new GuiItem(itemStack)));

        guiFiller.fillTop(new GuiItem(glass));
        guiFiller.fillBottom(new GuiItem(glass));

        final Material arrow = Material.ARROW;

        final int page = gui.getCurrentPageNumber();

        if (page > 1) {
            setPreviousButton(gui, arrow);
        }

        if (page < gui.getMaxPages()) {
            setNextButton(gui, arrow);
        }

        gui.open(info.getPlayer());
    }

    private void setNextButton(final PaginatedGui gui, final Material material) {
        gui.setItem(6, 6, new GuiItem(new ItemBuilder<>().withType(material).setDisplayName("<red>Next Page #{page}").addNamePlaceholder("{page}", String.valueOf(gui.getNextPageNumber())).getStack(), event -> {
            event.setCancelled(true);

            gui.next();

            final int page = gui.getCurrentPageNumber();

            if (page < gui.getMaxPages()) {
                setNextButton(gui, material);
            } else {
                gui.setItem(6, 6, new GuiItem(Material.BLACK_STAINED_GLASS_PANE));
            }

            if (page > 1) {
                setPreviousButton(gui, material);
            } else {
                gui.setItem(6, 4, new GuiItem(Material.BLACK_STAINED_GLASS_PANE));
            }
        }));
    }

    private void setPreviousButton(final PaginatedGui gui, final Material material) {
        gui.setItem(6, 4, new GuiItem(new ItemBuilder<>().withType(material).setDisplayName("<red>Previous Page #{page}").addNamePlaceholder("{page}", String.valueOf(gui.getPreviousPageNumber())).getStack(), event -> {
            event.setCancelled(true);

            gui.previous();

            final int page = gui.getCurrentPageNumber();

            if (page > 1) {
                setPreviousButton(gui, material);
            } else {
                gui.setItem(6, 4, new GuiItem(Material.BLACK_STAINED_GLASS_PANE));
            }

            if (page < gui.getMaxPages()) {
                setNextButton(gui, material);
            }
        }));
    }

    @Override
    public @NotNull final String getPermission() {
        return "vital.gui";
    }

    @Override
    public @NotNull final LiteralCommandNode<CommandSourceStack> literal() {
        return Commands.literal("vgui")
                .requires(source -> source.getSender().hasPermission(getPermission()))
                .executes(context -> {
                    execute(new PaperCommandInfo(context));

                    return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                }).build();
    }

    @Override
    public @NotNull final PaperCommand registerPermission() {
        final Permission permission = this.plugin.getServer().getPluginManager().getPermission(getPermission());

        if (permission == null) {
            this.plugin.getServer().getPluginManager().addPermission(new Permission(getPermission(), PermissionDefault.OP));
        }

        return this;
    }
}