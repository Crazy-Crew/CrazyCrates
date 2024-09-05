package com.badbones69.crazycrates.tasks.crates.types.v2;

import com.badbones69.crazycrates.api.enums.crates.CrateStatus;
import com.badbones69.crazycrates.api.events.crates.CrateStatusEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.PrizeManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.api.builders.CrateBuilder;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CsgoCrate extends CrateBuilder {

    private final CrateStatusEvent event;

    public CsgoCrate(@NotNull final Crate crate, @NotNull final Player player, final int size) {
        super(crate, player, size);

        this.event = new CrateStatusEvent(crate, player);
    }

    private int time = 1;
    private int full = 0;
    private int open = 0;

    @Override
    public void run() {
        final Player player = getPlayer();
        final Crate crate = getCrate();

        if (this.full <= 50) { // cycling
            moveItemsAndSetGlass();

            this.event.setStatus(CrateStatus.cycling).callEvent();
        }

        this.open++;

        if (this.open >= 5) { // opens the inventory
            player.openInventory(getInventory());

            this.open = 0;
        }

        this.full++;

        if (this.full > 51) { // slowing down
            if (MiscUtils.slowSpin(120, 15).contains(this.time)) { // cycling
                moveItemsAndSetGlass();

                this.event.setStatus(CrateStatus.cycling).callEvent();
            }

            this.time++;

            if (this.time >= 60) { // finished
                this.event.setStatus(CrateStatus.ended).callEvent();

                final ItemStack itemStack = new ItemStack(Material.GRAY_STAINED_GLASS);

                setItem(4, itemStack);
                setItem(22, itemStack);

                final ItemStack item = getInventory().getItem(13);

                if (item != null) PrizeManager.givePrize(player, crate, crate.getPrize(item));

                cancel();
            }
        }
    }

    @Override
    public void open(@NotNull final KeyType type, final boolean checkHand) {
        final Player player = getPlayer();
        final Crate crate = getCrate();

        final UUID uuid = player.getUniqueId();
        final String fileName = crate.getFileName();

        final boolean keyCheck = this.userManager.takeKeys(uuid, fileName, type, crate.useRequiredKeys() ? crate.getRequiredKeys() : 1, checkHand);

        if (!keyCheck) {
            this.crateManager.removePlayerFromOpeningList(player);

            return;
        }

        getBorder().forEach(this::setCustomGlassPane);

        // Set display items.
        for (int index = 9; index > 8 && index < 18; index++) {
            setItem(index, getCrate().pickPrize(getPlayer()).getDisplayItem(getPlayer()));
        }

        // Open the inventory.
        player.openInventory(getInventory());

        // run the task, which uses the run method above.
        runAtFixedRate(this.plugin, 0, 1);
    }

    private void moveItemsAndSetGlass() {
        final List<ItemStack> items = new ArrayList<>();

        final Player player = getPlayer();
        final Crate crate = getCrate();

        for (int i = 9; i > 8 && i < 17; i++) {
            items.add(getInventory().getItem(i));
        }

        setItem(9, crate.pickPrize(player).getDisplayItem(player));

        for (int i = 0; i < 8; i++) {
            setItem(i + 10, items.get(i));
        }

        getBorder().forEach(this::setCustomGlassPane);
    }

    private List<Integer> getBorder() {
        return Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 18, 19, 20, 21, 22, 23, 24, 25, 26);
    }
}