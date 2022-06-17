package com.badbones69.crazycrates.api.enums;

public enum KeyType {
    
    PHYSICAL_KEY("Physical_Key"),
    VIRTUAL_KEY("Virtual_Key"),
    FREE_KEY("Free_Key");
    
    private final String name;
    
    KeyType(String name) {
        this.name = name;
    }
    
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
    
    public String getName() {
        return name;
    }
    
}