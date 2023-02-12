package com.badbones69.crazycrates.cratetypes;

import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.managers.CrateManager;
import com.badbones69.crazycrates.enums.types.KeyType;
import com.badbones69.crazycrates.api.interfaces.HologramController;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.listeners.CrateControlListener;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class FireCracker implements CrateManager {

    private final List<Color> colors = Arrays.asList(Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE, Color.BLACK, Color.AQUA, Color.MAROON, Color.PURPLE);

    public void openCrate(Player player, Crate crate, KeyType keyType, boolean checkHand) {
        Location loc = CrateControlListener.inUse.get(player);
        HologramController hologramController = crazyManager.getHologramController();
        if (!crazyManager.takeKeys(1, player, crate, keyType, true)) {
            Methods.failedToTakeKey(player, crate);
            crazyManager.removePlayerFromOpeningList(player);
            return;
        }

        if (hologramController != null) hologramController.removeHologram(loc.getBlock());

        crazyManager.addCrateTask(player, new BukkitRunnable() {
            final Random r = new Random();
            final int color = r.nextInt(colors.size());
            int l = 0;
            final Location L = loc.clone().add(.5, 25, .5);

            @Override
            public void run() {
                L.subtract(0, 1, 0);
                Methods.firework(L, colors.get(color));
                l++;

                if (l == 25) {
                    crazyManager.endCrate(player);
                    // The key type is set to free because the key has already been taken above.
                    new QuickCrate().openCrate(player, crate, KeyType.FREE_KEY, checkHand);
                }
            }
        }.runTaskTimer(plugin, 0, 2));
    }

}