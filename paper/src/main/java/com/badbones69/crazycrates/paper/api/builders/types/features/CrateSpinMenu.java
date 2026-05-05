package com.badbones69.crazycrates.paper.api.builders.types.features;

import com.badbones69.crazycrates.paper.api.builders.gui.StaticInventoryBuilder;
import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.gui.GuiSettings;
import com.ryderbelserion.fusion.paper.builders.folia.FoliaScheduler;
import com.ryderbelserion.fusion.paper.builders.folia.Scheduler;
import com.ryderbelserion.fusion.paper.builders.gui.objects.GuiItem;
import com.ryderbelserion.fusion.paper.builders.gui.objects.border.GuiFiller;
import com.ryderbelserion.fusion.paper.builders.gui.types.simple.SimpleGui;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.Map;
import java.util.UUID;

public class CrateSpinMenu extends StaticInventoryBuilder {

    private final GuiSettings settings;

    public CrateSpinMenu(@NotNull final Player player, @NotNull final GuiSettings settings) {
        super(player, settings.getCrate(), settings.getTitle(), settings.getRows());

        this.settings = settings;
    }

    private final Player player = getPlayer();
    private final Crate crate = getCrate();
    private final SimpleGui gui = getGui();

    @Override
    public void open() {
        if (this.crate == null) return;

        if (this.settings.isFillerToggled()) {
            final GuiItem item = this.settings.getFillerStack(this.player);

            final GuiFiller guiFiller = this.gui.getFiller();

            final ItemStack itemStack = item.getItemStack();

            guiFiller.fill(this.settings.getFillerType(), itemStack);
        }

        final UUID uuid = this.player.getUniqueId();
        final String fileName = this.crate.getFileName();

        this.settings.getButtons().forEach((slot, button) -> this.gui.addSlotAction(slot, button.getGuiItem(this.player)));

        this.gui.setOpenAction(_ -> this.userManager.addRespinPrize(uuid, fileName, this.settings.getPrize().getSectionName()));

        this.gui.setCloseAction(_ -> {
            new FoliaScheduler(this.plugin, Scheduler.global_scheduler) {
                @Override
                public void run() {
                    if (userManager.hasRespinPrize(uuid, fileName)) { // if they have a respin prize, add it.
                        Messages.crate_prize_respin_not_claimed.sendMessage(player, Map.of(
                                "{crate_pretty}", crate.getCrateName(),
                                "{crate}", fileName,
                                "{prize}", userManager.getRespinPrize(uuid, fileName)
                        ));
                    }
                }
            }.runDelayed(20);

            this.crateManager.removePlayerFromOpeningList(this.player);
            this.crateManager.removeCrateInUse(this.player);
            this.crateManager.removeCrateTask(this.player);
            this.crateManager.endCrate(this.player);
        });

        this.gui.addSlotAction(this.settings.getSlot(), this.settings.getPrize().getDisplayItem(this.player, this.crate));

        this.gui.open(this.player);
    }
}