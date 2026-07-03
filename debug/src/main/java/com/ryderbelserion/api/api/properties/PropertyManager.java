package com.ryderbelserion.api.api.properties;

import com.ryderbelserion.api.api.properties.interfaces.IPropertyData;
import com.ryderbelserion.api.api.properties.objects.interfaces.Property;
import com.ryderbelserion.fusion.core.api.FusionProvider;
import com.ryderbelserion.fusion.files.FileManager;
import com.ryderbelserion.fusion.files.enums.FileType;
import com.ryderbelserion.fusion.kyori.FusionKyori;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.nio.file.Path;

public final class PropertyManager {

    private final FusionKyori fusion = (FusionKyori) FusionProvider.getInstance();

    private final FileManager fileManager = this.fusion.getFileManager();

    private CommentedConfigurationNode configuration;
    private final IPropertyData propertyData;
    private final FileType fileType;
    private final Path path;

    public PropertyManager(@NonNull final IPropertyData propertyData,
                           @NonNull final FileType fileType,
                           @NonNull final Path path
    ) {
        this.propertyData = propertyData;
        this.fileType = fileType;
        this.path = path;
    }

    public @NonNull <T> T getProperty(@NonNull final Property<T> property) {
        return this.propertyData.getProperty(this.configuration, property);
    }

    public <T> void setProperty(@NonNull final Property<T> property, @NonNull final T value) {
        this.propertyData.setProperty(this.configuration, property, value);
    }

    public void reload() {
        this.fileManager.reloadFile(this.path);
    }

    public void load() {
        this.fileManager.addFile(this.path, FileType.YAML).getYamlFile(this.path)
                .ifPresent(customFile -> this.configuration = customFile.getConfiguration());
    }

    public void save() {
        this.fileManager.saveFile(this.path);
    }

    public @NonNull FileType getFileType() {
        return this.fileType;
    }

    public @NonNull Path getPath() {
        return this.path;
    }
}