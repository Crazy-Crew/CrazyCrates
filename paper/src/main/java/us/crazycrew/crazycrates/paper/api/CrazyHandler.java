package us.crazycrew.crazycrates.paper.api;

import com.badbones69.crazycrates.paper.CrazyCrates;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.paper.api.users.BukkitUserManager;

public class CrazyHandler {

    @NotNull
    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private BukkitUserManager userManager;

    public void load() {
        this.userManager = new BukkitUserManager();
    }

    public void unload() {

    }

    @NotNull
    public BukkitUserManager getUserManager() {
        return this.userManager;
    }
}