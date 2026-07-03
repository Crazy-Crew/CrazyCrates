package com.ryderbelserion.api.api;

import com.ryderbelserion.api.api.properties.PropertyManager;
import com.ryderbelserion.api.api.properties.data.PropertyDataBuilder;
import com.ryderbelserion.api.api.properties.interfaces.IPropertyData;
import com.ryderbelserion.api.api.properties.interfaces.IPropertyHolder;
import com.ryderbelserion.fusion.files.enums.FileType;
import org.jspecify.annotations.NonNull;
import java.nio.file.Path;
import java.util.Arrays;

public final class ConfigBuilder {

    public static ConfigBuilder withYamlPath(final Path path) {
        return new ConfigBuilder(FileType.YAML, path);
    }

    private final FileType fileType;
    private final Path path;

    public ConfigBuilder(@NonNull final FileType fileType, @NonNull final Path path) {
        this.fileType = fileType;
        this.path = path;
    }

    private IPropertyData propertyData;

    @SafeVarargs
    public @NonNull final ConfigBuilder configuration(final Class<? extends IPropertyHolder>... properties) {
        this.propertyData = PropertyDataBuilder.createConfiguration(this.path, Arrays.asList(properties));

        return this;
    }

    public PropertyManager create() {
        final PropertyManager propertyManager = new PropertyManager(this.propertyData, this.fileType, this.path);

        propertyManager.load();

        return propertyManager;
    }

    public @NonNull Path getPath() {
        return this.path;
    }
}