package com.badbones69.crazycrates.commands.crates.types.admin.crates.migrator.enums;

public enum MigrationType {
    MOJANG_MAPPED_SINGLE("MojangMappedSingle"),
    MOJANG_MAPPED_ALL("MojangMappedAll"),

    //MIGRATE_OLD_COMMANDS("MigrateOldCommands"),

    CRATES_DEPRECATED_ALL("CratesDeprecated"),

    EXCELLENT_CRATES("ExcellentCrates"),

    SPECIALIZED_CRATES("SpecializedCrates");

    private final String name;

    MigrationType(String name) {
        this.name = name;
    }

    public final String getName() {
        return this.name;
    }

    public static MigrationType fromName(final String name) {
        MigrationType type = null;

        for (MigrationType key : MigrationType.values()) {
            if (key.getName().equalsIgnoreCase(name)) {
                type = key;

                break;
            }
        }

        return type;
    }
}