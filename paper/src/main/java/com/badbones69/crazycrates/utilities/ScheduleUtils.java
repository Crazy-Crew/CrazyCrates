package com.badbones69.crazycrates.utilities;

import com.badbones69.crazycrates.CrazyCrates;
import org.bukkit.scheduler.BukkitTask;

public class ScheduleUtils {

    private final CrazyCrates crazyCrates = CrazyCrates.getInstance();
    

    public BukkitTask queue(Runnable task) {
        return crazyCrates.getServer().getScheduler().runTask(crazyCrates, task);
    }

    public BukkitTask async(Runnable task) {
        return crazyCrates.getServer().getScheduler().runTaskAsynchronously(crazyCrates, task);
    }

    public BukkitTask later(Long delay, Runnable task) {
        return crazyCrates.getServer().getScheduler().runTaskLater(crazyCrates, task, delay);
    }

    public BukkitTask laterAsync(Long delay, Runnable task) {
        return crazyCrates.getServer().getScheduler().runTaskLaterAsynchronously(crazyCrates, task, delay);
    }

    public BukkitTask timer(Long period, Long delay, Runnable task) {
        return crazyCrates.getServer().getScheduler().runTaskTimer(crazyCrates, task, delay, period);
    }

    public BukkitTask timerAsync(Long period, Long delay, Runnable task) {
        return crazyCrates.getServer().getScheduler().runTaskTimerAsynchronously(crazyCrates, task, delay, period);
    }
}