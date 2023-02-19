package us.crazycrew.crazycrates;

import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import net.dehya.ruby.RubyConfig;
import net.dehya.ruby.RubyCore;
import net.dehya.ruby.command.cloud.RubyCommand;
import net.dehya.ruby.player.RubyPlayerRegistry;
import net.dehya.ruby.registry.senders.types.Console;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class CratesBootStrap implements PluginBootstrap, RubyCore {

    private Path path;

    @Override
    public void bootstrap(@NotNull PluginProviderContext context) {
        this.path = context.getDataDirectory();
    }

    @Override
    public @NotNull Path getDirectory() {
        return this.path;
    }

    @Override
    public @NotNull Console getConsole() {
        return null;
    }

    @Override
    public @NotNull RubyPlayerRegistry getPlayerRegistry() {
        return null;
    }

    @Override
    public @NotNull List<RubyCommand> getCommands() {
        return null;
    }

    @Override
    public @NotNull String getPrefix() {
        return RubyConfig.PREFIX_LOGGER;
    }
}