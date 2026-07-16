package com.ryderbelserion.crazycrates.common.storage.impl.file.types;

import com.ryderbelserion.crazycrates.common.storage.impl.file.FlatFactory;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.CommentedConfigurationNode;
import us.crazycrew.crazycrates.api.enums.Files;
import java.util.List;

@NullMarked
public class YamlFactory extends FlatFactory {

    public YamlFactory() {
        super("yaml");
    }

    @Override
    public void init() {}

    @Override
    public void stop() {}

    @Override
    public void save() {
        final CommentedConfigurationNode configuration = Files.locations.getConfiguration();

        if (!configuration.hasChild("Locations")) {
            try {
                configuration.node("Locations").setList(String.class, List.of());

                Files.locations.save();
            } catch (final Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}