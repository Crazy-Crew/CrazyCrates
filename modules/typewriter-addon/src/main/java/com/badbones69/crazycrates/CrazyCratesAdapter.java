package com.badbones69.crazycrates;

import me.gabber235.typewriter.adapters.Adapter;
import me.gabber235.typewriter.adapters.TypewriteAdapter;

@Adapter(name = "crazycrates", description = "A crazycrates adapter for having key requirements.", version = "0.0.1")
public class CrazyCratesAdapter extends TypewriteAdapter {

    @Override
    public void initialize() {
        System.out.println("Beep");
    }

    @Override
    public void shutdown() {
        System.out.println("Beep");
    }
}