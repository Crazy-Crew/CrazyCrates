package com.badbones69.crazycrates.platform;

import com.badbones69.crazycrates.CrazyCratesPaper;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.platform.Server;
import java.io.File;

public class PaperServer implements Server {

    @NotNull
    private final CrazyCratesPaper plugin = JavaPlugin.getPlugin(CrazyCratesPaper.class);

    @Override
    public File getFolder() {
        return this.plugin.getDataFolder();
    }
}