package com.badbones69.crazycrates.listeners;

import com.badbones69.crazycrates.CrazyCrates;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class FireworkDamageListener implements Listener {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    /**
     * @param firework The firework you want to add.
     */
    public void addFirework(Entity firework) {
        firework.setMetadata("nodamage", new FixedMetadataValue(plugin, true));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Firework fw) {
            if (fw.hasMetadata("nodamage")) e.setCancelled(true);
        }
    }
}