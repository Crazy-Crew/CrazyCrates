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

    private final Random random = new Random();

    public void openCrate(Player player, Crate crate, KeyType keyType, boolean checkHand) {
        Location originalLocation = CrateControlListener.inUse.get(player);
        HologramController hologramController = crazyManager.getHologramController();
        if (!crazyManager.takeKeys(1, player, crate, keyType, true)) {
            Methods.failedToTakeKey(player, crate);
            crazyManager.removePlayerFromOpeningList(player);
            return;
        }

        if (hologramController != null) hologramController.removeHologram(originalLocation.getBlock());

        crazyManager.addCrateTask(player, new BukkitRunnable() {
            final Color color = getRandomColor();
            int level = 0;
            final Location fireworkLocation = originalLocation.clone().add(.5, 25, .5);

            @Override
            public void run() {
                fireworkLocation.subtract(0, 1, 0);
                Methods.firework(fireworkLocation, color);
                level++;

                if (level == 25) {
                    crazyManager.endCrate(player);
                    // The key type is set to free because the key has already been taken above.
                    new QuickCrate().openCrate(player, crate, KeyType.FREE_KEY, checkHand);
                }
            }
        }.runTaskTimer(plugin, 0, 2));
    }

    private Color getRandomColor() {
        return colors.get(random.nextInt(colors.size()));
    }

}