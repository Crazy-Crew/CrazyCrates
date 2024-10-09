package com.badbones69.crazycrates.tasks.crates.types;

import com.badbones69.crazycrates.api.builders.types.features.CrateSpinMenu;
import com.badbones69.crazycrates.api.enums.misc.Files;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.api.PrizeManager;
import com.badbones69.crazycrates.api.objects.gui.GuiSettings;
import com.badbones69.crazycrates.managers.events.enums.EventType;
import com.ryderbelserion.vital.paper.util.scheduler.FoliaRunnable;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.api.builders.CrateBuilder;
import com.badbones69.crazycrates.utils.MiscUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.HashMap;

public class WheelCrate extends CrateBuilder {

    public WheelCrate(@NotNull final Crate crate, @NotNull final Player player, final int size) {
        super(crate, player, size);
    }

    private final Inventory inventory = getInventory();
    private final Player player = getPlayer();
    private final UUID uuid = this.player.getUniqueId();
    private final Crate crate = getCrate();

    private Map<Integer, ItemStack> rewards;

    @Override
    public void open(@NotNull final KeyType type, final boolean checkHand, final boolean isSilent, final EventType eventType) {
        // Crate event failed so we return.
        if (isCrateEventValid(type, checkHand, isSilent, eventType)) {
            return;
        }

        final String fileName = this.crate.getFileName();

        boolean keyCheck = this.userManager.takeKeys(this.uuid, fileName, type, this.crate.useRequiredKeys() ? this.crate.getRequiredKeys() : 1, checkHand);

        if (!keyCheck) {
            // Remove from opening list.
            this.crateManager.removePlayerFromOpeningList(this.player);

            return;
        }

        for (int index = 0; index < getSize(); index ++) {
            setCustomGlassPane(index);
        }

        this.rewards = new HashMap<>();

        for (int number : getBorder()) {
            final Prize prize = this.crate.pickPrize(this.player);

            setItem(number, prize.getDisplayItem(this.player, this.crate));

            this.rewards.put(number, prize.getDisplayItem(this.player, this.crate));
        }

        this.player.openInventory(this.inventory);

        final Material material = Material.LIME_STAINED_GLASS_PANE;

        addCrateTask(new FoliaRunnable(this.player.getScheduler(), null) {
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

                        if (crate.isCyclePrize() && !PrizeManager.isCapped(crate, player)) { // re-open this menu
                            new CrateSpinMenu(player, new GuiSettings(crate, prize, Files.respin_gui.getConfiguration())).open();

                            return;
                        } else {
                            userManager.removeRespinPrize(uuid, fileName);

                            // remove from the cache
                            userManager.removeRespinCrate(uuid, fileName, 0, false);
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
                    player.openInventory(inventory);

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
}