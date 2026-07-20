package com.badbones69.crazycrates.paper.cache;

import com.badbones69.crazycrates.paper.cache.objects.ActiveCrate;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import org.bukkit.Location;
import org.jspecify.annotations.NullMarked;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@NullMarked
public final class CacheManager {

    // crates that are currently being opened by a player
    private final Map<UUID, ActiveCrate> crates = new HashMap<>();

    public Optional<ActiveCrate> getActiveCrateByLocation(final Location location) {
        final String index = MiscUtils.location(location);

        Optional<ActiveCrate> optional = Optional.empty();

        for (final ActiveCrate crate : this.crates.values()) {
            if (!index.equals(crate.getLocationAsString())) {
                continue;
            }

            optional = Optional.of(crate);

            break;
        }

        return optional;
    }

    public void addActiveCrate(final UUID uuid, final ActiveCrate crate) {
        this.crates.putIfAbsent(uuid, crate);
    }

    public Optional<ActiveCrate> getActiveCrate(final UUID uuid) {
        return Optional.ofNullable(this.crates.get(uuid));
    }

    public boolean hasOpeningCrate(final UUID uuid) {
        return this.crates.containsKey(uuid);
    }

    public void removeActiveCrate(final UUID uuid) {
        this.crates.remove(uuid);
    }
}