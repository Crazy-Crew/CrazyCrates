package com.badbones69.crazycrates.api.utilities.handlers.tasks;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.utilities.CommonUtils;
import com.badbones69.crazycrates.api.utilities.handlers.objects.crates.Crate;
import com.badbones69.crazycrates.api.utilities.handlers.objects.crates.CrateTask;
import com.badbones69.crazycrates.api.utilities.handlers.objects.Prize;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitTask;
import java.util.HashMap;
import java.util.UUID;

public class CrateTaskHandler {

    private final HashMap<UUID, CrateTask> currentTasks = new HashMap<>();

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrazyManager crazyManager = plugin.getStarter().getCrazyManager();

    private final CommonUtils commonUtils = plugin.getStarter().getCommonUtils();

    /**
     * Add a crate task that is going on for a player.
     *
     * @param player The player opening the crate.
     * @param task The task of the crate.
     */
    public void addTask(Player player, BukkitTask task) {
        CrateTask crateTask = new CrateTask();

        crateTask.setPlayer(player.getUniqueId());
        crateTask.setCurrentTask(task);

        currentTasks.put(crateTask.getPlayer(), crateTask);
    }

    /**
     * Checks to see if the player has a crate task going on.
     * @param player - The player opening the crate.
     *
     * @return True if they do have a task and false if not.
     */
    public boolean hasCrateTask(Player player) {
        return currentTasks.containsKey(player.getUniqueId());
    }

    /**
     * Remove a task from the list of current tasks.
     * @param player - The player opening the crate.
     */
    public void removeTask(Player player) {
        if (hasCrateTask(player)) getTasks().get(player.getUniqueId()).getCurrentTask().cancel();

        currentTasks.remove(player.getUniqueId());
    }

    /**
     * Clear all tasks
     */
    public void clearTasks() {
        if (!currentTasks.isEmpty()) currentTasks.clear();
    }

    /**
     * Get the list of current tasks
     */
    public HashMap<UUID, CrateTask> getTasks() {
        return currentTasks;
    }

    /**
     * End the task for a player if they have one.
     * @param player - The player opening the crate.
     */
    public void endCrate(Player player) {
        if (hasCrateTask(player)) removeTask(player);
    }

    /**
     * End the crate and give the prize to the player.
     * @param player - The player opening the crate.
     * @param crate - The crate they are using.
     * @param inventory - The inventory that is open.
     */
    public void endCrate(Player player, Crate crate, Inventory inventory) {
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

        endCrate(player);

        Prize prize = crate.getPrize(inventory.getItem(13));

        commonUtils.pickPrize(player, crate, prize);

        crazyManager.removePlayerFromOpeningList(player);
    }
}