package com.badbones69.crazycrates.common.crates.quadcrates;

public enum CrateParticles {
    
    flame("flame"),
    villager_happy("villager happy"),
    spell_witch("spell witch"),
    redstone("redstone");
    
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
        return this.name;
    }
}