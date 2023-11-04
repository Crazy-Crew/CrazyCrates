package us.crazycrew.crazycrates.api.enums.types;

public enum CrateType {

    menu("Menu"),
    cosmic("Cosmic"),
    crate_on_the_go("CrateOnTheGo"),
    csgo("CSGO"),
    fire_cracker("FireCracker"),
    quad_crate("QuadCrate"),
    quick_crate("QuickCrate"),
    roulette("Roulette"),
    wheel("Wheel"),
    wonder("Wonder"),
    war("War");
    
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
        return this.name;
    }
}