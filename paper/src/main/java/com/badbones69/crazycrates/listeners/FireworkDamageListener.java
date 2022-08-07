package com.badbones69.crazycrates.listeners;

import com.badbones69.crazycrates.CrazyCrates;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class FireworkDamageListener implements Listener {

    /**
     * @param firework The firework you want to add.
     */
    public void addFirework(Entity firework, CrazyCrates plugin) {
        firework.setMetadata("nodamage", new FixedMetadataValue(plugin, true));
    }

    // Ryder Start
    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Firework fw) {
            if (fw.hasMetadata("nodamage")) e.setCancelled(true);
        }
    }
    // Ryder End
}