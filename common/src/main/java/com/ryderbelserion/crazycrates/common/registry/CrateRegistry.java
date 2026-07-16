package com.ryderbelserion.crazycrates.common.registry;

import com.ryderbelserion.crazycrates.common.objects.crates.CrateWorld;
import org.jspecify.annotations.NullMarked;
import us.crazycrew.crazycrates.api.interfaces.registry.ICrateRegistry;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@NullMarked
public final class CrateRegistry implements ICrateRegistry<CrateWorld> {

    private final Map<UUID, CrateWorld> worlds = new ConcurrentHashMap<>();

    @Override
    public void addWorld(final CrateWorld world) {
        this.worlds.putIfAbsent(world.getIdentifier(), world);
    }

    @Override
    public void removeWorld(final UUID identifier) {
        this.worlds.remove(identifier);
    }

    @Override
    public boolean hasWorld(final UUID identifier) {
        return this.worlds.containsKey(identifier);
    }

    @Override
    public Optional<CrateWorld> getWorld(final UUID identifier) {
        return Optional.ofNullable(this.worlds.get(identifier));
    }
}