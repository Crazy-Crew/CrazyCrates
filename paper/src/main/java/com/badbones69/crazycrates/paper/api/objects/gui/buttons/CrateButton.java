package com.badbones69.crazycrates.paper.api.objects.gui.buttons;

import com.badbones69.crazycrates.paper.api.PrizeManager;
import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import com.badbones69.crazycrates.paper.managers.BukkitUserManager;
import com.badbones69.crazycrates.paper.managers.events.enums.EventType;
import com.badbones69.crazycrates.paper.tasks.crates.CrateManager;
import com.ryderbelserion.fusion.paper.builders.gui.interfaces.GuiAction;
import com.ryderbelserion.fusion.paper.builders.gui.objects.GuiItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import java.util.Map;
import java.util.UUID;

public class CrateButton extends GuiButton {

    private final BukkitUserManager userManager = this.plugin.getUserManager();
    private final CrateManager crateManager = this.plugin.getCrateManager();

    private final Crate crate;
    private final Prize prize;

    public CrateButton(@NotNull final Crate crate, @Nullable final Prize prize, @NotNull final ConfigurationSection section) {
        super(section, Map.of(
                "%crate_pretty%", crate.getCrateName(),
                "%crate_raw%", crate.getFileName(),
                "%prize%", prize != null ? prize.getSectionName() : "N/A"
        ));

        this.crate = crate;
        this.prize = prize;
    }

    @Override
    public @NotNull final GuiItem getGuiItem(@NotNull final Player player) { //todo() test if we even need to check if clicker is player lol
        final GuiItem guiItem = super.getGuiItem(player);

        final GuiAction<InventoryClickEvent> action = guiItem.getAction();

        guiItem.setAction(event -> {
            action.execute(event);

            if (!(event.getWhoClicked() instanceof Player clicker)) return;

            final UUID uuid = clicker.getUniqueId();
            final String crateName = this.crate.getFileName();

            switch (getSection().getName()) {
                case "accept" -> {
                    final Prize prize = this.crate.getPrize(this.userManager.getRespinPrize(uuid, crateName));

                    PrizeManager.givePrize(clicker, this.crate, prize);

                    if (!this.crate.isCyclePersistRestart()) {
                        this.userManager.removeRespinCrate(uuid, crateName, this.userManager.getCrateRespin(uuid, crateName));
                    }

                    this.userManager.removeRespinPrize(uuid, crateName);

                    this.crateManager.removePlayerFromOpeningList(clicker);
                    this.crateManager.removeCrateInUse(clicker);
                    this.crateManager.removeCrateTask(clicker);
                    this.crateManager.endCrate(clicker);
                }

                case "deny" -> {
                    if (PrizeManager.isCapped(this.crate, clicker)) {
                        final Prize prize = this.crate.getPrize(this.userManager.getRespinPrize(uuid, crateName));

                        PrizeManager.givePrize(clicker, this.crate, prize);

                        this.userManager.removeRespinPrize(uuid, crateName); // remove just in case

                        // remove from the cache
                        if (!this.crate.isCyclePersistRestart()) {
                            this.userManager.removeRespinCrate(uuid, crateName, this.userManager.getCrateRespin(uuid, crateName));
                        }

                        final int cap = PrizeManager.getCap(this.crate, clicker);

                        Messages.crate_prize_max_respins.sendMessage(clicker, Map.of("{status}", cap >= 1 ?
                                Messages.crate_prize_max_respins_left.getMessage(clicker, Map.of("{respins_total}", String.valueOf(cap), "{respins_left}", "0")) :
                                Messages.crate_prize_max_respins_none.getMessage(clicker)));

                        return;
                    }

                    this.userManager.addRespinCrate(uuid, crateName, 1);

                    this.crateManager.openCrate(clicker, this.crate, KeyType.free_key, clicker.getLocation(), true, false, true, EventType.event_crate_opened);
                }
            }
        });

        return guiItem;
    }

    public @NotNull final Crate getCrate() {
        return this.crate;
    }

    public @NotNull final Prize getPrize() {
        return this.prize;
    }
}