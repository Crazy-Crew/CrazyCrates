package com.badbones69.crazycrates.api.utilities.handlers.objects.crates;

import org.bukkit.scheduler.BukkitTask;
import java.util.UUID;

public class CrateTask {

    /**
     * The player uuid.
     */
    private UUID uuid;

    /**
     * The current bukkit task.
     */
    private BukkitTask currentTask;

    /**
     * Set the blank uuid to a new uuid
     * @param uuid - The new uuid.
     */
    public void setPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Set the blank bukkit task to a new bukkit task.
     * @param currentTask - The new bukkit task.
     */
    public void setCurrentTask(BukkitTask currentTask) {
        this.currentTask = currentTask;
    }

    /**
     * @return The player uuid.
     */
    public UUID getPlayer() {
        return uuid;
    }

    /**
     * @return The current bukkit task.
     */
    public BukkitTask getCurrentTask() {
        return currentTask;
    }
}