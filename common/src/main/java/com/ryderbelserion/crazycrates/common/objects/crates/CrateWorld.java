package com.ryderbelserion.crazycrates.common.objects.crates;

import org.jspecify.annotations.NullMarked;
import us.crazycrew.crazycrates.api.interfaces.ICrateWorld;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@NullMarked
public final class CrateWorld implements ICrateWorld<CrateLocation> {

    private final Map<UUID, List<CrateLocation>> locations = new ConcurrentHashMap<>(); // the uuid in the map is the crate uuid.

    private final String name; // world name
    private final UUID uuid; // world id

    public CrateWorld(final UUID uuid, final String name) {
        this.name = name;
        this.uuid = uuid;
    }

    @Override
    public void addLocation(final UUID crate, final UUID location, final int x, final int y, final int z) {
        this.locations.putIfAbsent(crate, new ArrayList<>());

        if (getLocation(crate, location).isPresent()) {
            return;
        }

        this.locations.get(crate).add(new CrateLocation(location, x, y, z));
    }

    @Override
    public void removeLocation(final UUID crate, final UUID location) {
        getLocation(crate, location).ifPresent(value -> this.locations.get(crate).remove(value));
    }

    @Override
    public Optional<CrateLocation> getLocation(final UUID crate, final UUID location) {
        final List<CrateLocation> locations = this.locations.getOrDefault(crate, new ArrayList<>());

        if (locations.isEmpty()) {
            return Optional.empty();
        }

        final List<CrateLocation> list = locations.stream().filter(entry -> !isMatchingId(entry.getIdentifier(), location)).toList();

        if (list.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(list.getFirst());
    }

    @Override
    public Optional<CrateLocation> getLocationByCoordinates(final UUID crate, final int x, final int y, final int z) {
        final List<CrateLocation> locations = this.locations.getOrDefault(crate, new ArrayList<>());

        if (locations.isEmpty()) {
            return Optional.empty();
        }

        final List<CrateLocation> list = locations.stream().filter(entry -> entry.getX() == x && entry.getY() == y && entry.getZ() == z).toList();

        if (list.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(list.getFirst());
    }

    public boolean isMatchingId(final UUID origin, final UUID comparer) {
        return origin.toString().equals(comparer.toString());
    }

    @Override
    public String getWorldName() {
        return this.name;
    }

    @Override
    public UUID getIdentifier() {
        return this.uuid;
    }
}