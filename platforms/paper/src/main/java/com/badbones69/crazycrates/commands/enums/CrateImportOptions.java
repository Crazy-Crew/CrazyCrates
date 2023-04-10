package com.badbones69.crazycrates.commands.enums;

public enum CrateImportOptions {

    ADVANCED_CRATES("advanced_crates"),
    SPECIALIZED_CRATES("specialized_crates");

    final String name;

    CrateImportOptions(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}