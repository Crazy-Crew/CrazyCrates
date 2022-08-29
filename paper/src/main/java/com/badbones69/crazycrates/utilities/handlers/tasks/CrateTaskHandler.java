package com.badbones69.crazycrates.utilities.handlers.tasks;

import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.utilities.CommonUtils;
import com.badbones69.crazycrates.utilities.handlers.objects.CrateTask;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitTask;
import java.util.HashMap;
import java.util.UUID;

@Singleton
public class CrateTaskHandler {

    private final HashMap<UUID, CrateTask> currentTasks = new HashMap<>();

    private final CrateTask crateTask = new CrateTask();

    @Inject private CrazyManager crazyManager;

    @Inject private CommonUtils commonUtils;

    /**
     * Add a crate task that is going on for a player.
     *
     * @param player The player opening the crate.
     * @param task The task of the crate.
     */
    public void addTask(Player player, BukkitTask task) {
        crateTask.setPlayer(player.getUniqueId());
        crateTask.setCurrentTask(task);

        System.out.println(crateTask.getPlayer());

        currentTasks.put(crateTask.getPlayer(), crateTask);
    }

    /**
     * Checks to see if the player has a crate task going on.
     *
     * @return True if they do have a task and false if not.
     */
    public boolean hasCrateTask() {
        System.out.println(crateTask.getPlayer());

        return currentTasks.containsKey(crateTask.getPlayer());
    }

    /**
     * Remove a task from the list of current tasks.
     */
    public void removeTask() {
        System.out.println(crateTask.getPlayer());

        if (hasCrateTask()) getCurrentTasks().get(crateTask.getPlayer()).getCurrentTask().cancel();

        currentTasks.remove(crateTask.getPlayer());
    }

    public HashMap<UUID, CrateTask> getCurrentTasks() {
        return currentTasks;
    }

    public void endCrate() {
        if (hasCrateTask()) removeTask();
    }

    public void endCrate(Player player, Crate crate, Inventory inventory) {
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

        endCrate();

        Prize prize = crate.getPrize(inventory.getItem(13));

        commonUtils.pickPrize(player, crate, prize);

        crazyManager.removePlayerFromOpeningList(player);
    }
}