package us.crazycrew.crazycrates.api.enums.types;

/**
 * An enum class that contains all our current key types.
 *
 * @author Ryder Belserion
 * @version 1.0-snapshot
 */
public enum KeyType {
    
    physical_key("physical_key", "Physical Key"),
    virtual_key("virtual_key", "Virtual Key"),
    free_key("free_key", "Free Key");
    
    private final String name;
    private final String friendlyName;

    /**
     * A constructor to build a key type reference.
     *
     * @param name of the key-type
     */
    KeyType(String name, String friendlyName) {
        this.friendlyName = friendlyName;
        this.name = name;
    }

    /**
     * Get the key type by name.
     *
     * @param type the name of the key-type
     * @return the type of key otherwise null
     */
    public static KeyType getFromName(String type) {
        if (type.equalsIgnoreCase("virtual") || type.equalsIgnoreCase("v")) {
            return KeyType.virtual_key;
        } else if (type.equalsIgnoreCase("physical") || type.equalsIgnoreCase("p")) {
            return KeyType.physical_key;
        } else if (type.equalsIgnoreCase("free") || type.equalsIgnoreCase("f")) {
            return KeyType.free_key;
        }

        return null;
    }

    /**
     * Get a message friendly version of the key type.
     *
     * @return the name of the key-type
     */
    public String getFriendlyName() {
        return this.friendlyName;
    }

    /**
     * Get the name of the key type.
     *
     * @return the name of the key-type
     */
    public String getName() {
        return this.name;
    }
}