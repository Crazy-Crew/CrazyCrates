package com.badbones69.crazycrates.api.enums;

public enum FillerType {

    FILL_TOP("top"),
    FILL_BOTTOM("bottom"),
    FILL_BORDER("border"),
    FILL_SIDE("side"),
    FILL("full");

    private final String type;

    FillerType(final String type) {
        this.type = type;
    }

    public final String getType() {
        return this.type;
    }

    public static FillerType getFromName(String name) {
        for (FillerType type : FillerType.values()) {
            if (type.getType().equalsIgnoreCase(name)) {
                return type;
            }
        }

        return null;
    }
}