package com.badbones69.crazycrates.cratetypes;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.enums.KeyType;
import com.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class FireCracker {

    private final CrazyCrates plugin;
    private final CrazyManager crazyManager;

    private final Methods methods;

    private final QuickCrate quickCrate;

    public FireCracker(CrazyCrates plugin, CrazyManager crazyManager, Methods methods, QuickCrate quickCrate) {
        this.plugin = plugin;
        this.crazyManager = crazyManager;

        this.methods = methods;

        this.quickCrate = quickCrate;
    }
    
    public void startFireCracker(final Player player, final Crate crate, KeyType keyType, final Location loc) {

        if (!crazyManager.takeKeys(1, player, crate, keyType, true)) {
            methods.failedToTakeKey(player, crate);
            crazyManager.removePlayerFromOpeningList(player);
            return;
        }

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
            final Random random = new Random();

            final int color = random.nextInt(colors.size());

            int count = 0;

            final Location location = loc.clone().add(.5, 25, .5);
            
            @Override
            public void run() {
                location.subtract(0, 1, 0);
                methods.firework(location, Collections.singletonList(colors.get(color)));

                count++;

                if (count == 25) {
                    crazyManager.endCrate(player);
                    // The key type is set to free because the key has already been taken above.
                    quickCrate.openCrate(player, loc, crate, KeyType.FREE_KEY);
                }
            }
        }.runTaskTimer(plugin, 0, 2));
    }
}