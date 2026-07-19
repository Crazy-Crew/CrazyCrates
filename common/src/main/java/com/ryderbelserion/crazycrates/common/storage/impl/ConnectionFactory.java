package com.ryderbelserion.crazycrates.common.storage.impl;

import com.ryderbelserion.crazycrates.common.enums.CrateStatus;
import com.ryderbelserion.crazycrates.common.objects.CrazyLocation;
import org.jspecify.annotations.NullMarked;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@NullMarked
public abstract class ConnectionFactory {

    public abstract String addCrateLocation(final String crateName, final String worldName, final String id, final int x, final int y, final int z);

    public abstract String addCrateLocation(final String crateName, final String worldName, final int x, final int y, final int z);

    public abstract Optional<CrazyLocation> getCrateLocation(final String id);

    public abstract Map<CrateStatus, List<CrazyLocation>> getCrateLocations();

    public abstract void removeCrateLocation(final String id);

    public abstract String getImpl();

    public abstract void init();

    public abstract void stop();

    public abstract void save();

}