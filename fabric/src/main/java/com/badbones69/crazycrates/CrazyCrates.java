package com.badbones69.crazycrates;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class CrazyCrates implements ModInitializer {

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStart);
        ServerLifecycleEvents.SERVER_STOPPED.register(this::onServerStop);
    }

    private void onServerStart(MinecraftServer server) {
        System.out.println("We have started!");
    }

    private void onServerStop(MinecraftServer server) {
        System.out.println("We have stopped!");
    }
}