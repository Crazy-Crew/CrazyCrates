package us.crazycrew.crazycrates.paper;

import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.common.CrazyCratesPlugin;
import us.crazycrew.crazycrates.paper.api.MigrationService;
import us.crazycrew.crazycrates.paper.api.users.BukkitUserManager;
import java.io.File;

public class CrazyHandler extends CrazyCratesPlugin {

    private BukkitUserManager userManager;

    public CrazyHandler(File dataFolder) {
        super(dataFolder);
    }

    public void load() {
        // Creates user manager instance.
        this.userManager = new BukkitUserManager();

        // Migrates 2 config.yml settings to plugin-config.yml
        MigrationService service = new MigrationService();
        service.migrate();
    }

    public void unload() {

    }

    @Override
    @NotNull
    public BukkitUserManager getUserManager() {
        return this.userManager;
    }
}