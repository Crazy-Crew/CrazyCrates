package com.badbones69.crazycrates.api.enums.crates;

public enum CrateStatus {

    failed("Failed"),
    ended("Ended"),
    cycling("Cycling"),
    opened("Opened");

    private final String status;

    CrateStatus(final String status) {
        this.status = status;
    }

    public final String getStatus() {
        return this.status;
    }
}