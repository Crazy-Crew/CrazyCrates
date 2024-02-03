package com.badbones69.crazycrates.common.crates;

import java.util.ArrayList;
import java.util.List;

public class CrateHologram {
    
    private final boolean enabled;
    private final double height;
    private final int range;
    private final List<String> messages;

    /**
     * Empty constructor
     */
    public CrateHologram() {
        this.enabled = false;
        this.height = 0.0;
        this.range = 8;
        this.messages = new ArrayList<>();
    }

    /**
     * A secondary constructor to build a hologram.
     *
     * @param enabled if the hologram enabled or not
     * @param height of the hologram from the ground
     * @param messages the hologram will display
     */
    public CrateHologram(boolean enabled, double height, int range, List<String> messages) {
        this.enabled = enabled;
        this.height = height;
        this.range = range;
        this.messages = messages;
    }

    /**
     * Check if the hologram is enabled or not.
     *
     * @return true if yes otherwise false.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Gets the range at which a hologram can be seen.
     *
     * @return the range
     */
    public int getRange() {
        return range;
    }

    /**
     * Get the height of the hologram from the ground.
     *
     * @return the height
     */
    public double getHeight() {
        return height;
    }

    /**
     * Get the messages the hologram will display.
     *
     * @return the list of messages
     */
    public List<String> getMessages() {
        return messages;
    }
}