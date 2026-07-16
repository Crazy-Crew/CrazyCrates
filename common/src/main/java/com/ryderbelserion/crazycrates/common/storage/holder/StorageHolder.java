package com.ryderbelserion.crazycrates.common.storage.holder;

import com.ryderbelserion.crazycrates.common.storage.impl.ConnectionFactory;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.CommentedConfigurationNode;
import us.crazycrew.crazycrates.api.enums.Files;
import java.util.UUID;

@NullMarked
public final class StorageHolder {

    private final ConnectionFactory factory;

    public StorageHolder(final ConnectionFactory factory) {
        this.factory = factory;
    }

    public StorageHolder init() {
        this.factory.init();

        return this;
    }

    public StorageHolder save() {
        this.factory.save();

        return this;
    }

    public void removeCrateLocation(final String id) {
        final CommentedConfigurationNode configuration = Files.locations.getConfiguration().node("Locations");

        if (!configuration.hasChild(id)) {
            return;
        }

        configuration.removeChild(id);

        Files.locations.save();
    }

    public String addCrateLocation(final String crateName, final String worldName, final int x, final int y, final int z) {
        return addCrateLocation(crateName, worldName, UUID.randomUUID().toString(), x, y, z);
    }

    public String addCrateLocation(final String crateName, final String worldName, final String id, final int x, final int y, final int z) {
        final CommentedConfigurationNode configuration = Files.locations.getConfiguration();

        final CommentedConfigurationNode section = configuration.node("Locations");

        setValue(section.node(id, "Crate"), String.class, crateName);
        setValue(section.node(id, "World"), String.class, worldName);
        setValue(section.node(id, "X"), Integer.class, x);
        setValue(section.node(id, "Y"), Integer.class, y);
        setValue(section.node(id, "Z"), Integer.class, z);

        Files.locations.save();

        return id;
    }

    private void setValue(final CommentedConfigurationNode section, final Class<?> type, final Object value) {
        try {
            section.set(type, value);
        } catch (final Exception exception) {
            exception.printStackTrace();
        }
    }
}