package com.ryderbelserion.crazycrates.paper.api.objects.crate;

import com.ryderbelserion.crazycrates.paper.CrazyCrates;
import com.ryderbelserion.crazycrates.paper.api.objects.buttons.Button;
import com.ryderbelserion.crazycrates.paper.api.objects.other.CratePrize;
import com.ryderbelserion.crazycrates.paper.api.objects.prize.Prize;
import com.ryderbelserion.fusion.paper.builders.gui.enums.GuiState;
import com.ryderbelserion.fusion.paper.builders.gui.types.paginated.PaginatedGui;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@NullMarked
public final class CrateGui {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final Crate crate;
    private final String name;
    private final int rows;

    public CrateGui(final Crate crate, final CommentedConfigurationNode configuration) {
        this.name = configuration.node("name").getString("");
        this.rows = configuration.node("rows").getInt(6);

        this.crate = crate;
    }

    public PaginatedGui getGui(final Player entity) {
        final PaginatedGui gui = PaginatedGui.gui(
                this.plugin,
                this.name,
                this.rows
        );

        final Collection<CrateButton> buttons = this.crate.getButtons().values();

        final Map<String, String> placeholders = Map.of(
                "{player}", entity.getName()
        );

        for (final CrateButton crateButton : buttons) {
            final Button button = crateButton.getButton();

            button.getDisplayItem().addItem(entity, gui, crateButton.getSlot(), _ -> button.execute(entity, placeholders));
        }

        final Collection<CratePrize> values = this.crate.getPrizes().values();

        for (final CratePrize value : values) {
            final List<Prize> prizes = value.getKeys();

            for (final Prize prize : prizes) {
                prize.getDisplayItem().addItem(entity, gui, -1);
            }
        }

        gui.addState(GuiState.block_all_interactions);

        /*gui.setDefaultAction(event -> {

        });*/

        return gui;
    }
}