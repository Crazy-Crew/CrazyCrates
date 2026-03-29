package us.crazycrew.crazycrates.api.enums;

/**
 * An enum class that contains all our current crate types.
 *
 * @author Ryder Belserion
 * @version 0.7.0
 * @since 0.1.0
 */
public enum CrateType {

    csgo("CSGO");
    
    private final String name;

    /**
     * A constructor to build a {@link CrateType} reference.
     *
     * @param name the name of the {@link CrateType}
     * @since 0.1.0
     */
    CrateType(String name) {
        this.name = name;
    }

    /**
     * Get the {@link CrateType} by name.
     *
     * @param name the name of the crate
     * @return {@link CrateType}
     * @since 0.1.0
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
     * @since 0.1.0
     */
    public String getName() {
        return this.name;
    }
}