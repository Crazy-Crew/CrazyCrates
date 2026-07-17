package com.ryderbelserion.crazycrates.common.objects;

import org.jspecify.annotations.NullMarked;

@NullMarked
public final class CrazyLocation {

    private final String crateName;
    private final String worldName;
    private final String id;
    private final int x;
    private final int y;
    private final int z;

    public CrazyLocation(final String crateName, final String worldName, final String id, final int x, final int y, final int z) {
        this.crateName = crateName;
        this.worldName = worldName;
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getWorldName() {
        return this.worldName;
    }

    public String getCrateName() {
        return this.crateName;
    }

    public String getId() {
        return this.id;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }
}