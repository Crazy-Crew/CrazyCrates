package com.badbones69.crazycrates.api.enums.types;

public enum KeyType {
    
    PHYSICAL_KEY("Physical_Key"),
    VIRTUAL_KEY("Virtual_Key"),
    FREE_KEY("Free_Key");
    
    private final String name;

    /**
     * A constructor to build a key type reference.
     *
     * @param name of the key-type
     */
    KeyType(String name) {
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
            return KeyType.VIRTUAL_KEY;
        } else if (type.equalsIgnoreCase("physical") || type.equalsIgnoreCase("p")) {
            return KeyType.PHYSICAL_KEY;
        } else if (type.equalsIgnoreCase("free") || type.equalsIgnoreCase("f")) {
            return KeyType.FREE_KEY;
        }

        return null;
    }

    /**
     * Get the name of the key type.
     *
     * @return the name of the key-type
     */
    public String getName() {
        return name;
    }
}