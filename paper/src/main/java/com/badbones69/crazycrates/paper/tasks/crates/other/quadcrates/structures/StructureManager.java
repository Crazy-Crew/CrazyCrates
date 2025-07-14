package com.badbones69.crazycrates.paper.tasks.crates.other.quadcrates.structures;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import com.google.common.collect.Lists;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.structure.Structure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class StructureManager implements IStructureManager {

    private final Set<Location> postStructurePasteBlocks = new HashSet<>();
    private final Set<Location> preStructurePasteBlocks = new HashSet<>();

    private final CrazyCrates plugin = CrazyCrates.getPlugin();
    private final ComponentLogger logger = this.plugin.getComponentLogger();
    private final boolean isVerbose = MiscUtils.isLogging();

    private File file = null;

    private Structure structure = null;

    private boolean doNotApply = false;

    @Override
    public void applyStructure(@Nullable final File file) {
        if (file == null) {
            this.doNotApply = true;

            return;
        }

        this.file = file;

        this.structure = CompletableFuture.supplyAsync(() -> {
            try {
                return this.plugin.getServer().getStructureManager().loadStructure(this.file);
            } catch (final IOException exception) {
                if (this.isVerbose) this.logger.error("Failed to load structure: {}!", this.file.getName(), exception);

                return null;
            }
        }).join();
    }

    @Override
    public @NotNull org.bukkit.structure.StructureManager getStructureManager() {
        return this.plugin.getServer().getStructureManager();
    }

    @Override
    public void saveStructure(@Nullable final File file, @Nullable final Location one, @Nullable final Location two, boolean includeEntities) {
        if (this.doNotApply) return;

        if (file == null || one == null || two == null) return;

        // Fill the structure with blocks between 2 corners.
        this.structure.fill(one, two, includeEntities);

        // Save structure to file.
        try {
            getStructureManager().saveStructure(file, this.structure);
        } catch (final IOException exception) {
            if (this.isVerbose) this.logger.error("Failed to save structure to: {}!", file.getName(), exception);
        }
    }

    @Override
    public void pasteStructure(@Nullable final Location location, final boolean storeBlocks) {
        if (this.doNotApply) return;

        if (location == null) return;

        try {
            // Get the blocks from the hashset and set them.
            if (storeBlocks) getBlocks(location);

            final Location clonedLocation = location.clone().subtract(2, 0.0, 2);

            // Place the structure.
            this.structure.place(clonedLocation, false, StructureRotation.NONE, Mirror.NONE, 0, 1F, ThreadLocalRandom.current());

            // Get the structure blocks.
            if (storeBlocks) getStructureBlocks(clonedLocation);
        } catch (final Exception exception) {
            if (this.isVerbose) this.logger.error("Could not paste structure", exception);
        }
    }

    @Override
    public void removeStructure() {
        if (this.doNotApply) return;

        this.postStructurePasteBlocks.forEach(block -> {
            if (block.getBlock().getType() != Material.AIR) {
                Location location = block.toBlockLocation();

                location.getBlock().setType(Material.AIR, true);
            }
        });
    }

    private void getStructureBlocks(@NotNull final Location location) {
        for (int x = 0; x < getStructureX(); x++) {
            for (int y = 0; y < getStructureY(); y++) {
                for (int z = 0; z < getStructureZ(); z++) {
                    final Block relativeLocation = location.getBlock().getRelative(x, y, z);

                    this.postStructurePasteBlocks.add(relativeLocation.getLocation());

                    this.postStructurePasteBlocks.forEach(block -> {
                        final Location blockLoc = block.toBlockLocation();

                        blockLoc.getBlock().getState().update();
                    });
                }
            }
        }
    }

    @Override
    public @NotNull Set<Location> getBlocks(@Nullable final Location location) {
        if (this.doNotApply) return Collections.emptySet();

        if (location == null) return getNearbyBlocks();

        for (int x = 0; x < getStructureX(); x++) {
            for (int y = 0; y < getStructureY(); y++) {
                for (int z = 0; z < getStructureZ(); z++) {
                    Block relativeLocation = location.getBlock().getRelative(x, y, z).getLocation().subtract(2, 0.0, 2).getBlock();

                    this.preStructurePasteBlocks.add(relativeLocation.getLocation());
                }
            }
        }

        return getNearbyBlocks();
    }

    @Override
    public double getStructureX() {
        if (this.doNotApply) return 0.0;

        return this.structure.getSize().getX();
    }

    @Override
    public double getStructureY() {
        if (this.doNotApply) return 0.0;

        return this.structure.getSize().getY();
    }

    @Override
    public double getStructureZ() {
        if (this.doNotApply) return 0.0;

        return this.structure.getSize().getZ();
    }

    @Override
    public @NotNull Set<Location> getNearbyBlocks() {
        return Collections.unmodifiableSet(this.preStructurePasteBlocks);
    }

    @Override
    public @NotNull List<Material> getBlockBlacklist() {
        return Lists.newArrayList(
                Material.OAK_SIGN, Material.SPRUCE_SIGN, Material.BIRCH_SIGN, Material.JUNGLE_SIGN, Material.ACACIA_SIGN, Material.CHERRY_SIGN, Material.DARK_OAK_SIGN,
                Material.MANGROVE_SIGN, Material.BAMBOO_SIGN, Material.CRIMSON_SIGN, Material.WARPED_SIGN, Material.OAK_HANGING_SIGN, Material.SPRUCE_HANGING_SIGN,
                Material.BIRCH_HANGING_SIGN, Material.JUNGLE_HANGING_SIGN, Material.ACACIA_HANGING_SIGN, Material.CHERRY_HANGING_SIGN, Material.DARK_OAK_HANGING_SIGN,
                Material.MANGROVE_HANGING_SIGN, Material.BAMBOO_HANGING_SIGN, Material.CRIMSON_HANGING_SIGN, Material.WARPED_HANGING_SIGN,

                Material.STONE_BUTTON, Material.POLISHED_BLACKSTONE_BUTTON, Material.OAK_BUTTON, Material.SPRUCE_BUTTON, Material.BIRCH_BUTTON,
                Material.JUNGLE_BUTTON, Material.ACACIA_BUTTON, Material.CHERRY_BUTTON, Material.DARK_OAK_BUTTON, Material.MANGROVE_BUTTON, Material.BAMBOO_BUTTON,
                Material.CRIMSON_BUTTON, Material.WARPED_BUTTON);
    }

    @Override
    public void createStructure() {
        this.structure = getStructureManager().createStructure();
    }

    @Override
    public @NotNull File getStructureFile() {
        return this.file;
    }
}