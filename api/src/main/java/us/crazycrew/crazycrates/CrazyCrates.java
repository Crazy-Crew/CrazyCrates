package us.crazycrew.crazycrates;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.resource.YamlFileResourceOptions;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.CrazyCratesService;
import us.crazycrew.crazycrates.api.ICrazyCrates;
import us.crazycrew.crazycrates.api.users.UserManager;
import us.crazycrew.crazycrates.platform.impl.ConfigKeys;
import us.crazycrew.crazycrates.platform.impl.messages.CommandKeys;
import us.crazycrew.crazycrates.platform.impl.messages.CrateKeys;
import us.crazycrew.crazycrates.platform.impl.messages.ErrorKeys;
import us.crazycrew.crazycrates.platform.impl.messages.MiscKeys;
import us.crazycrew.crazycrates.platform.impl.messages.PlayerKeys;
import us.crazycrew.crazycrates.platform.Server;
import java.io.File;

public class CrazyCrates implements ICrazyCrates {

    private final SettingsManager messages;

    private final SettingsManager config;

    private final Server server;

    public CrazyCrates(Server server) {
        // Create server object.
        this.server = server;

        // The builder options
        YamlFileResourceOptions builder = YamlFileResourceOptions.builder().indentationSize(2).build();

        // Create config.yml
        this.config = SettingsManagerBuilder
                .withYamlFile(new File(server.getFolder(), "config.yml"), builder)
                .useDefaultMigrationService()
                .configurationData(ConfigKeys.class)
                .create();

        // Create messages.yml
        this.messages = SettingsManagerBuilder
                .withYamlFile(new File(server.getFolder(), "messages.yml"), builder)
                .useDefaultMigrationService()
                .configurationData(MiscKeys.class, ErrorKeys.class, PlayerKeys.class, CrateKeys.class, CommandKeys.class)
                .create();

        // Register legacy provider
        CrazyCratesService.register(this);

        // Register provider.
        CrazyCratesProvider.register(this);
    }

    public void reload() {
        this.config.reload();

        this.messages.reload();
    }

    public void disable() {
        this.config.save();

        this.messages.save();

        // Unregister legacy provider.
        CrazyCratesService.unregister();

        // Unregister provider.
        CrazyCratesProvider.unregister();
    }

    public Server getServer() {
        return this.server;
    }

    public SettingsManager getConfig() {
        return this.config;
    }

    public SettingsManager getMessages() {
        return this.messages;
    }

    @NotNull
    @Override
    public UserManager getUserManager() {
        return this.server.getUserManager();
    }
}