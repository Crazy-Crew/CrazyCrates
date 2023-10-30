package us.crazycrew.crazycrates.paper.listeners;

import us.crazycrew.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.Starter;
import com.badbones69.crazycrates.paper.api.FileManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.paper.CrazyHandler;

public class PlayerKeyListener implements Listener {

    @NotNull
    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    @NotNull
    private final CrazyHandler crazyHandler = this.plugin.getCrazyHandler();

    @NotNull
    private final Starter starter = this.plugin.getCrazyCrates().getStarter();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent e) {
        Player player = e.getPlayer();

        // Set new keys if we have to.
        this.starter.getCrazyManager().setNewPlayerKeys(player);

        // Just in case any old data is in there.
        this.starter.getCrazyManager().loadOfflinePlayersKeys(player);

        // Also add the new data.
        this.crazyHandler.getUserManager().loadOfflinePlayersKeys(player, this.starter.getCrazyManager().getCrates());

        FileManager.Files.DATA.saveFile();
    }
}