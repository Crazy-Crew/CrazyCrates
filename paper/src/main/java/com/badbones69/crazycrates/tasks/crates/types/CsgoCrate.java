package com.badbones69.crazycrates.tasks.crates.types;

import com.badbones69.crazycrates.api.builders.v2.CrateBuilder;
import com.badbones69.crazycrates.api.enums.crates.CrateStatus;
import com.badbones69.crazycrates.api.events.crates.CrateStatusEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.Gui;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.GuiItem;
import com.ryderbelserion.vital.paper.util.scheduler.FoliaRunnable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsgoCrate extends CrateBuilder {

    public CsgoCrate(final Player player, final Crate crate) {
        super(player, crate, 3);

        setSeconds(6000);
    }

    private final CrateStatusEvent event = getEvent();
    private final Player player = getPlayer();
    private final Crate crate = getCrate();
    private final Gui gui = getGui();

    private final long startTime = System.currentTimeMillis(); // 5.4 seconds

    private int inventoryStatus = 0;
    private int delayCounter = 0;

    @Override
    public void run() {
        final long elapsedTime = System.currentTimeMillis() - this.startTime;

        if (elapsedTime >= getSeconds()) { // elapsed the allowed time.
            this.event.setStatus(CrateStatus.ended).callEvent();

            cancel();

            return;
        }

        if (elapsedTime <= 5000) {
            this.event.setStatus(CrateStatus.cycling).callEvent();

            moveItemsAndSetGlass();
        }

        this.inventoryStatus++;

        if (this.inventoryStatus >= 5) {
            this.player.openInventory(this.gui.getInventory());

            this.inventoryStatus = 0;
        }

        if (elapsedTime > 5100) { // slowing down
            this.delayCounter = this.delayCounter + 10;

            this.event.setStatus(CrateStatus.silent).callEvent();

            new FoliaRunnable(this.player.getScheduler(), null) {
                @Override
                public void run() {
                    moveItemsAndSetGlass();

                    event.setStatus(CrateStatus.cycling).callEvent();

                    cancel();
                }
            }.runAtFixedRate(this.plugin, this.delayCounter, 1);
        }

        /*
        this.full++;

        if (this.full > 51) { // slowing down
            if (MiscUtils.slowSpin(120, 15).contains(this.time)) { // cycling
                moveItemsAndSetGlass();

                this.event.setStatus(CrateStatus.cycling).callEvent();
            }

            this.time++;

            if (this.time >= 60) { // finished
                final ItemStack itemStack = new ItemStack(Material.GRAY_STAINED_GLASS);

                setItem(4, itemStack);
                setItem(22, itemStack);

                final ItemStack item = getInventory().getItem(13);

                Prize prize = null;

                if (item != null) {
                    prize = crate.getPrize(item);
                }

                final PlayerPrizeEvent playerPrizeEvent = new PlayerPrizeEvent(player, this.event, crate, prize);

                playerPrizeEvent.callEvent();

                if (playerPrizeEvent.isCancelled()) {
                    cancel();

                    return;
                }

                cancel();
            }
        }*/
    }

    @Override
    public void open(@NotNull final KeyType type, final boolean inspectInventory) {
        getBorder().forEach(this::setRandomGlass);

        for (int index = 9; index > 8 && index < 18; index++) {
            this.gui.setItem(index, new GuiItem(this.crate.pickPrize(this.player).getDisplayItem(this.player, this.crate)));
        }

        this.gui.open(this.player);

        runAtFixedRate(this.plugin, 0, 1);
    }

    private void moveItemsAndSetGlass() {
        final List<ItemStack> items = new ArrayList<>();

        final Inventory inventory = this.gui.getInventory();

        for (int count = 9; count > 8 && count < 17; count++) {
            items.add(inventory.getItem(count));
        }

        this.gui.setItem(9, new GuiItem(this.crate.pickPrize(this.player).getDisplayItem(this.player, this.crate)));

        for (int count = 0; count < 8; count++) {
            this.gui.setItem(count, new GuiItem(items.get(count)));
        }

        getBorder().forEach(this::setRandomGlass);
    }

    private List<Integer> getBorder() {
        return Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 18, 19, 20, 21, 22, 23, 24, 25, 26);
    }
}