package com.ryderbelserion.crazycrates.common.objects;

import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class CrazyLocation {

    private final String crateName;
    private final Key worldKey;
    private final String id;
    private final int x;
    private final int y;
    private final int z;

    public CrazyLocation(final String crateName, final Key worldKey, final String id, final int x, final int y, final int z) {
        this.crateName = crateName;
        this.worldKey = worldKey;
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Key getWorldKey() {
        return this.worldKey;
    }

    @Deprecated
    public String getWorldName() {
        return getWorldId();
    }

    public String getWorldId() {
        return this.worldKey.asString();
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