package com.badbones69.crazycrates.fabric.server;

import net.fabricmc.api.DedicatedServerModInitializer;

public class CrazyCrates implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        System.out.println("Guten Tag!");
    }
}