package com.badbones69.crazycrates.api.quadcrates;

public enum CrateParticles {
    
    FLAME("Flame"),
    VILLAGER_HAPPY("Villager Happy"),
    SPELL_WITCH("Spell Witch"),
    REDSTONE("Redstone");
    
    private final String name;

    /**
     * A constructor to build a particle type.
     *
     * @param name of the particle
     */
    CrateParticles(String name) {
        this.name = name;
    }

    /**
     * @return the particle name
     */
    public String getName() {
        return name;
    }
}