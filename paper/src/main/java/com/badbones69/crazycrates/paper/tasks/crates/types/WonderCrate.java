package com.badbones69.crazycrates.paper.tasks.crates.types;

import com.badbones69.crazycrates.paper.api.builders.types.features.CrateSpinMenu;
import com.badbones69.crazycrates.paper.api.enums.other.keys.FileKeys;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import com.badbones69.crazycrates.paper.api.PrizeManager;
import com.badbones69.crazycrates.paper.api.builders.LegacyItemBuilder;
import com.badbones69.crazycrates.paper.api.objects.gui.GuiSettings;
import com.badbones69.crazycrates.paper.managers.events.enums.EventType;
import com.badbones69.crazycrates.paper.managers.BukkitUserManager;
import com.badbones69.crazycrates.paper.tasks.crates.CrateManager;
import com.ryderbelserion.fusion.paper.api.scheduler.FoliaScheduler;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.paper.api.builders.CrateBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WonderCrate extends CrateBuilder {

    private final CrateManager crateManager = this.plugin.getCrateManager();

    private final BukkitUserManager userManager = this.plugin.getUserManager();

    public WonderCrate(@NotNull final Crate crate, @NotNull final Player player, final int size) {
        super(crate, player, size);
    }

    private final Inventory inventory = getInventory();
    private final Player player = getPlayer();
    private final UUID uuid = this.player.getUniqueId();
    private final Crate crate = getCrate();

    @Override
    public void open(@NotNull final KeyType type, final boolean checkHand, final boolean isSilent, final EventType eventType) {
        // Crate event failed so we return.
        if (isCrateEventValid(type, checkHand, isSilent, eventType)) {
            return;
        }

        final String fileName = this.crate.getFileName();

        final boolean keyCheck = this.userManager.takeKeys(this.uuid, fileName, type, this.crate.useRequiredKeys() ? this.crate.getRequiredKeys() : 1, checkHand);

        if (!keyCheck) {
            // Remove from opening list.
            this.crateManager.removePlayerFromOpeningList(getPlayer());

            return;
        }

        final List<String> slots = new ArrayList<>();

        for (int index = 0; index < getSize(); index++) {
            final Prize prize = this.crate.pickPrize(this.player);

            slots.add(String.valueOf(index));

            setItem(index, prize.getDisplayItem(this.player, this.crate));
        }

        this.player.openInventory(this.inventory);

        addCrateTask(new FoliaScheduler(null, this.player) {
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

                    final ItemStack material = new LegacyItemBuilder(crate.isGlassBorderToggled() ? ItemType.BLACK_STAINED_GLASS_PANE : ItemType.AIR).setDisplayName(" ").asItemStack();

                    setItem(this.slot1, material);
                    setItem(this.slot2, material);

                    for (String slot : slots) {
                        this.prize = crate.pickPrize(player);

                        setItem(Integer.parseInt(slot), this.prize.getDisplayItem(player, crate));
                    }

                    playSound("cycle-sound", Sound.Source.MASTER, "block.note_block.xylophone");

                    this.slot1++;
                    this.slot2--;
                }

                if (this.full > 67) {
                    if (crate.isGlassBorderToggled()) {
                        for (int slot : this.other) {
                            setCustomGlassPane(slot);
                        }
                    }

                    playSound("cycle-sound", Sound.Source.MASTER, "block.note_block.xylophone");
                }

                player.openInventory(getInventory());

                if (this.full > 100) {
                    crateManager.endCrate(player);

                    player.closeInventory(InventoryCloseEvent.Reason.UNLOADED);

                    if (crate.isCyclePrize() && !PrizeManager.isCapped(crate, player)) { // re-open this menu
                        new CrateSpinMenu(player, new GuiSettings(crate, prize, FileKeys.respin_gui.getConfiguration())).open();

                        crateManager.removePlayerFromOpeningList(player);

                        return;
                    } else {
                        userManager.removeRespinPrize(uuid, fileName);

                        if (!crate.isCyclePersistRestart()) {
                            userManager.removeRespinCrate(uuid, fileName, userManager.getCrateRespin(uuid, fileName));
                        }
                    }

                    PrizeManager.givePrize(player, crate, this.prize);

                    playSound("stop-sound", Sound.Source.MASTER, "entity.player.levelup");

                    crateManager.removePlayerFromOpeningList(player);

                    return;
                }

                this.full++;
                this.time++;

                if (this.time > 2) this.time = 0;
            }
        }.runAtFixedRate(0, 2));
    }
}