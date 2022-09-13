package com.badbones69.crazycrates.common.enums.crates;

public enum KeyType {
    
    PHYSICAL_KEY("Physical_Key"),
    VIRTUAL_KEY("Virtual_Key"),
    FREE_KEY("Free_Key");

    /**
     * The blank key type.
     */
    private final String name;

    /**
     * @param name - The key type.
     */
    KeyType(String name) {
        this.name = name;
    }

    /**
     * @param type - The key type.
     * @return The key type we want
     */
    public KeyType getFromName(String type) {
        if (type.equalsIgnoreCase("virtual") || type.equalsIgnoreCase("v")) {
            return KeyType.VIRTUAL_KEY;
        } else if (type.equalsIgnoreCase("physical") || type.equalsIgnoreCase("p")) {
            return KeyType.PHYSICAL_KEY;
        } else if (type.equalsIgnoreCase("free") || type.equalsIgnoreCase("f")) {
            return KeyType.FREE_KEY;
        }

        return null;
    }

    /**
     * @return The name of the key type.
     */
    public String getName() {
        return name;
    }
}