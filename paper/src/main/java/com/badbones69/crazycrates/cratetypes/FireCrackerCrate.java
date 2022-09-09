package com.badbones69.crazycrates.cratetypes;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.utilities.ScheduleUtils;
import com.badbones69.crazycrates.api.utilities.handlers.objects.crates.Crate;
import com.badbones69.crazycrates.api.utilities.handlers.tasks.CrateTaskHandler;
import com.badbones69.crazycrates.common.enums.crates.KeyType;
import com.google.common.collect.Lists;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class FireCrackerCrate {

    // Global Methods.
    public final CrazyCrates plugin = CrazyCrates.getPlugin();

    public final CrazyManager crazyManager = plugin.getStarter().getCrazyManager();

    public final ScheduleUtils scheduleUtils = plugin.getStarter().getScheduleUtils();

    public final Methods methods = plugin.getStarter().getMethods();

    public final CrateTaskHandler crateTaskHandler = plugin.getStarter().getCrateTaskHandler();

    // Class Internals.
    /**
     * Start the fire cracker crate.
     * @param player - Returns the opening player.
     * @param crate - Returns the crate the player is opening.
     * @param keyType - Returns the key type the player is using.
     * @param loc - Returns the location the player is at.
     */
    public void startFireCracker(Player player, Crate crate, KeyType keyType, Location loc) {

        if (!crazyManager.takeKeys(1, player, crate, keyType, true)) {
            methods.failedToTakeKey(player, crate);
            crazyManager.removePlayerFromOpeningList(player);
            return;
        }

        final List<Color> colors = Lists.newArrayList(Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE, Color.BLACK, Color.AQUA, Color.MAROON, Color.PURPLE);

        crateTaskHandler.addTask(player, scheduleUtils.timer(2L, 0L, () -> {
            final Random random = new Random();

            final int color = random.nextInt(colors.size());

            AtomicInteger count = new AtomicInteger();

            final Location location = loc.clone().add(.5, 25, .5);

            location.subtract(0, 1, 0);

            methods.firework(location, Collections.singletonList(colors.get(color)));

            if (count.incrementAndGet() == 25) {
                crateTaskHandler.endCrate(player);
                // The key type is set to free because the key has already been taken above.
                // quickCrate.openCrate(player, loc, crate, KeyType.FREE_KEY);
            }
        }));
    }
}