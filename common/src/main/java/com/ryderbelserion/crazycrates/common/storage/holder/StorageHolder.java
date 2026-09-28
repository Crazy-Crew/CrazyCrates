package com.ryderbelserion.crazycrates.common.storage.holder;

import com.ryderbelserion.crazycrates.common.enums.CrateStatus;
import com.ryderbelserion.crazycrates.common.objects.CrazyLocation;
import com.ryderbelserion.crazycrates.common.storage.impl.ConnectionFactory;
import net.kyori.adventure.key.Key;
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

    public StorageHolder stop() {
        this.factory.stop();

        return this;
    }

    public boolean hasWorld(final Key key) {
        return this.factory.hasWorld(key);
    }

    public void addWorld(final Key key) {
        this.factory.addWorld(key);
    }

    public String addCrateLocation(final String crateName, final Key worldKey, final String id, final int x, final int y, final int z) {
        return this.factory.addCrateLocation(crateName, worldKey, id, x, y, z);
    }

    public String addCrateLocation(final String crateName, final Key worldKey, final int x, final int y, final int z) {
        return addCrateLocation(crateName, worldKey, UUID.randomUUID().toString(), x, y, z);
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