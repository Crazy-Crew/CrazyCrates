package com.badbones69.crazycrates.listeners.other;

import com.badbones69.crazycrates.api.enums.PersistentKeys;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataContainer;

public class EntityDamageListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onFireworkDamage(EntityDamageEvent event) {
        final Entity directEntity = event.getDamageSource().getDirectEntity();

        if (directEntity instanceof final Firework firework) {
            final PersistentDataContainer container = firework.getPersistentDataContainer();

            if (container.has(PersistentKeys.no_firework_damage.getNamespacedKey())) {
                event.setCancelled(true);
            }
        }
    }
}