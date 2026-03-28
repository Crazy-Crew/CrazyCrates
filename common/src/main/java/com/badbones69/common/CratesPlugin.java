package com.badbones69.common;

import com.ryderbelserion.fusion.files.FileManager;
import com.ryderbelserion.fusion.files.enums.FileType;
import com.ryderbelserion.fusion.kyori.FusionKyori;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import com.badbones69.common.api.enums.FileKeys;
import com.badbones69.common.config.ConfigManager;
import us.crazycrew.crazycrates.CratesProvider;
import us.crazycrew.crazycrates.api.objects.Crates;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public abstract class CratesPlugin extends Crates {

    private final FusionKyori<Audience> fusion;
    protected final FileManager fileManager;

    public CratesPlugin(@NotNull final FusionKyori<Audience> fusion) {
        super(fusion.getDataPath());

        this.fusion = fusion;
        this.fileManager = this.fusion.getFileManager();
    }

    @Override
    public void init() {
        this.fusion.init();

        CratesProvider.register(this);

        try {
            Files.createDirectories(this.path);
        } catch (final IOException ignored) {}

        this.fileManager.addFolder(this.path.resolve("crates"), FileType.YAML)
                .addFolder(this.path.resolve("schematics"), FileType.NBT)
                .addFolder(this.path.resolve("guis"), FileType.YAML);

        List.of(
                //FileKeys.messages,
                //FileKeys.editor,
                //FileKeys.config,

                FileKeys.crates_log,
                FileKeys.keys_log,

                FileKeys.respin
        ).forEach(FileKeys::addFile);

        ConfigManager.load(this.path);
    }

    @Override
    public void post() {

    }

    @Override
    public void reload() {
        ConfigManager.refresh();
    }

    @Override
    public void stop() {
        CratesProvider.unregister();
    }

    @Override
    public @NotNull final List<String> getCrateFiles(final boolean hasExtension) {
        return this.fusion.getFilesByName("crates", this.path, ".yml", this.fusion.getDepth(), hasExtension);
    }
}