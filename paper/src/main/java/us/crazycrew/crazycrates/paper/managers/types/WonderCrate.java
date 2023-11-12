package us.crazycrew.crazycrates.paper.managers.types;

import com.badbones69.crazycrates.paper.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.ItemBuilder;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import us.crazycrew.crazycrates.paper.api.builders.CrateBuilder;
import us.crazycrew.crazycrates.paper.other.MiscUtils;
import java.util.ArrayList;
import java.util.List;

public class WonderCrate extends CrateBuilder {


    public WonderCrate(Crate crate, Player player, int size, String title) {
        super(crate, player, size, title);
    }

    @Override
    public void open(KeyType type, boolean checkHand) {
        // Crate event failed so we return.
        if (isCrateEventValid(getPlayer(), getCrate(), type, checkHand)) {
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

        List<String> slots = new ArrayList<>();

        for (int index = 0; index < getSize(); index++) {
            Prize prize = getCrate().pickPrize(getPlayer());
            slots.add(String.valueOf(index));

            getInventory().setItem(index, prize.getDisplayItem());
        }

        getPlayer().openInventory(getInventory());

        addCrateTask(getPlayer(), new BukkitRunnable() {
            int time = 0;
            int full = 0;

            int slotOne = 0;
            int slotTwo = 44;

            final List<Integer> otherSlots = new ArrayList<>();

            Prize prize = null;

            @Override
            public void run() {
                if (time >= 2 && full <= 65) {
                    slots.remove(String.valueOf(slotOne));
                    slots.remove(String.valueOf(slotTwo));

                    otherSlots.add(slotOne);
                    otherSlots.add(slotTwo);

                    ItemStack material = new ItemBuilder().setMaterial(Material.BLACK_STAINED_GLASS_PANE).setName(" ").build();

                    setItem(material, slotOne);
                    setItem(material, slotTwo);

                    slots.forEach(slot -> {
                        prize = getCrate().pickPrize(getPlayer());
                        setItem(prize.getDisplayItem(), Integer.parseInt(slot));
                    });

                    slotOne++;
                    slotTwo--;
                }

                if (full > 67) {
                    otherSlots.forEach(slot -> setCustomGlassPane(slot));
                }

                getPlayer().openInventory(getInventory());

                if (full > 100) {
                    plugin.getCrateManager().endCrate(getPlayer());

                    getPlayer().closeInventory(InventoryCloseEvent.Reason.UNLOADED);

                    plugin.getCrazyHandler().getPrizeManager().givePrize(getPlayer(), prize, getCrate());

                    if (prize.useFireworks()) MiscUtils.spawnFirework(getPlayer().getLocation().add(0, 1, 0), null);

                    plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(getPlayer(), getCrate(), getCrate().getName(), prize));
                    plugin.getCrateManager().removePlayerFromOpeningList(getPlayer());

                    return;
                }

                full++;
                time++;

                if (time < 2) time = 0;
            }
        }.runTaskTimer(plugin, 0, 2));
    }
}