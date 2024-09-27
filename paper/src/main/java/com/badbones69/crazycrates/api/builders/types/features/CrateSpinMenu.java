package com.badbones69.crazycrates.api.builders.types.features;

import com.badbones69.crazycrates.api.builders.gui.StaticInventoryBuilder;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.gui.GuiSettings;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.Gui;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.GuiFiller;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.GuiItem;
import org.bukkit.entity.Player;
import java.util.List;

public class CrateSpinMenu extends StaticInventoryBuilder {

    private final GuiSettings settings;

    public CrateSpinMenu(final Player player, final GuiSettings settings) {
        super(player, settings.getCrate(), settings.getTitle(), settings.getRows());

        this.settings = settings;
    }

    private final Player player = getPlayer();
    private final Crate crate = getCrate();
    private final Gui gui = getGui();

    @Override
    public void open() {
        if (this.crate == null) return;

        if (this.settings.isFillerToggled()) {
            final GuiItem item = this.settings.getFillerStack();

            final GuiFiller guiFiller = this.gui.getFiller();

            switch (this.settings.getFillerType()) {
                case FILL -> guiFiller.fill(item);

                case FILL_BORDER -> guiFiller.fillBorder(item);

                case FILL_TOP -> guiFiller.fillTop(item);

                case FILL_SIDE -> guiFiller.fillSide(GuiFiller.Side.BOTH, List.of(item));

                case FILL_BOTTOM -> guiFiller.fillBottom(item);
            }
        }

        this.settings.getButtons().forEach((slot, button) -> this.gui.setItem(slot, button.getGuiItem()));

        this.gui.setOpenGuiAction(action -> this.userManager.addRespinPrize(this.player.getUniqueId(), this.crate.getFileName(), this.settings.getPrize().getSectionName()));

        this.gui.setItem(this.settings.getSlot(), new GuiItem(this.settings.getPrize().getDisplayItem(this.player, this.crate)));

        this.gui.open(this.player);
    }
}