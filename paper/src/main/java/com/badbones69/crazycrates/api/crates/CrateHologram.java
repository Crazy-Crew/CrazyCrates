package com.badbones69.crazycrates.api.crates;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A class containing the data required to create a hologram.
 *
 * @author Ryder Belserion
 * @version 0.6
 * @since 0.1
 */
public class CrateHologram {
    
    private final boolean enabled;
    private final double height;
    private final int range;
    private final List<String> messages;
    private final String backgroundColor;
    private final int updateInterval;

    /**
     * Empty constructor
     */
    public CrateHologram() {
        this.enabled = false;
        this.height = 0.0;
        this.range = 8;
        this.messages = new ArrayList<>();
        this.backgroundColor = "transparent";
        this.updateInterval = -1;
    }

    /**
     * A secondary constructor to build a hologram.
     *
     * @param enabled if the hologram enabled or not
     * @param height height of the hologram from the ground
     * @param range range the hologram can be seen
     * @param color the background color
     * @param updateInterval the update interval
     * @param messages the hologram will display
     */
    public CrateHologram(final boolean enabled, final double height, final int range, @NotNull final String color, final int updateInterval, @NotNull final List<String> messages) {
        this.enabled = enabled;
        this.height = height;
        this.range = range;
        this.backgroundColor = color;
        this.updateInterval = updateInterval;
        this.messages = messages;
    }

    /**
     * Check if the hologram is enabled or not.
     *
     * @return true if yes otherwise false
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Gets the range at which a hologram can be seen.
     *
     * @return the range
     */
    public int getRange() {
        return this.range;
    }

    /**
     * Get the height of the hologram from the ground.
     *
     * @return the height
     */
    public double getHeight() {
        return this.height;
    }

    /**
     * Gets the background color of the hologram.
     *
     * @return the color
     */
    public String getBackgroundColor() {
        return this.backgroundColor;
    }

    /**
     * Gets the update interval.
     *
     * @return the update interval
     */
    public int getUpdateInterval() {
        return this.updateInterval;
    }

    /**
     * Get the messages the hologram will display.
     *
     * @return the list of messages
     */
    public List<String> getMessages() {
        return this.messages;
    }
}