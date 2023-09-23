package com.badbones69.crazycrates.paper.listeners;

import com.badbones69.crazycrates.paper.api.enums.DataKeys;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class FireworkDamageListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Firework firework) {

            PersistentDataContainer container = firework.getPersistentDataContainer();

            if (container.has(DataKeys.NO_FIREWORK_DAMAGE.getKey(), PersistentDataType.BOOLEAN)) event.setCancelled(true);
        }
    }
}