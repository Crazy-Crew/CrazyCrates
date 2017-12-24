package me.badbones69.crazycrates.cratetypes;

import me.badbones69.crazycrates.api.objects.Prize;
import me.badbones69.crazycrates.Main;
import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.enums.CrateType;
import me.badbones69.crazycrates.api.enums.KeyType;
import me.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.controlers.CrateControl;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class QuickCrate implements Listener
{

    public static HashMap<Player, Entity> Rewards = new HashMap<>();
    private static CrazyCrates cc = CrazyCrates.getInstance();
    private static HashMap<Player, BukkitTask> tasks = new HashMap<>();

    public static void openCrate(final Player player, final Location loc, Crate crate, KeyType key, boolean remove)
    {
        if (remove)
        {
            cc.takeKeys(1, player, crate, key);
        }
        Prize prize = cc.pickPrize(player, crate, loc.clone().add(.5, 1.3, .5));
        cc.getReward(player, prize);
        Bukkit.getPluginManager().callEvent(new PlayerPrizeEvent(player, CrateType.QUICK_CRATE, crate.getName(), prize));
        final Entity reward = player.getWorld().dropItem(loc.clone().add(.5, 1, .5), prize.getDisplayItem());
        reward.setVelocity(new Vector(0, .2, 0));
        reward.setCustomName(prize.getDisplayItem().getItemMeta().getDisplayName());
        reward.setCustomNameVisible(true);
        Rewards.put(player, reward);
        Methods.playChestAction(loc.getBlock(), true);
        if (prize.toggleFirework())
        {
            Methods.fireWork(loc.clone().add(.5, 1, .5));
        }
        tasks.put(player, new BukkitRunnable()
        {
            @Override
            public void run()
            {
                endQuickCrate(player, loc);
            }
        }.runTaskLater(Main.getPlugin(), 5 * 20));
    }

    public static void endQuickCrate(Player player, Location loc)
    {
        if (tasks.containsKey(player))
        {
            tasks.get(player).cancel();
            tasks.remove(player);
        }
        if (Rewards.get(player) != null)
        {
            Rewards.get(player).remove();
            Rewards.remove(player);
            Methods.playChestAction(loc.getBlock(), false);
            CrateControl.inUse.remove(player);
            cc.removePlayerFromOpeningList(player);
        }
    }

    @EventHandler
    public void onHopperPickUp(InventoryPickupItemEvent e)
    {
        if (Rewards.containsValue(e.getItem()))
        {
            e.setCancelled(true);
        }
    }

}