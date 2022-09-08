package com.badbones69.crazycrates.cratetypes;

import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.common.enums.crates.KeyType;
import com.badbones69.crazycrates.api.utilities.handlers.objects.crates.Crate;
import com.badbones69.crazycrates.api.utilities.ScheduleUtils;
import com.badbones69.crazycrates.api.utilities.handlers.tasks.CrateTaskHandler;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class FireCrackerCrate {

    @Inject private CrazyManager crazyManager;

    // Utilities
    @Inject private Methods methods;
    @Inject private ScheduleUtils scheduleUtils;

    // Listeners
    @Inject private QuickCrate quickCrate;

    // Task Handler
    @Inject private CrateTaskHandler crateTaskHandler;

    public void startFireCracker(final Player player, final Crate crate, KeyType keyType, final Location loc) {

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
                quickCrate.openCrate(player, loc, crate, KeyType.FREE_KEY);
            }
        }));
    }
}