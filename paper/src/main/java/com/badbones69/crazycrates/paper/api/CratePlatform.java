package com.badbones69.crazycrates.paper.api;

import com.badbones69.common.CratesPlugin;
import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.commands.BaseCommand;
import com.badbones69.crazycrates.paper.commands.types.admin.ReloadCommand;
import com.badbones69.crazycrates.paper.commands.types.admin.TestCommand;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.files.FileManager;
import com.ryderbelserion.fusion.paper.FusionPaper;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Map;

public class CratePlatform extends CratesPlugin {

    private final CrazyCrates plugin;
    private final FusionPaper fusion;
    private final long time;

    public CratePlatform(@NotNull final CrazyCrates plugin, @NotNull final FusionPaper fusion) {
        super(fusion);

        this.plugin = plugin;
        this.fusion = fusion;

        this.time = System.nanoTime();
    }

    private CrateManager crateManager;

    @Override
    public void init() {
        super.init();

        this.crateManager = new CrateManager(this, this.plugin);
        this.crateManager.load();

        this.fusion.log(Level.INFO, "Done ({time})!", Map.of(
                "{time}",
                "%.3fs".formatted((double) (System.nanoTime() - this.time) / 1.0E9D))
        );

        post();
    }

    @Override
    public void post() {
        super.post();

        final LifecycleEventManager<Plugin> eventManager = this.plugin.getLifecycleManager();

        eventManager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            LiteralArgumentBuilder<CommandSourceStack> root = new BaseCommand().registerPermissions().literal().createBuilder();

            List.of(
                    new ReloadCommand(),

                    // debug
                    new TestCommand()
            ).forEach(command -> root.then(command.registerPermissions().literal()));

            event.registrar().register(root.build(), "The base command for CrazyCrates");
        });
    }

    @Override
    public void reload() {
        super.reload();

        this.crateManager.load();
    }

    public @NotNull final CrateManager getCrateManager() {
        return this.crateManager;
    }

    public @NotNull final FileManager getFileManager() {
        return this.fileManager;
    }

    public @NotNull final FusionPaper getFusion() {
        return this.fusion;
    }
}