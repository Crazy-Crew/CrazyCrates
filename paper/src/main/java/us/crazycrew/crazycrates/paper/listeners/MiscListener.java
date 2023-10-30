package us.crazycrew.crazycrates.paper.listeners;

import org.bukkit.entity.Firework;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.persistence.PersistentDataContainer;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.FileManager.Files;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.paper.CrazyHandler;
import us.crazycrew.crazycrates.paper.api.enums.PersistentKeys;

public class MiscListener implements Listener {

    @NotNull
    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    @NotNull
    private final CrazyHandler crazyHandler = this.plugin.getCrazyHandler();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent e) {
        Player player = e.getPlayer();

        // Set new keys if we have to.
        this.plugin.getCrateManager().setNewPlayerKeys(player);

        // Just in case any old data is in there.
        this.plugin.getCrazyManager().loadOfflinePlayersKeys(player);

        // Also add the new data.
        this.crazyHandler.getUserManager().loadOfflinePlayersKeys(player, this.crazyHandler.getCrateManager().getCrates());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerAttemptPickUp(PlayerAttemptPickupItemEvent event) {
        if (this.plugin.getCrazyManager().isDisplayReward(event.getItem())) {
            event.setCancelled(true);
            return;
        }

        if (this.plugin.getCrazyManager().isInOpeningList(event.getPlayer())) {
            // DrBot Start
            if (this.plugin.getCrazyManager().getOpeningCrate(event.getPlayer()).getCrateType().equals(CrateType.quick_crate)) return;
            // DrBot End
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onFireworkDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Firework firework) {
            PersistentDataContainer container = firework.getPersistentDataContainer();

            if (container.has(PersistentKeys.no_firework_damage.getNamespacedKey(this.plugin))) event.setCancelled(true);
        }
    }
}