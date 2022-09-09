package com.badbones69.crazycrates.common.enums.crates;

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

    /**
     * The blank crate type name.
     */
    private final String name;

    /**
     * @param name - The name of the crate.
     */
    CrateType(String name) {
        this.name = name;
    }

    /**
     * @param name - The name of the crate.
     * @return - The crate object.
     */
    public static CrateType getFromName(String name) {
        for (CrateType crate : CrateType.values()) {
            if (crate.getName().equalsIgnoreCase(name)) return crate;
        }

        return null;
    }

    /**
     * @return The name of the crate.
     */
    public String getName() {
        return name;
    }
}