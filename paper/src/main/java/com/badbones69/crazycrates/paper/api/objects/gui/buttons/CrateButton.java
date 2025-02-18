package com.badbones69.crazycrates.paper.api.objects.gui.buttons;

import com.badbones69.crazycrates.paper.api.PrizeManager;
import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import com.badbones69.crazycrates.paper.managers.BukkitUserManager;
import com.badbones69.crazycrates.paper.managers.events.enums.EventType;
import com.badbones69.crazycrates.paper.tasks.crates.CrateManager;
import com.ryderbelserion.fusion.paper.builder.gui.interfaces.GuiAction;
import com.ryderbelserion.fusion.paper.builder.gui.interfaces.GuiItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import java.util.HashMap;
import java.util.UUID;

public class CrateButton extends GuiButton {

    private final BukkitUserManager userManager = this.plugin.getUserManager();
    private final CrateManager crateManager = this.plugin.getCrateManager();

    private final Crate crate;
    private final Prize prize;

    public CrateButton(final Crate crate, final Prize prize, final ConfigurationSection section) {
        super(section, new HashMap<>() {{
            put("%crate_pretty%", crate.getCrateName());
            put("%crate_raw%", crate.getFileName());
            put("%prize%", prize.getSectionName());
        }});

        this.crate = crate;
        this.prize = prize;
    }

    @Override
    public final @NotNull GuiItem getGuiItem() {
        final GuiItem guiItem = super.getGuiItem();

        final GuiAction<InventoryClickEvent> action = guiItem.getAction();

        guiItem.setAction(event -> {
            if (action != null) {
                action.execute(event);
            }

            final Player player = (Player) event.getWhoClicked();
            final UUID uuid = player.getUniqueId();
            final String crateName = this.crate.getFileName();

            switch (getSection().getName()) {
                case "accept" -> {
                    final Prize prize = this.crate.getPrize(this.userManager.getRespinPrize(uuid, crateName));

                    PrizeManager.givePrize(player, this.crate, prize);

                    if (!this.crate.isCyclePersistRestart()) {
                        this.userManager.removeRespinCrate(uuid, crateName, this.userManager.getCrateRespin(uuid, crateName));
                    }

                    this.userManager.removeRespinPrize(uuid, crateName);

                    this.crateManager.removePlayerFromOpeningList(player);
                    this.crateManager.removeCrateInUse(player);
                    this.crateManager.removeCrateTask(player);
                    this.crateManager.endCrate(player);
                }

                case "deny" -> {
                    if (PrizeManager.isCapped(this.crate, player)) {
                        final Prize prize = this.crate.getPrize(this.userManager.getRespinPrize(uuid, crateName));

                        PrizeManager.givePrize(player, this.crate, prize);

                        this.userManager.removeRespinPrize(uuid, crateName); // remove just in case

                        // remove from the cache
                        if (!this.crate.isCyclePersistRestart()) {
                            this.userManager.removeRespinCrate(uuid, crateName, this.userManager.getCrateRespin(uuid, crateName));
                        }

                        final int cap = PrizeManager.getCap(crate, player);

                        Messages.crate_prize_max_respins.sendMessage(player, new HashMap<>() {{
                            put("{status}", cap >= 1 ? Messages.crate_prize_max_respins_left.getMessage(player, new HashMap<>() {{
                                put("{respins_total}", String.valueOf(cap));
                                put("{respins_left}", "0");
                            }}) : Messages.crate_prize_max_respins_none.getMessage(player));
                        }});

                        return;
                    }

                    this.userManager.addRespinCrate(uuid, crateName, 1);

                    this.crateManager.openCrate(player, this.crate, KeyType.free_key, player.getLocation(), true, false, true, EventType.event_crate_opened);
                }
            }
        });

        return guiItem;
    }

    public final Crate getCrate() {
        return this.crate;
    }

    public final Prize getPrize() {
        return this.prize;
    }
}