package com.badbones69.crazycrates.utilities.handlers.objects.crates;

import org.bukkit.scheduler.BukkitTask;
import java.util.UUID;

public class CrateTask {

    private UUID uuid;

    private BukkitTask currentTask;

    public void setPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public void setCurrentTask(BukkitTask currentTask) {
        this.currentTask = currentTask;
    }

    public UUID getPlayer() {
        return uuid;
    }

    public BukkitTask getCurrentTask() {
        return currentTask;
    }
}