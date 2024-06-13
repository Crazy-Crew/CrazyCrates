package com.badbones69.crazycrates.tasks.crates.types;

import com.badbones69.crazycrates.api.crates.quadcrates.CrateSchematic;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.other.CrateLocation;
import com.ryderbelserion.vital.paper.util.structures.StructureManager;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.builders.CrateBuilder;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.badbones69.crazycrates.tasks.crates.other.quadcrates.QuadCrateManager;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class QuadCrate extends CrateBuilder {

    private @NotNull final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private @NotNull final CrateManager crateManager = this.plugin.getCrateManager();

    private final Location location;

    public QuadCrate(@NotNull final Crate crate, @NotNull final Player player, @NotNull final Location location) {
        super(crate, player);

        this.location = location;
    }

    @Override
    public void open(@NotNull final KeyType type, final boolean checkHand) {
        // Crate event failed so we return.
        if (isCrateEventValid(type, checkHand)) {
            return;
        }

        final FileConfiguration config = getFile();
        final List<CrateSchematic> schematics = this.crateManager.getCrateSchematics();

        final CrateSchematic crateSchematic = config.getBoolean("Crate.structure.random", true) ? schematics.get(ThreadLocalRandom.current().nextInt(schematics.size())) : this.crateManager.getCrateSchematic(config.getString("Crate.structure.file", ""));

        // todo() add message if schematic null before session starts.
        if (crateSchematic == null) {
            return;
        }

        final StructureManager handler = new StructureManager(this.plugin);

        handler.applyStructure(crateSchematic.schematicFile());

        final CrateLocation crateLocation = this.crateManager.getCrateLocation(this.location);

        if (crateLocation != null) {
            final QuadCrateManager session = new QuadCrateManager(getPlayer(), getCrate(), type, crateLocation.getLocation(), checkHand, handler);

            session.startCrate();
        }
    }

    @Override
    public void run() {

    }
}