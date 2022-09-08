package com.badbones69.crazycrates.api.utilities;

import com.badbones69.crazycrates.CrazyCrates;
import com.google.inject.Singleton;
import org.bukkit.scheduler.BukkitTask;

@Singleton
public class ScheduleUtils {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    public BukkitTask queue(Runnable task) {
        return plugin.getServer().getScheduler().runTask(plugin, task);
    }

    public BukkitTask async(Runnable task) {
        return plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task);
    }

    public BukkitTask later(Long delay, Runnable task) {
        return plugin.getServer().getScheduler().runTaskLater(plugin, task, delay);
    }

    public BukkitTask laterAsync(Long delay, Runnable task) {
        return plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, task, delay);
    }

    public BukkitTask timer(Long period, Long delay, Runnable task) {
        return plugin.getServer().getScheduler().runTaskTimer(plugin, task, delay, period);
    }

    public BukkitTask timerAsync(Long period, Long delay, Runnable task) {
        return plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, task, delay, period);
    }
}