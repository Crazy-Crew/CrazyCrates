package com.badbones69.crazycrates.utils.scheduler;

import com.badbones69.crazycrates.CrazyCrates;
import org.bukkit.scheduler.BukkitTask;

public class SchedulerUtils {

    private static final CrazyCrates plugin = CrazyCrates.getPlugin();

    /**
     * Whether to queue a single task.
     *
     * @param task the runnable lambda.
     * @return the new sync task.
     */
    public static BukkitTask queue(Runnable task) {
        return plugin.getServer().getScheduler().runTask(plugin, task);
    }

    /**
     * Whether to queue a single task.
     *
     * @param task the runnable lambda.
     * @return the new async task.
     */
    public static BukkitTask async(Runnable task) {
        return plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task);
    }

    /**
     * Whether to queue a later task.
     *
     * @param delay the delay that defines when the task should run.
     * @param task the runnable lambda.
     * @return the new sync later task.
     */
    public static BukkitTask later(long delay, Runnable task) {
        return plugin.getServer().getScheduler().runTaskLater(plugin, task, delay);
    }

    /**
     * Whether to queue an async later task.
     *
     * @param delay the delay that defines when the task should run.
     * @param task the runnable lambda.
     * @return the new async later task.
     */
    public static BukkitTask laterAsync(long delay, Runnable task) {
        return plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, task, delay);
    }

    /**
     * Whether to queue a sync timed task.
     *
     * @param period the period defines how long it should run.
     * @param delay the delay that defines when the task should run.
     * @param task the runnable lambda.
     * @return the new sync timed task.
     */
    public static BukkitTask timer(long period, long delay, Runnable task) {
        return plugin.getServer().getScheduler().runTaskTimer(plugin, task, delay, period);
    }

    /**
     * Whether to queue an async timed task.
     *
     * @param period the period defines how long it should run.
     * @param delay the delay that defines when the task should run.
     * @param task the runnable lambda.
     * @return the new async timed task.
     */
    public static BukkitTask timerAsync(long period, long delay, Runnable task) {
        return plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, task, delay, period);
    }
}