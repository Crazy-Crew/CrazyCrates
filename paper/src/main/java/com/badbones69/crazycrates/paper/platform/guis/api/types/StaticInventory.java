package com.badbones69.crazycrates.paper.platform.guis.api.types;

import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.platform.guis.api.AbstractGui;
import com.ryderbelserion.fusion.paper.builders.gui.enums.GuiState;
import com.ryderbelserion.fusion.paper.builders.gui.types.simple.SimpleGui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class StaticInventory extends AbstractGui {

    protected final SimpleGui gui;

    public StaticInventory(@NotNull final Player player, @Nullable final Crate crate, @NotNull final String title, final int size) {
        super(player, crate, title, size);

        this.gui = SimpleGui.gui(this.plugin, title, size).addState(GuiState.block_all_interactions);
    }
}