package com.ryderbelserion.crazycrates;

import com.ryderbelserion.crazycrates.objects.Crate;
import com.ryderbelserion.fusion.core.utils.FileUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import java.nio.file.Path;

public class CrazyCrates extends JavaPlugin {

    @Override
    public void onEnable() {
        final ComponentLogger logger = getComponentLogger();

        final Path path = getDataPath();

        new FusionPaper(logger, path).enable(this);

        FileUtils.extract("crates", path, true, false);

        final YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(path.resolve("crates").resolve("Rakia.yml"))
                .build();

        CommentedConfigurationNode node = null;

        try {
            node = loader.load();
        } catch (final ConfigurateException exception) {
            logger.warn("Could not load Rakia.yml", exception);
        }

        if (node == null) return;

        try {
            final Crate crate = node.get(Crate.class);

            if (crate == null) return;

            logger.warn("Crate Name: {}", crate.getCrateName());
        } catch (final SerializationException exception) {
            logger.warn("Failed to serialize Rakia.yml", exception);
        }
    }
}