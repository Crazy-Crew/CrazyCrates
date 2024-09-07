package com.badbones69.crazycrates.tasks.crates.types.v2;

import com.badbones69.crazycrates.api.enums.crates.CrateStatus;
import com.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.api.events.crates.CrateStatusEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.api.builders.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.api.builders.CrateBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WonderCrate extends CrateBuilder {

    private final CrateStatusEvent event;

    public WonderCrate(@NotNull final Crate crate, @NotNull final Player player, final int size) {
        super(crate, player, size);

        this.event = new CrateStatusEvent(crate, player);
    }

    private final List<Integer> slots = new ArrayList<>();

    private int time = 0;
    private int full = 0;

    private int slot1 = 0;
    private int slot2 = 44;

    @Override
    public void run() {
        final Player player = getPlayer();
        final Crate crate = getCrate();

        final List<Integer> other = new ArrayList<>();

        Prize prize = null;

        if (this.time >= 2 && this.full <= 65) {
            this.slots.remove(this.slot1);
            this.slots.remove(this.slot2);

            other.add(this.slot1);
            other.add(this.slot2);

            final ItemStack material = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName(" ").getStack();

            setItem(this.slot1, material);
            setItem(this.slot2, material);

            for (int slot : this.slots) {
                prize = crate.pickPrize(player);

                setItem(slot, prize.getDisplayItem(player));
            }

            this.event.setStatus(CrateStatus.cycling).callEvent();

            this.slot1++;
            this.slot2--;
        }

        if (this.full > 67) {
            for (int slot : other) {
                setCustomGlassPane(slot);
            }

            this.event.setStatus(CrateStatus.cycling).callEvent();
        }

        player.openInventory(getInventory());

        if (this.full > 100) {
            final PlayerPrizeEvent playerPrizeEvent = new PlayerPrizeEvent(player, this.event, crate, prize);

            playerPrizeEvent.callEvent();

            if (playerPrizeEvent.isCancelled()) {
                cancel();

                return;
            }

            cancel();

            return;
        }

        this.full++;
        this.time++;

        if (this.time > 2) this.time = 0;
    }

    @Override
    public void open(@NotNull final KeyType type, final boolean checkHand) {
        // Crate event failed so we return.
        if (isCrateEventValid(type, checkHand)) {
            return;
        }

        final Player player = getPlayer();
        final UUID uuid = player.getUniqueId();
        final Crate crate = getCrate();
        final String fileName = crate.getFileName();

        final boolean keyCheck = this.userManager.takeKeys(uuid, fileName, type, crate.useRequiredKeys() ? crate.getRequiredKeys() : 1, checkHand);

        if (!keyCheck) {
            this.crateManager.removePlayerFromOpeningList(getPlayer());

            return;
        }

        for (int index = 0; index < getSize(); index++) {
            this.slots.add(index);

            setItem(index, crate.pickPrize(player).getDisplayItem(player));
        }

        player.openInventory(getInventory());

        // run the task, which uses the run method above.
        runAtFixedRate(this.plugin, 0, 2);
    }
}