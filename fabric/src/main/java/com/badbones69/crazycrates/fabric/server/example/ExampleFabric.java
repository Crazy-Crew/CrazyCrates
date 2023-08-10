package com.badbones69.crazycrates.fabric.server.example;

/*
import com.mojang.brigadier.CommandDispatcher;
import com.ryderbelserion.lexicon.fabric.FabricImpl;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.commands.CommandSourceStack;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ExampleFabric implements DedicatedServerModInitializer {

    private FabricImpl fabric;

    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            this.fabric = new FabricImpl(server);

            this.fabric.enable();

            //this.fabric.getManager().setNamespace("example").addCommand(new ExampleCommand());

            CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();

            dispatcher.register(
                    literal("foo")
                            .then(
                                    argument("bar", integer())
                                            .executes(c -> {
                                                c.getSource().sendMessage(this.fabric.color().parse("<red>Bar is</red> <yellow>" + getInteger(c, "bar") + "</yellow>"));
                                                return 1;
                                            })
                            )
                            .executes(c -> {
                                c.getSource().sendMessage(this.fabric.color().parse("<red>Guten Tag</red>"));
                                return 1;
                            })
            );
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            this.fabric.disable();
        });
    }

    public FabricImpl getFabric() {
        return this.fabric;
    }
}
 */