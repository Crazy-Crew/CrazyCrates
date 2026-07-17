package com.ryderbelserion.crazycrates.common.storage.impl.file.types;

import com.ryderbelserion.crazycrates.common.CrazyCratesPlugin;
import com.ryderbelserion.crazycrates.common.enums.CrateStatus;
import com.ryderbelserion.crazycrates.common.objects.CrazyLocation;
import com.ryderbelserion.crazycrates.common.storage.impl.file.FlatFactory;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.CommentedConfigurationNode;
import us.crazycrew.crazycrates.api.enums.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@NullMarked
public class YamlFactory extends FlatFactory {

    public YamlFactory(final CrazyCratesPlugin plugin) {
        super(plugin, "yaml");
    }

    @Override
    public void removeCrateLocation(final String id) {
        final CommentedConfigurationNode configuration = Files.locations.getConfiguration().node("Locations");

        if (!configuration.hasChild(id)) {
            return;
        }

        configuration.removeChild(id);

        Files.locations.save();
    }

    @Override
    public Optional<CrazyLocation> getCrateLocation(final String id) {
        Optional<CrazyLocation> value = Optional.empty();

        for (final Map.Entry<CrateStatus, CrazyLocation> index : getCrateLocations().entrySet()) {
            final CrateStatus status = index.getKey();

            if (status.equals(CrateStatus.failed)) continue;

            final CrazyLocation location = index.getValue();

            if (!location.getId().equals(id)) continue;

            value = Optional.of(location);
        }

        return value;
    }

    @Override
    public Map<CrateStatus, CrazyLocation> getCrateLocations() {
        final Map<CrateStatus, CrazyLocation> locations = new HashMap<>();

        final CommentedConfigurationNode configuration = Files.locations.getConfiguration();

        final CommentedConfigurationNode section = configuration.node("Locations");

        for (final Map.Entry<Object, CommentedConfigurationNode> key : section.childrenMap().entrySet()) {
            final CommentedConfigurationNode index = key.getValue();
            final String id = key.getKey().toString();

            final String worldName = index.node("World").getString("");

            if (worldName.isBlank()) continue;

            final String crateName = index.node("Crate").getString("");

            if (crateName.isBlank()) continue;

            if (!index.hasChild("X") || !index.hasChild("Y") || !index.hasChild("Z")) {
                locations.put(CrateStatus.failed, new CrazyLocation(
                        crateName,
                        worldName,
                        id,
                        -1,
                        -1,
                        -1
                ));

                continue;
            }

            final int x = index.node("X").getInt();
            final int y = index.node("Y").getInt();
            final int z = index.node("Z").getInt();

            final CrazyLocation location = new CrazyLocation(crateName, worldName, id, x, y, z);

            if (!this.plugin.isCrateAvailable(crateName)) {
                locations.put(CrateStatus.unavailable, location);

                continue;
            }

            locations.put(CrateStatus.success, location);
        }

        return locations;
    }

    @Override
    public String addCrateLocation(final String crateName, final String worldName, final int x, final int y, final int z) {
        return addCrateLocation(crateName, worldName, UUID.randomUUID().toString(), x, y, z);
    }

    @Override
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

    @Override
    public void init() {}

    @Override
    public void stop() {}

    @Override
    public void save() {}

    private void setValue(final CommentedConfigurationNode section, final Class<?> type, final Object value) {
        try {
            section.set(type, value);
        } catch (final Exception exception) {
            exception.printStackTrace();
        }
    }
}