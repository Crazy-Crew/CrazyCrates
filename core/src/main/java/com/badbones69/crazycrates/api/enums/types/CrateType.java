package com.badbones69.crazycrates.api.enums.types;

public enum CrateType {
    
    MENU("Menu"),
    COSMIC("Cosmic"),
    CRATE_ON_THE_GO("CrateOnTheGo"),
    CSGO("CSGO"),
    FIRE_CRACKER("FireCracker"),
    QUAD_CRATE("QuadCrate"),
    QUICK_CRATE("QuickCrate"),
    ROULETTE("Roulette"),
    WHEEL("Wheel"),
    WONDER("Wonder"),
    WAR("War");
    
    private final String name;

    /**
     * A constructor to build a crate type reference.
     *
     * @param name of the crate type
     */
    CrateType(String name) {
        this.name = name;
    }

    /**
     * Get the crate type by name.
     *
     * @param name of the crate
     * @return crate object otherwise null
     */
    public static CrateType getFromName(String name) {
        for (CrateType crate : CrateType.values()) {
            if (crate.getName().equalsIgnoreCase(name)) return crate;
        }

        return null;
    }

    /**
     * Get the name of the crate.
     *
     * @return the crate name
     */
    public String getName() {
        return name;
    }
}