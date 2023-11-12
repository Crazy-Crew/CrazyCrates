package us.crazycrew.crazycrates.paper.managers.types;

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

    public CsgoCrate(Crate crate, Player player, int size, String title) {
        super(crate, player, size, title);
    }

    @Override
    public void open(KeyType type, boolean checkHand) {
        // Set the glass/display items to the inventory.
        populate();

        // Open the inventory.
        getPlayer().openInventory(getInventory());

        boolean keyCheck = this.plugin.getCrazyHandler().getUserManager().takeKeys(1, getPlayer().getUniqueId(), getCrate().getName(), type, checkHand);

        if (!keyCheck) {
            // Send the message about failing to take the key.
            MiscUtils.failedToTakeKey(getPlayer(), getCrate());

            // Remove from opening list.
            this.plugin.getCrateManager().removePlayerFromOpeningList(getPlayer());

            return;
        }

        addCrateTask(getPlayer(), new BukkitRunnable() {
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

        setGlassPane(glass.get(1), 0);

        setGlassPane(glass.get(2), 1);
        setGlassPane(glass.get(2), 1 + 18);

        setGlassPane(glass.get(3), 2);
        setGlassPane(glass.get(3), 2 + 18);

        setGlassPane(glass.get(5), 3);
        setGlassPane(glass.get(5), 3 + 18);

        ItemStack itemStack = new ItemBuilder().setMaterial(Material.BLACK_STAINED_GLASS_PANE).setName(" ").build();
        setGlassPane(itemStack, 4);
        setGlassPane(itemStack, 4 + 18);

        setGlassPane(glass.get(6), 5);
        setGlassPane(glass.get(6), 5 + 18);

        setGlassPane(glass.get(7), 6);
        setGlassPane(glass.get(7), 6 + 18);

        setGlassPane(glass.get(8), 7);
        setGlassPane(glass.get(8), 7 + 18);

        setCustomGlassPane(8);
        setCustomGlassPane(8 + 18);

        // Set display items.
        for (int index = 9; index > 8 && index < 18; index++) {
            getInventory().setItem(index, getCrate().pickPrize(getPlayer()).getDisplayItem());
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

        getInventory().setItem(9, getCrate().pickPrize(getPlayer()).getDisplayItem());

        for (int i = 0; i < 8; i++) {
            getInventory().setItem(i + 10, items.get(i));
        }

        populate();
    }
}