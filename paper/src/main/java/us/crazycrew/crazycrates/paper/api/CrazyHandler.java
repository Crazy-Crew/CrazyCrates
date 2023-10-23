package us.crazycrew.crazycrates.paper.api;

import com.badbones69.crazycrates.paper.CrazyCrates;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.common.CrazyCratesPlugin;
import us.crazycrew.crazycrates.paper.api.users.BukkitUserManager;
import java.io.File;

public class CrazyHandler extends CrazyCratesPlugin {

    @NotNull
    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private BukkitUserManager userManager;

    public CrazyHandler(File dataFolder) {
        super(dataFolder);
    }

    public void load() {
        this.userManager = new BukkitUserManager();
    }

    public void unload() {

    }

    @Override
    @NotNull
    public BukkitUserManager getUserManager() {
        return this.userManager;
    }
}