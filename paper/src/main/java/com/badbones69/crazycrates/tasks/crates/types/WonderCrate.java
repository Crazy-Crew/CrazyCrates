package com.badbones69.crazycrates.tasks.crates.types;

import com.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.ItemBuilder;
import com.badbones69.crazycrates.api.objects.Prize;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.api.builders.CrateBuilder;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import java.util.ArrayList;
import java.util.List;

public class WonderCrate extends CrateBuilder {

    public WonderCrate(Crate crate, Player player, int size) {
        super(crate, player, size);
    }

    @Override
    public void open(KeyType type, boolean checkHand) {
        // Crate event failed so we return.
        if (isCrateEventValid(type, checkHand)) {
            return;
        }

        boolean keyCheck = this.plugin.getCrazyHandler().getUserManager().takeKeys(1, getPlayer().getUniqueId(), getCrate().getName(), type, checkHand);

        if (!keyCheck) {
            // Send the message about failing to take the key.
            MiscUtils.failedToTakeKey(getPlayer(), getCrate());

            // Remove from opening list.
            this.plugin.getCrateManager().removePlayerFromOpeningList(getPlayer());

            return;
        }

        final List<String> slots = new ArrayList<>();

        for (int index = 0; index < getSize(); index++) {
            Prize prize = getCrate().pickPrize(getPlayer());
            slots.add(String.valueOf(index));

            setItem(index, prize.getDisplayItem());
        }

        getPlayer().openInventory(getInventory());

        addCrateTask(new BukkitRunnable() {
            int time = 0;
            int full = 0;

            int slot1 = 0;
            int slot2 = 44;

            final List<Integer> other = new ArrayList<>();

            Prize prize = null;

            @Override
            public void run() {
                if (time >= 2 && full <= 65) {
                    slots.remove(slot1 + "");
                    slots.remove(slot2 + "");

                    other.add(slot1);
                    other.add(slot2);

                    ItemStack material = new ItemBuilder().setMaterial(Material.BLACK_STAINED_GLASS_PANE).setName(" ").build();

                    setItem(slot1, material);
                    setItem(slot2, material);

                    for (String slot : slots) {
                        prize = getCrate().pickPrize(getPlayer());
                        setItem(Integer.parseInt(slot), prize.getDisplayItem());
                    }

                    slot1++;
                    slot2--;
                }

                if (full > 67) {
                    for (int slot : other) {
                        setCustomGlassPane(slot);
                    }
                }

                getPlayer().openInventory(getInventory());

                if (full > 100) {
                    plugin.getCrateManager().endCrate(getPlayer());

                    getPlayer().closeInventory(InventoryCloseEvent.Reason.UNLOADED);

                    plugin.getCrazyHandler().getPrizeManager().givePrize(getPlayer(), prize, getCrate());

                    playSound("stop-sound", SoundCategory.PLAYERS, "ENTITY_PLAYER_LEVELUP");

                    if (prize.useFireworks()) MiscUtils.spawnFirework(getPlayer().getLocation().add(0, 1, 0), null);

                    plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(getPlayer(), getCrate(), getCrate().getName(), prize));
                    plugin.getCrateManager().removePlayerFromOpeningList(getPlayer());

                    return;
                }

                full++;
                time++;

                if (time > 2) time = 0;
            }
        }.runTaskTimer(plugin, 0, 2));
    }

    @Override
    public void run() {

    }
}