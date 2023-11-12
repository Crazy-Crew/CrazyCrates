package us.crazycrew.crazycrates.paper.managers.crates.types;

import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.ItemBuilder;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import us.crazycrew.crazycrates.paper.api.builders.CrateBuilder;
import us.crazycrew.crazycrates.paper.other.MiscUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CsgoCrate extends CrateBuilder {

    public CsgoCrate(Crate crate, Player player, int size) {
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

        // Set the glass/display items to the inventory.
        populate();

        // Open the inventory.
        getPlayer().openInventory(getInventory());

        addCrateTask(new BukkitRunnable() {
            int time = 1;

            int full = 0;

            int open = 0;

            @Override
            public void run() {
                if (full <= 50) { // When Spinning
                    moveItemsAndSetGlass();
                    getPlayer().playSound(getPlayer().getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                }

                open++;

                if (open >= 5) {
                    getPlayer().openInventory(getInventory());
                    open = 0;
                }

                full++;

                if (full > 51) {
                    if (calculateSpinDelays().contains(time)) { // When Slowing Down
                        moveItemsAndSetGlass();

                        getPlayer().playSound(getPlayer().getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                    }

                    time++;

                    if (time == 60) { // When done
                        getPlayer().playSound(getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                        plugin.getCrateManager().endCrate(getPlayer());
                        Prize prize = getCrate().getPrize(getInventory().getItem(13));

                        plugin.getCrazyHandler().getPrizeManager().checkPrize(prize, getPlayer(), getCrate());

                        plugin.getCrateManager().removePlayerFromOpeningList(getPlayer());

                        cancel();

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (getPlayer().getOpenInventory().getTopInventory().equals(getInventory())) getPlayer().closeInventory();
                            }
                        }.runTaskLater(plugin, 40);
                    } else if (time > 60) { // Added this due reports of the prizes spamming when low tps.
                        cancel();
                    }
                }
            }
        }.runTaskTimer(this.plugin, 1, 1));
    }

    private void populate() {
        HashMap<Integer, ItemStack> glass = new HashMap<>();

        for (int index = 0; index < 10; index++) {
            if (index < 9 && index != 3) glass.put(index, getInventory().getItem(index));
        }

        for (int index : glass.keySet()) {
            if (getInventory().getItem(index) == null) {
                setCustomGlassPane(index);
                setCustomGlassPane(index + 18);
            }
        }

        for (int index = 1; index < 10; index++) {
            if (index < 9 && index != 4) glass.put(index, getInventory().getItem(index));
        }

        setItem(glass.get(1), 0);

        setItem(glass.get(2), 1);
        setItem(glass.get(2), 1 + 18);

        setItem(glass.get(3), 2);
        setItem(glass.get(3), 2 + 18);

        setItem(glass.get(5), 3);
        setItem(glass.get(5), 3 + 18);

        ItemStack itemStack = new ItemBuilder().setMaterial(Material.BLACK_STAINED_GLASS_PANE).setName(" ").build();
        setItem(itemStack, 4);
        setItem(itemStack, 4 + 18);

        setItem(glass.get(6), 5);
        setItem(glass.get(6), 5 + 18);

        setItem(glass.get(7), 6);
        setItem(glass.get(7), 6 + 18);

        setItem(glass.get(8), 7);
        setItem(glass.get(8), 7 + 18);

        setCustomGlassPane(8);
        setCustomGlassPane(8 + 18);

        // Set display items.
        for (int index = 9; index > 8 && index < 18; index++) {
            setItem(getCrate().pickPrize(getPlayer()).getDisplayItem(), index);
        }
    }

    private List<Integer> calculateSpinDelays() {
        List<Integer> spinDelays = new ArrayList<>();
        int totalSpins = 120;
        int spinIncrement = 15;

        for (int currentSpin = totalSpins; spinIncrement > 0; currentSpin--) {
            spinDelays.add(currentSpin);
            currentSpin -= spinIncrement;
            spinIncrement--;
        }

        return spinDelays;
    }

    private void moveItemsAndSetGlass() {
        List<ItemStack> items = new ArrayList<>();

        for (int i = 10; i < 18; i++) {
            items.add(getInventory().getItem(i));
        }

        setItem(getCrate().pickPrize(getPlayer()).getDisplayItem(), 9);

        for (int i = 0; i < 8; i++) {
            setItem(items.get(i), i + 10);
        }

        populate();
    }
}