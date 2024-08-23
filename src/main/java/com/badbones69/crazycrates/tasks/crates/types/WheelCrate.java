package com.badbones69.crazycrates.tasks.crates.types;

import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.api.PrizeManager;
import com.ryderbelserion.vital.paper.util.scheduler.FoliaRunnable;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.api.builders.CrateBuilder;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.HashMap;

public class WheelCrate extends CrateBuilder {

    private @NotNull final CrateManager crateManager = this.plugin.getCrateManager();

    private @NotNull final BukkitUserManager userManager = this.plugin.getUserManager();

    public WheelCrate(@NotNull final Crate crate, @NotNull final Player player, final int size) {
        super(crate, player, size);
    }

    private Map<Integer, ItemStack> rewards;

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

        boolean keyCheck = this.userManager.takeKeys(uuid, fileName, type, crate.useRequiredKeys() ? crate.getRequiredKeys() : 1, checkHand);

        if (!keyCheck) {
            // Remove from opening list.
            this.crateManager.removePlayerFromOpeningList(player);

            return;
        }

        for (int index = 0; index < getSize(); index ++) {
            setCustomGlassPane(index);
        }

        this.rewards = new HashMap<>();

        for (int number : getBorder()) {
            final Prize prize = crate.pickPrize(player);

            setItem(number, prize.getDisplayItem(player));

            this.rewards.put(number, prize.getDisplayItem(player));
        }

        player.openInventory(getInventory());

        final Material material = Material.LIME_STAINED_GLASS_PANE;

        addCrateTask(new FoliaRunnable(player.getScheduler(), null) {
            final List<Integer> slots = getBorder();

            int uh = 0;
            int what = 17;

            int full = 0;

            final int timer = MiscUtils.randomNumber(42, 68);

            int slower = 0;
            int open = 0;
            int slow = 0;

            @Override
            public void run() {
                if (this.uh >= 18) this.uh = 0;

                if (this.what >= 18) this.what = 0;

                if (this.full < this.timer) {
                    populateMenu();
                }

                if (this.full >= this.timer) {
                    if (MiscUtils.slowSpin(46, 9).contains(this.slower)) {
                        populateMenu();
                    }

                    if (this.full >= this.timer + 47) {
                        this.slow++;

                        if (this.slow >= 2) {
                            for (int slot = 0; slot < getSize(); slot++) {
                                if (!getBorder().contains(slot)) setCustomGlassPane(slot);
                            }

                            this.slow = 0;
                        }
                    }

                    // Crate is done.
                    if (this.full >= (this.timer + 55 + 47)) {
                        Prize prize = null;

                        if (crateManager.isInOpeningList(player)) {
                            prize = crate.getPrize(rewards.get(this.slots.get(this.what)));
                        }

                        PrizeManager.givePrize(player, crate, prize);

                        playSound("stop-sound", Sound.Source.PLAYER, "entity.player.levelup");

                        player.closeInventory(InventoryCloseEvent.Reason.UNLOADED);

                        crateManager.removePlayerFromOpeningList(player);
                        crateManager.endCrate(player);

                        // Clear it because why not.
                        rewards.clear();
                    }

                    this.slower++;
                }

                this.full++;
                this.open++;

                if (this.open > 5) {
                    player.openInventory(getInventory());

                    this.open = 0;
                }
            }

            private void populateMenu() {
                @NotNull ItemStack itemStack = rewards.get(this.slots.get(this.uh));

                setItem(this.slots.get(this.uh), itemStack.withType(material));

                int otherSlot = this.slots.get(this.what);

                setItem(this.slots.get(this.what), rewards.get(otherSlot));

                playSound("cycle-sound", Sound.Source.PLAYER, "block.note_block.xylophone");

                this.uh++;
                this.what++;
            }
        }.runAtFixedRate(this.plugin, 1, 1));
    }

    private List<Integer> getBorder() {
        return Arrays.asList(10, 11, 12, 13, 14, 15, 16, 25, 34, 43, 42, 41, 40, 39, 38, 37, 28, 19);
    }

    @Override
    public void run() {

    }
}