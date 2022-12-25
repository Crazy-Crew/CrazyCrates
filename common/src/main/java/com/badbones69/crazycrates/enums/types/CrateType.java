package com.badbones69.crazycrates.enums.types;

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
    WAR("War"),
    PARTICLES("Particles");
    
    private final String name;
    
    CrateType(String name) {
        this.name = name;
    }
    
    public static CrateType getFromName(String name) {
        for (CrateType crate : CrateType.values()) {
            if (crate.getName().equalsIgnoreCase(name)) return crate;
        }

        return null;
    }
    
    public String getName() {
        return name;
    }
}