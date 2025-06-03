package com.badbones69.crazycrates.paper.api.builders.types.features;

import com.badbones69.crazycrates.paper.api.builders.gui.StaticInventoryBuilder;
import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.gui.GuiSettings;
import com.ryderbelserion.fusion.paper.api.builders.gui.interfaces.Gui;
import com.ryderbelserion.fusion.paper.api.builders.gui.interfaces.GuiFiller;
import com.ryderbelserion.fusion.paper.api.builders.gui.interfaces.GuiItem;
import com.ryderbelserion.fusion.paper.api.enums.Scheduler;
import com.ryderbelserion.fusion.paper.api.scheduler.FoliaScheduler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CrateSpinMenu extends StaticInventoryBuilder {

    private final GuiSettings settings;

    public CrateSpinMenu(@NotNull final Player player, @NotNull final GuiSettings settings) {
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

        final UUID uuid = player.getUniqueId();
        final String fileName = this.crate.getFileName();

        this.settings.getButtons().forEach((slot, button) -> this.gui.setItem(slot, button.getGuiItem()));

        this.gui.setOpenGuiAction(action -> this.userManager.addRespinPrize(uuid, fileName, this.settings.getPrize().getSectionName()));

        this.gui.setCloseGuiAction(action -> {
            new FoliaScheduler(this.plugin, Scheduler.global_scheduler) {
                @Override
                public void run() {
                    if (userManager.hasRespinPrize(uuid, fileName)) { // if they have a respin prize, add it.
                        Messages.crate_prize_respin_not_claimed.sendMessage(player, new HashMap<>() {{
                            put("{crate_pretty}", crate.getCrateName());
                            put("{crate}", fileName);
                            put("{prize}", userManager.getRespinPrize(uuid, fileName));
                        }});
                    }
                }
            }.runDelayed(20);

            this.crateManager.removePlayerFromOpeningList(this.player);
            this.crateManager.removeCrateInUse(this.player);
            this.crateManager.removeCrateTask(this.player);
            this.crateManager.endCrate(this.player);
        });

        this.gui.setItem(this.settings.getSlot(), new GuiItem(this.settings.getPrize().getDisplayItem(this.player, this.crate)));

        this.gui.open(this.player);
    }
}