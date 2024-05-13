package us.crazycrew.crazycrates.api.enums.types;

/**
 * An enum class that contains all our current key types.
 *
 * @author Ryder Belserion
 * @version 0.5
 * @since 0.1
 */
public enum KeyType {

    /**
     * A physical key
     */
    physical_key("physical_key", "Physical Key"),
    /**
     * A virtual key
     */
    virtual_key("virtual_key", "Virtual Key"),
    /**
     * A free key
     */
    free_key("free_key", "Free Key");
    
    private final String name;
    private final String friendlyName;

    /**
     * A constructor to build a {@link KeyType} reference.
     *
     * @param name name of the {@link KeyType}
     * @since 0.1
     */
    KeyType(String name, String friendlyName) {
        this.friendlyName = friendlyName;
        this.name = name;
    }

    /**
     * Get the {@link KeyType} by name.
     *
     * @param type the name of the {@link KeyType}
     * @return the {@link KeyType}
     * @since 0.1
     */
    public static KeyType getFromName(String type) {
        if (type.isEmpty()) return KeyType.virtual_key;

        if (type.equalsIgnoreCase("virtual") || type.equalsIgnoreCase("v")) {
            return KeyType.virtual_key;
        } else if (type.equalsIgnoreCase("physical") || type.equalsIgnoreCase("p")) {
            return KeyType.physical_key;
        } else if (type.equalsIgnoreCase("free") || type.equalsIgnoreCase("f")) {
            return KeyType.free_key;
        }

        return KeyType.virtual_key;
    }

    /**
     * Get a message friendly version of the {@link KeyType}.
     *
     * @return the name of the {@link KeyType}
     * @since 0.1
     */
    public String getFriendlyName() {
        return this.friendlyName;
    }

    /**
     * Get the name of the {@link KeyType}.
     *
     * @return the name of the {@link KeyType}
     * @since 0.1
     */
    public String getName() {
        return this.name;
    }
}