package com.ryderbelserion.crazycrates.common.storage.impl;

import com.ryderbelserion.crazycrates.common.enums.CrateStatus;
import com.ryderbelserion.crazycrates.common.objects.CrazyLocation;
import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NullMarked;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@NullMarked
public abstract class ConnectionFactory {

    public abstract String addCrateLocation(final String crateId, final Key worldKey, final String id, final int x, final int y, final int z);

    public abstract String addCrateLocation(final String crateId, final Key worldKey, final int x, final int y, final int z);

    public abstract void addWorld(final Key worldKey);

    public abstract boolean hasWorld(final Key worldKey);

    public abstract void removeWorld(final Key worldKey);

    public abstract Optional<CrazyLocation> getCrateLocation(final String id);

    public abstract Map<CrateStatus, List<CrazyLocation>> getCrateLocations();

    public abstract void removeCrateLocation(final String id);

    public abstract String getImpl();

    public abstract void init();

    public abstract void stop();

    public abstract void save();

}