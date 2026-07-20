package com.ryderbelserion.crazycrates.common.storage.holder;

import com.ryderbelserion.crazycrates.common.enums.CrateStatus;
import com.ryderbelserion.crazycrates.common.objects.CrazyLocation;
import com.ryderbelserion.crazycrates.common.storage.impl.ConnectionFactory;
import org.jspecify.annotations.NullMarked;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@NullMarked
public final class StorageHolder {

    private final ConnectionFactory factory;

    public StorageHolder(final ConnectionFactory factory) {
        this.factory = factory;
    }

    public StorageHolder init() {
        this.factory.init();

        return this;
    }

    public StorageHolder save() {
        this.factory.save();

        return this;
    }

    public String addCrateLocation(final String crateName, final String worldName, final String id, final int x, final int y, final int z) {
        return this.factory.addCrateLocation(crateName, worldName, id, x, y, z);
    }

    public String addCrateLocation(final String crateName, final String worldName, final int x, final int y, final int z) {
        return addCrateLocation(crateName, worldName, UUID.randomUUID().toString(), x, y, z);
    }

    public Optional<CrazyLocation> getCrateLocation(final String id) {
        return this.factory.getCrateLocation(id);
    }

    public Map<CrateStatus, List<CrazyLocation>> getCrateLocations() {
        return this.factory.getCrateLocations();
    }

    public void removeCrateLocation(final String id) {
        this.factory.removeCrateLocation(id);
    }
}