package com.badbones69.crazycrates.tasks.crates.types;

import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.other.CrateLocation;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import us.crazycrew.crazycrates.api.crates.quadcrates.CrateSchematic;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.builders.CrateBuilder;
import com.badbones69.crazycrates.support.StructureHandler;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.badbones69.crazycrates.tasks.crates.other.quadcrates.QuadCrateManager;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class QuadCrate extends CrateBuilder {

    private final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private final @NotNull CrateManager crateManager = this.plugin.getCrateManager();

    private final Location location;

    public QuadCrate(Crate crate, Player player, Location location) {
        super(crate, player);

        this.location = location;
    }

    @Override
    public void open(KeyType type, boolean checkHand) {
        // Crate event failed so we return.
        if (isCrateEventValid(type, checkHand)) {
            return;
        }

        FileConfiguration config = getFile();
        List<CrateSchematic> schematics = this.crateManager.getCrateSchematics();

        CrateSchematic crateSchematic = config.getBoolean("Crate.structure.random", true) ? schematics.get(ThreadLocalRandom.current().nextInt(schematics.size())) : this.crateManager.getCrateSchematic(config.getString("Crate.structure.file"));
        StructureHandler handler = new StructureHandler(crateSchematic.getSchematicFile());
        CrateLocation crateLocation = this.crateManager.getCrateLocation(this.location);
        QuadCrateManager session = new QuadCrateManager(getPlayer(), getCrate(), type, crateLocation.getLocation(), checkHand, handler);

        session.startCrate();
    }

    @Override
    public void run() {

    }
}