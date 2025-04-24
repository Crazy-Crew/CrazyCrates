package com.ryderbelserion.crazycrates.objects;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class Crate {

    @Setting("Crate.CrateName")
    private String crateName;

    public void setCrateName(@NotNull final String crateName) {
        this.crateName = crateName;
    }

    public @NotNull String getCrateName() {
        return this.crateName;
    }
}