package com.badbones69.crazycrates.controllers;

import com.badbones69.crazycrates.api.CrazyManager;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.ArrayList;

public class FireworkDamageEvent implements Listener {
    
    private static final ArrayList<Entity> fireworks = new ArrayList<>();

    private static final CrazyManager cc = CrazyManager.getInstance();
    
    /**
     *
     * @return All the active fireworks.
     */
    public static ArrayList<Entity> getFireworks() {
        return fireworks;
    }
    
    /**
     *
     * @param firework The firework you want to add.
     */
    public static void addFirework(Entity firework) {
        fireworks.add(firework);
    }
    
    /**
     *
     * @param firework The firework you are removing.
     */
    public static void removeFirework(Entity firework) {
        fireworks.remove(firework);
    }
    
    @EventHandler
    public void onFireworkDamage(EntityDamageByEntityEvent e) {
        if (fireworks.contains(e.getDamager())) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onFireworkExplode(FireworkExplodeEvent e) {
        final Entity firework = e.getEntity();
        if (getFireworks().contains(firework)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    fireworks.remove(firework);
                }
            }.runTaskLater(cc.getPlugin(), 5);
        }
    }
    
}