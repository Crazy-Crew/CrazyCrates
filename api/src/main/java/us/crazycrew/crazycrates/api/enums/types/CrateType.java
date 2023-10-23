package us.crazycrew.crazycrates.api.enums.types;

public enum CrateType {

    menu("menu"),
    cosmic("cosmic"),
    crate_on_the_go("crateonthego"),
    csgo("csgo"),
    fire_cracker("firecracker"),
    quad_crate("quadcrate"),
    quick_crate("quickcrate"),
    roulette("roulette"),
    wheel("wheel"),
    wonder("wonder"),
    war("war");
    
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