package com.badbones69.crazycrates.cratetypes;

public class FireCrackerCrate {
    /*
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
     */
}