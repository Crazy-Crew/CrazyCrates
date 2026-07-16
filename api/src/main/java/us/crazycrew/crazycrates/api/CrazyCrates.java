package us.crazycrew.crazycrates.api;

import com.ryderbelserion.fusion.kyori.FusionKyori;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;
import us.crazycrew.crazycrates.api.adapters.sender.ISenderAdapter;
import us.crazycrew.crazycrates.api.config.impl.ConfigManager;
import us.crazycrew.crazycrates.platform.IServer;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@NullMarked
public abstract class CrazyCrates<C, S> implements IServer {

    public static final UUID CONSOLE_UUID = new UUID(0, 0);
    public static final String CONSOLE_NAME = "Console";
    public static final String namespace = "crazycrates";

    protected final Path path;

    public CrazyCrates(final Path path) {
        this.path = path;
    }

    public abstract List<String> getCrateFiles(final boolean removeExtension);

    public List<String> getCrateFiles() {
        return getCrateFiles(false);
    }

    @ApiStatus.Internal
    public abstract ISenderAdapter<C, S> getSenderAdapter();

    @ApiStatus.Internal
    public abstract ConfigManager getConfigManager();

    @ApiStatus.Internal
    public abstract FusionKyori getFusion();

    public abstract Path getCratesPath();

    public abstract Path getDataPath();

    @ApiStatus.Internal
    public abstract void loadMessages();

    public abstract void disable();

    public abstract void reload();

    public abstract void init();

}