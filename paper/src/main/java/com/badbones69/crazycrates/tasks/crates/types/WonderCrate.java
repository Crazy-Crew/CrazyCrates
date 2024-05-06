package com.badbones69.crazycrates.tasks.crates.types;

import com.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.api.PrizeManager;
import com.ryderbelserion.vital.items.ItemBuilder;
import com.ryderbelserion.vital.util.scheduler.FoliaRunnable;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.api.builders.CrateBuilder;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WonderCrate extends CrateBuilder {

    private @NotNull final CrateManager crateManager = this.plugin.getCrateManager();

    private @NotNull final BukkitUserManager userManager = this.plugin.getUserManager();

    public WonderCrate(@NotNull final Crate crate, @NotNull final Player player, final int size) {
        super(crate, player, size);
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
        final String crateName = crate.getName();

        final boolean keyCheck = this.userManager.takeKeys(uuid, crateName, type, 1, checkHand);

        if (!keyCheck) {
            // Remove from opening list.
            this.crateManager.removePlayerFromOpeningList(getPlayer());

            return;
        }

        final List<String> slots = new ArrayList<>();

        for (int index = 0; index < getSize(); index++) {
            final Prize prize = crate.pickPrize(player);

            slots.add(String.valueOf(index));

            setItem(index, prize.getDisplayItem(player));
        }

        player.openInventory(getInventory());

        addCrateTask(new FoliaRunnable(player.getScheduler(), null) {
            int time = 0;
            int full = 0;

            int slot1 = 0;
            int slot2 = 44;

            final List<Integer> other = new ArrayList<>();

            Prize prize = null;

            @Override
            public void run() {
                if (this.time >= 2 && this.full <= 65) {
                    slots.remove(this.slot1 + "");
                    slots.remove(this.slot2 + "");

                    other.add(this.slot1);
                    other.add(this.slot2);

                    final ItemStack material = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName(" ").build();

                    setItem(this.slot1, material);
                    setItem(this.slot2, material);

                    for (String slot : slots) {
                        this.prize = crate.pickPrize(player);

                        setItem(Integer.parseInt(slot), this.prize.getDisplayItem(player));
                    }

                    this.slot1++;
                    this.slot2--;
                }

                if (this.full > 67) {
                    for (int slot : this.other) {
                        setCustomGlassPane(slot);
                    }
                }

                player.openInventory(getInventory());

                if (this.full > 100) {
                    crateManager.endCrate(player);

                    getPlayer().closeInventory(InventoryCloseEvent.Reason.UNLOADED);

                    PrizeManager.givePrize(player, this.prize, crate);

                    playSound("stop-sound", SoundCategory.PLAYERS, "entity.player.levelup");

                    if (this.prize.useFireworks()) MiscUtils.spawnFirework(getPlayer().getLocation().add(0, 1, 0), null);

                    plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crateName, this.prize));

                    crateManager.removePlayerFromOpeningList(player);

                    return;
                }

                this.full++;
                this.time++;

                if (this.time > 2) this.time = 0;
            }
        }.runAtFixedRate(this.plugin, 0, 2));
    }

    @Override
    public void run() {

    }
}