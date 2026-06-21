package com.ryderbelserion.crazycrates.paper.api.managers;

import com.ryderbelserion.crazycrates.paper.CrazyCrates;
import com.ryderbelserion.crazycrates.paper.api.CratePlatform;
import com.ryderbelserion.crazycrates.paper.api.objects.prize.Prize;
import com.ryderbelserion.fusion.files.FileManager;
import com.ryderbelserion.fusion.files.types.configurate.YamlCustomFile;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.nio.file.Path;
import java.util.*;

public class PrizeManager {

    private final FileManager fileManager;
    private final CratePlatform platform;

    private final FusionPaper fusion;

    public PrizeManager(@NotNull final CrazyCrates plugin) {
        this.platform = plugin.getPlatform();

        this.fileManager = this.platform.getFileManager();
        this.fusion = this.platform.getFusion();
    }

    private final Map<String, Prize> prizes = new HashMap<>();

    public void load() {
        final Path folder = this.platform.getPrizesPath();

        this.prizes.clear();

        for (final Path path : this.fusion.getFilesByPath(folder, List.of(".yml"))) {
            final Optional<YamlCustomFile> optional = this.fileManager.getYamlFile(path);

            if (optional.isEmpty()) {
                continue;
            }

            final YamlCustomFile customFile = optional.get();

            final CommentedConfigurationNode configuration = customFile.getConfiguration();

            if (!configuration.hasChild("crate")) {
                continue;
            }

            final String name = customFile.getPrettyName();

            this.prizes.put(name, new Prize(configuration, name));
        }
    }

    public @NotNull final Optional<Prize> getPrize(@NotNull final String name) {
        return Optional.ofNullable(this.prizes.get(name));
    }

    public @NotNull final Map<String, Prize> getPrizes() {
        return Collections.unmodifiableMap(this.prizes);
    }
}