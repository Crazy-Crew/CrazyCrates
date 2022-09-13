package com.badbones69.crazycrates.api.utilities;

import com.badbones69.crazycrates.CrazyCrates;
import org.bukkit.scheduler.BukkitTask;

public class ScheduleUtils {

    // Global Methods.
    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    // Class Internals.

    /**
     * Whether to queue a single task.
     * @param task - The runnable lambda.
     * @return The new sync task.
     */
    public BukkitTask queue(Runnable task) {
        return plugin.getServer().getScheduler().runTask(plugin, task);
    }

    /**
     * Whether to queue a single task.
     * @param task - The runnable lambda.
     * @return The new async task.
     */
    public BukkitTask async(Runnable task) {
        return plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task);
    }

    /**
     * Whether to queue a later task.
     * @param delay - The delay that defines when the task should run.
     * @param task - The runnable lambda.
     * @return The new sync later task.
     */
    public BukkitTask later(long delay, Runnable task) {
        return plugin.getServer().getScheduler().runTaskLater(plugin, task, delay);
    }

    /**
     * Whether to queue an async later task.
     * @param delay - The delay that defines when the task should run.
     * @param task - The runnable lambda.
     * @return The new async later task.
     */
    public BukkitTask laterAsync(long delay, Runnable task) {
        return plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, task, delay);
    }

    /**
     * Whether to queue a sync timed task.
     * @param period - The period defines how long it should run.
     * @param delay - The delay that defines when the task should run.
     * @param task - The runnable lambda.
     * @return The new sync timed task.
     */
    public BukkitTask timer(long period, long delay, Runnable task) {
        return plugin.getServer().getScheduler().runTaskTimer(plugin, task, delay, period);
    }

    /**
     * Whether to queue an async timed task.
     * @param period - The period defines how long it should run.
     * @param delay - The delay that defines when the task should run.
     * @param task - The runnable lambda.
     * @return The new async timed task.
     */
    public BukkitTask timerAsync(long period, long delay, Runnable task) {
        return plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, task, delay, period);
    }
}