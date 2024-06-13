package us.crazycrew.crazycrates.api.enums.types;

/**
 * An enum class that contains all our current crate types.
 *
 * @author Ryder Belserion
 * @version 0.7
 * @since 0.1
 */
public enum CrateType {

    /**
     * The menu i.e. /crates menu
     */
    menu("Menu"),
    /**
     * The cosmic crate type
     */
    cosmic("Cosmic"),
    /**
     * CrateOnTheGo which allows players to be given crates that can be opened anywhere instantly.
     */
    crate_on_the_go("CrateOnTheGo"),
    /**
     * CSGO Crate, A crate type that mimics the infamous CSGO Crate! SHOCKING
     */
    csgo("CSGO"),
    /**
     * Casino Crate like a slots machine in a casino.
     */
    casino("Casino"),
    /**
     * FireCracker, A simple variant of QuickCrate that launches fireworks.
     */
    fire_cracker("FireCracker"),
    /**
     * A crate that spawns a schematic in the physical world with 4 chests that spawn on each corner.
     */
    quad_crate("QuadCrate"),
    /**
     * A crate that opens up quickly
     */
    quick_crate("QuickCrate"),
    /**
     * It's a roulette crate.
     */
    roulette("Roulette"),
    /**
     * Spin the wheel!
     */
    wheel("Wheel"),
    /**
     * Makes you wonder what you're going to get!
     */
    wonder("Wonder"),
    /**
     * Definitely going to war over what you win.
     */
    war("War");
    
    private final String name;

    /**
     * A constructor to build a {@link CrateType} reference.
     *
     * @param name the name of the {@link CrateType}
     * @since 0.1
     */
    CrateType(String name) {
        this.name = name;
    }

    /**
     * Get the {@link CrateType} by name.
     *
     * @param name the name of the crate
     * @return {@link CrateType}
     * @since 0.1
     */
    public static CrateType getFromName(String name) {
        if (name.isEmpty()) return CrateType.csgo;

        for (CrateType crate : CrateType.values()) {
            if (crate.getName().equalsIgnoreCase(name)) return crate;
        }

        return CrateType.csgo;
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