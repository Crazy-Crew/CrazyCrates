package com.badbones69.crazycrates.common.enums.particles;

public enum QuadCrateParticles {
    
    FLAME("Flame"),
    VILLAGER_HAPPY("Villager Happy"),
    SPELL_WITCH("Spell Witch"),
    REDSTONE("Redstone");

    /**
     * The blank particle name.
     */
    private final String name;

    /**
     * @param name - The particle name.
     */
    QuadCrateParticles(String name) {
        this.name = name;
    }

    /**
     * @return The particle name.
     */
    public String getName() {
        return name;
    }
}