package com.badbones69.crazycrates.tasks.crates.types;

import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.other.CrateLocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.common.crates.quadcrates.CrateSchematic;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.builders.CrateBuilder;
import com.badbones69.crazycrates.support.StructureHandler;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.badbones69.crazycrates.tasks.crates.other.quadcrates.QuadCrateManager;
import java.util.concurrent.ThreadLocalRandom;

public class QuadCrate extends CrateBuilder {

    @NotNull
    private final CrazyCrates plugin = CrazyCrates.get();

    @NotNull
    private final CrateManager crateManager = this.plugin.getCrateManager();

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

        CrateSchematic crateSchematic = this.crateManager.getCrateSchematics().get(ThreadLocalRandom.current().nextInt(this.crateManager.getCrateSchematics().size()));
        StructureHandler handler = new StructureHandler(crateSchematic.getSchematicFile());
        CrateLocation crateLocation = this.crateManager.getCrateLocation(this.location);
        QuadCrateManager session = new QuadCrateManager(getPlayer(), getCrate(), type, crateLocation.getLocation(), checkHand, handler);

        session.startCrate();
    }

    @Override
    public void run() {

    }
}