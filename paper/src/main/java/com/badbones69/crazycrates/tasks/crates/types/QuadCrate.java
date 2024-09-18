package com.badbones69.crazycrates.tasks.crates.types;

import com.badbones69.crazycrates.api.objects.crates.quadcrates.CrateSchematic;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.crates.CrateLocation;
import com.ryderbelserion.vital.paper.util.structures.StructureManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.bukkit.configuration.file.YamlConfiguration;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.api.builders.CrateBuilder;
import com.badbones69.crazycrates.tasks.crates.other.quadcrates.QuadCrateManager;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class QuadCrate extends CrateBuilder {

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

        final YamlConfiguration config = getFile();
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
}