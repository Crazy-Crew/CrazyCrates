package com.badbones69.crazycrates.paper.cratetypes;

import us.crazycrew.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.CrazyManager;
import us.crazycrew.crazycrates.paper.api.interfaces.HologramController;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import us.crazycrew.crazycrates.paper.utils.MiscUtils;

import java.util.ArrayList;
import java.util.Random;

public class FireCracker {

    private static final CrazyCrates plugin = CrazyCrates.getPlugin(CrazyCrates.class);

    private static final CrazyManager crazyManager = plugin.getCrazyManager();
    
    public static void startFireCracker(final Player player, final Crate crate, KeyType keyType, final Location loc, HologramController hologramController) {
        if (!plugin.getCrazyHandler().getUserManager().takeKeys(1, player.getUniqueId(), crate.getName(), keyType, true)) {
            MiscUtils.failedToTakeKey(player, crate);
            crazyManager.removePlayerFromOpeningList(player);
            return;
        }

        if (hologramController != null) hologramController.removeHologram(loc.getBlock());

        final ArrayList<Color> colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.YELLOW);
        colors.add(Color.GREEN);
        colors.add(Color.BLUE);
        colors.add(Color.BLACK);
        colors.add(Color.AQUA);
        colors.add(Color.MAROON);
        colors.add(Color.PURPLE);

        crazyManager.addCrateTask(player, new BukkitRunnable() {
            final Random rng = new Random();
            final int color = rng.nextInt(colors.size());
            int l = 0;
            final Location location = loc.clone().add(.5, 25, .5);
            
            @Override
            public void run() {
                location.subtract(0, 1, 0);
                MiscUtils.spawnFirework(location, colors.get(color));
                l++;

                if (l == 25) {
                    crazyManager.endCrate(player);
                    // The key type is set to free because the key has already been taken above.
                    QuickCrate.openCrate(player, loc, crate, KeyType.free_key, hologramController);
                }
            }
        }.runTaskTimer(plugin, 0, 2));
    }
}