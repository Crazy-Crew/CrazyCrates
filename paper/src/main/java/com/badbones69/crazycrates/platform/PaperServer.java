package com.badbones69.crazycrates.platform;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.ICrazyCrates;
import us.crazycrew.crazycrates.platform.Server;
import java.io.File;
import java.util.logging.Logger;

public class PaperServer implements Server, ICrazyCrates {

    @NotNull
    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    @Override
    public File getFolder() {
        return this.plugin.getDataFolder();
    }

    @Override
    public Logger getLogger() {
        return this.plugin.getLogger();
    }

    @NotNull
    @Override
    public BukkitUserManager getUserManager() {
        return this.plugin.getUserManager();
    }
}