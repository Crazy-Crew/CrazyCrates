package com.ryderbelserion.crazycrates.common.objects.crates;

import org.jspecify.annotations.NullMarked;
import us.crazycrew.crazycrates.api.interfaces.ICrateLocation;
import java.util.UUID;

@NullMarked
public final class CrateLocation implements ICrateLocation {

    private final UUID identifier;

    private final int x;
    private final int y;
    private final int z;

    public CrateLocation(
            final UUID identifier,
            final int x,
            final int y,
            final int z
    ) {
        this.identifier = identifier;

        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public UUID getIdentifier() {
        return this.identifier;
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public int getZ() {
        return this.z;
    }
}